package com.ditto.home.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.ditto.home.ui.adapter.HomeAdapter
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.domain.model.PatternIdData
import com.example.home_ui.R
import com.example.home_ui.databinding.HomeFragmentBinding
import core.appstate.AppState
import core.data.model.SoftwareUpdateResult
import core.network.NetworkUtility
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import core.ui.rxbus.RxBus
import core.ui.rxbus.RxBusEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject


class HomeFragment : BaseFragment(), Utility.CustomCallbackDialogListener {

    private lateinit var job: Job
    @Inject
    lateinit var loggerFactory: LoggerFactory
    var versionResult: SoftwareUpdateResult? = null

    val logger: Logger by lazy {
        loggerFactory.create(HomeFragment::class.java.simpleName)
    }
    private val homeViewModel: HomeViewModel by ViewModelDelegate()
    lateinit var binding: HomeFragmentBinding
    var toolbar: Toolbar? = null
    var versionDisposable: CompositeDisposable? = null
    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(
            inflater
        ).also {
            it.viewModel = homeViewModel
            it.lifecycleOwner = this
        }
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        versionDisposable?.clear()
        versionDisposable?.dispose()
    }

    @SuppressLint("CheckResult")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("HOME", "onActivityCreated")
        bottomNavViewModel.visibility.set(false)
        bottomNavViewModel.refreshMenu(context)

        (activity as BottomNavigationActivity)?.refreshMenuItem()
        (activity as BottomNavigationActivity)?.setEmaildesc()
        if (AppState.getIsLogged()) {
            bottomNavViewModel.isGuestBase.set(false)
        } else {
            bottomNavViewModel.isGuestBase.set(true)
        }
        toolbarViewModel.isShowActionBar.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(true)
        (activity as BottomNavigationActivity).setToolbar()
        setHomeAdapter()
        if (!AppState.isShownCoachMark()) {
            bottomNavViewModel.isShownCoachMark.set(AppState.isShownCoachMark())
            bottomNavViewModel.coachmarkFlowFinished.observe(viewLifecycleOwner, {
                if(it){
                    loadHomeFragment()
                }
            })
        } else {
            loadHomeFragment()
        }
    }

    private fun loadHomeFragment() {
        homeViewModel.disposable += homeViewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (activity!= null && context!= null &&isAdded) {
                    handleEvent(it)
                }
            }

        if (NetworkUtility.isNetworkAvailable(context)) {
            homeViewModel.fetchTailornovaTrialPattern() // fetch trial pattern api from tailornova saving to db >> showing count also
            /*if (AppState.getIsLogged()) {
                homeViewModel.fetchData() // todo remove fetchData and uncomment above line
            } else {
                homeViewModel.fetchListOfTrialPatternFromInternalStorage()// fetching trial pattern from internal db >> setting count also
            }*/
        } else {
            if (AppState.getIsLogged()) {
                homeViewModel.fetchOfflineData() // offline >> fetching from DB >> fetch Demo pattern
            } else {
                homeViewModel.fetchListOfTrialPatternFromInternalStorage()// fetching trial pattern from internal db >> setting count also
            }
        }
    }

    private fun setEventForDeeplink() {
        arguments?.getString("DEEPLINK")?.let {

            when (it) {
                "HOME" -> {

                }
                "LIBRARY" -> {
                    logger.d("HOMESCREEN  :LIBRARY")
                    if (findNavController().currentDestination?.id == R.id.homeFragment) {
                        this.arguments?.clear();
                        findNavController().navigate(R.id.action_home_to_my_library)
                    }

                }
                "DETAIL" -> {
                    logger.d("HOMESCREEN  :DETAIL")
                    if (findNavController().currentDestination?.id == R.id.homeFragment) {
                        val designId = arguments?.getString("clickedID")
                        val orderNumber = arguments?.getString("clickedOrderNumber")
                        logger.d("HOME PATTERN ID =$designId")
                        val bundle = bundleOf(
                            "clickedTailornovaID" to designId, "clickedOrderNumber" to orderNumber,
                            "ISFROM" to "DEEPLINK"
                        )
                        this.arguments?.clear();
                        findNavController().navigate(
                            R.id.action_deeplink_to_patternDescriptionFragment,
                            bundle
                        )
                    }

                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        GlobalScope.launch {
            delay(500)
            (activity as BottomNavigationActivity).setToolbar()
            bottomNavViewModel.visibility.set(false)
            toolbarViewModel.isShowTransparentActionBar.set(true)
            toolbarViewModel.isShowActionBar.set(false)
        }
        listenVersionEvents()
        Log.d("HOME", "onResume")
        try {
            val pInfo: PackageInfo =
                context?.getPackageName()
                    ?.let { context?.getPackageManager()?.getPackageInfo(it, 0) }!!
            val version = pInfo.versionName
            println("================= version = ${version}")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }

    private fun listenVersionEvents() {
        versionDisposable = CompositeDisposable()
        versionDisposable?.plusAssign(
            RxBus.listen(RxBusEvent.checkVersion::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.isCheckVersion) {
                        !it.isCheckVersion
                        bottomNavViewModel.showProgress.set(true)
                        homeViewModel.versionCheck()
                    }
                })
        versionDisposable?.plusAssign(RxBus.listen(RxBusEvent.versionReceived::class.java)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {

                bottomNavViewModel.showProgress.set(false)
                versionResult = it.versionReceived
                showVersionPopup()

            })

        versionDisposable?.plusAssign(RxBus.listen(RxBusEvent.versionErrorReceived::class.java)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                bottomNavViewModel.showProgress.set(false)
                showAlert(it.versionerrorReceived)
            })
    }

    override fun onStop() {
        super.onStop()
        versionDisposable?.clear()
        versionDisposable?.dispose()
    }

    private fun showVersionPopup() {
        var negativeText = versionResult?.response?.cancel!!
        var positiveText = versionResult?.response?.confirm!!
        var status = Utility.Iconype.WARNING
        if (versionResult?.response?.version_update == false) {
            negativeText = ""
            positiveText = "OK"
            status = Utility.Iconype.SUCCESS
        }

        Utility.getCommonAlertDialogue(
            requireContext(),
            versionResult?.response?.title!!,
            versionResult?.response?.body!!,
            negativeText,
            positiveText,
            this,
            Utility.AlertType.SOFTWARE_UPDATE,
            status
        )

    }

    private fun handleEvent(event: HomeViewModel.Event) =
        when (event) {
            is HomeViewModel.Event.OnClickDitto -> {
                if (findNavController().currentDestination?.id == R.id.homeFragment) {
                    findNavController().navigate(R.id.action_home_to_buy_pattern)
                } else {
                    logger.d("OnClickBuyPattern failed")
                }
            }
            is HomeViewModel.Event.OnClickJoann -> {
                if (findNavController().currentDestination?.id == R.id.homeFragment) {
                    findNavController().navigate(R.id.action_homeFragment_to_ShopFragment)
                } else {
                    logger.d("OnClickJoann failed")
                }
            }
            is HomeViewModel.Event.OnClickMyPatterns -> {
                val list = homeViewModel.homeDataResponse.value?.prod

                if (findNavController().currentDestination?.id == R.id.homeFragment) {
                    val bundle = bundleOf(
                        "clickedID" to context?.let { Utility.getSharedPref(it) },
                        "isFrom" to "RESUME_RECENT", "PATTERNS" to list
                    )
                    findNavController().navigate(R.id.action_home_to_my_library, bundle)
                } else {
                    logger.d("OnClickResumeRecent failed")
                }
            }
            HomeViewModel.Event.OnClickTutorial -> {
                if (findNavController().currentDestination?.id == R.id.homeFragment) {
                    val bundle = bundleOf("isFromHome" to true)
                    findNavController().navigate(R.id.action_home_to_tutorial, bundle)
                } else {
                    logger.d("OnClickJoann failed")
                }
            }
            HomeViewModel.Event.OnResultSuccess -> {
                bottomNavViewModel.showProgress.set(false)
                if (recycler_view != null) {
                    (recycler_view.adapter as HomeAdapter).setListData(homeViewModel.homeItem)
                    (recycler_view.adapter as HomeAdapter).notifyDataSetChanged()
                }
                logger.d("PATTERNS=  :  $homeViewModel.homeDataResponse")
                setEventForDeeplink()
            }
            HomeViewModel.Event.OnShowProgress -> {
                bottomNavViewModel.showProgress.set(true)

            }
            HomeViewModel.Event.OnHideProgress -> {
                bottomNavViewModel.showProgress.set(false)


            }
            HomeViewModel.Event.OnResultFailed -> {
                bottomNavViewModel.showProgress.set(false)
                showAlert()

            }
            HomeViewModel.Event.NoInternet -> {
                bottomNavViewModel.showProgress.set(false)
                showAlert()
            }
            HomeViewModel.Event.OnTrialPatternSuccess -> {
                Log.d("Download", "OnTrialPatternSuccess")
                if (dowloadPermissonGranted()) {
                    if (!::job.isInitialized || !job.isActive) {
                        job = downloadTrialPattenImages()
                    } else {

                    }
                } else {
                    if (activity!= null && context!= null &&isAdded) {
                        requestPermissions(
                            REQUIRED_PERMISSIONS_DOWNLOAD,
                            REQUEST_CODE_PERMISSIONS_DOWNLOAD
                        )
                    }else{

                    }
                }
            }
            HomeViewModel.Event.OnImageDownloadComplete -> {

            }
        }

    private fun downloadTrialPattenImages() = GlobalScope.launch {
        homeViewModel.trialPatternData.forEach {
            val map = getPatternPieceListTailornova(it)
            Log.d("Download", "OnTrialPatternSuccess forEach >> ${it.patternName}")
            runBlocking {
                try {
                    homeViewModel.prepareDowloadList(
                        homeViewModel.imageFilesToDownload(
                            map,
                            it.patternName
                        ), it.patternName
                    )
                } catch (e: Throwable) {
                    Log.d("Download", "Erro! ${e.message}")
                }
            }
        }
    }


    private fun showAlert() {
        val errorMessage = homeViewModel.errorString.get() ?: ""
        if (requireContext() != null) {
            Utility.getCommonAlertDialogue(
                requireContext(),
                "",
                errorMessage,
                "",
                getString(R.string.str_ok),
                this,
                Utility.AlertType.NETWORK,
                Utility.Iconype.FAILED
            )
        }
    }

    private fun setHomeAdapter() {
        val adapter = HomeAdapter(requireContext())
        recycler_view.adapter = adapter
        adapter.viewModel = homeViewModel
    }

    override fun onCustomPositiveButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        if (versionResult?.response?.version_update == true) {
            val packageName = context?.packageName
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
        }
    }

    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {

        if (versionResult?.response?.force_update == true) {
            requireActivity().finishAffinity()
        }

    }

    private fun showAlert(versionerrorReceived: String) {
        Utility.getCommonAlertDialogue(
            requireContext(),
            "",
            versionerrorReceived,
            "",
            getString(com.ditto.menuitems_ui.R.string.str_ok),
            this,
            Utility.AlertType.NETWORK,
            Utility.Iconype.FAILED
        )
    }


    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 111
        private const val REQUEST_ACTIVITY_RESULT_CODE = 121
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.BLUETOOTH)
        private const val REQUEST_CODE_PERMISSIONS_DOWNLOAD = 131
        private val REQUIRED_PERMISSIONS_DOWNLOAD =
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
    }

    private fun dowloadPermissonGranted() = REQUIRED_PERMISSIONS_DOWNLOAD.all {
        context?.let { it1 ->
            ContextCompat.checkSelfPermission(
                it1, it
            )
        } == PackageManager.PERMISSION_GRANTED
    }


    fun getPatternPieceListTailornova(patternIdData: PatternIdData): HashMap<String, String> {
        var hashMap: HashMap<String, String> = HashMap<String, String>()
        hashMap[patternIdData.thumbnailImageName.toString()] =
            patternIdData.thumbnailImageUrl.toString()
        for (patternItem in patternIdData.selvages ?: emptyList()) {
            hashMap[patternItem.imageName.toString()] = patternItem.imageUrl.toString()
        }
        for (patternItem in patternIdData.patternPieces ?: emptyList()) {
            hashMap[patternItem.thumbnailImageName.toString()] =
                patternItem.thumbnailImageUrl.toString()
            hashMap[patternItem.imageName.toString()] = patternItem.imageUrl.toString()
            for (splicedImage in patternItem.splicedImages ?: emptyList()) {
                hashMap[splicedImage.imageName.toString()] = splicedImage.imageUrl.toString()
                hashMap[splicedImage.mapImageName.toString()] = splicedImage.mapImageUrl.toString()
            }
        }
        return hashMap
    }

    /**
     * [Function] Call back when user allow/deny the permission
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (dowloadPermissonGranted() && requestCode == REQUEST_CODE_PERMISSIONS_DOWNLOAD) {
            Log.d("onReqPermissionsResult", "permission granted")
            if (core.network.NetworkUtility.isNetworkAvailable(requireContext())) {
                if (!::job.isInitialized || !job.isActive) {
                    job = downloadTrialPattenImages()
                } else {

                }
            } else {
                Utility.getCommonAlertDialogue(
                    requireContext(),
                    "",
                    getString(com.ditto.mylibrary.ui.R.string.no_internet_available),
                    "",
                    getString(com.ditto.mylibrary.ui.R.string.str_ok),
                    this,
                    Utility.AlertType.NETWORK,
                    Utility.Iconype.FAILED
                )
            }
        } else {
            //checkSocketConnectionBeforeWorkspace()
            // todo need dialog to ask for permission
            Utility.getCommonAlertDialogue(
                requireContext(),
                "",
                "Without this permission you will not able to use this feature",
                "",
                getString(com.ditto.menuitems_ui.R.string.str_ok),
                this,
                Utility.AlertType.RUNTIMEPERMISSION,
                Utility.Iconype.NONE
            )
            //Toast.makeText(requireContext(), "Denied", Toast.LENGTH_SHORT)
            Log.d("onReqPermissionsResult", "permission denied")
        }

    }
}
