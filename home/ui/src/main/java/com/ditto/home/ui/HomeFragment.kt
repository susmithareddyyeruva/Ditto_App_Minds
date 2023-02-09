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
import core.ERROR_FETCH
import core.appstate.AppState
import core.data.model.SoftwareUpdateResult
import core.lib.BuildConfig
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


class HomeFragment : BaseFragment(), Utility.CustomCallbackDialogListener,
    Utility.CallbackDialogListener {

    private var isUiEventsDisposableSet: Boolean = false
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
        logger.d("HOME, onActivityCreated")
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
        /*if (!AppState.isShownCoachMark()) {
            bottomNavViewModel.isShownCoachMark.set(AppState.isShownCoachMark())
            bottomNavViewModel.coachmarkFlowFinished.observe(viewLifecycleOwner, {
                if (it) {
                    loadHomeFragment()
                }
            })
        } else {
            loadHomeFragment()
        }*/
        loadHomeFragment()
    }

    private fun loadHomeFragment() {
        if (homeViewModel.disposable.size() == 0) {
            homeViewModel.disposable += homeViewModel.events
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (activity != null && context != null && isAdded) {
                        handleEvent(it)
                    }
                    isUiEventsDisposableSet = true
                }

        }

        if (NetworkUtility.isNetworkAvailable(context)) {
//            homeViewModel.fetchTailornovaTrialPattern() // fetch trial pattern api from tailornova saving to db >> showing count also
            homeViewModel.fetchHomePagePatternOnline()
        } else {
            homeViewModel.fetchHomePagePatternOffline()
        }
    }

    private fun setEventForDeeplink() {
        arguments?.getString("DEEPLINK")?.let {

            when (it) {
                "HOME" -> {
                    logger.d("Deeplink argument : Home")
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
                        val mannequinId = arguments?.getString("mannequinId")
                        logger.d("HOME PATTERN ID =$designId")
                        val bundle = bundleOf(
                            "clickedTailornovaID" to designId,
                            "clickedOrderNumber" to orderNumber,
                            "mannequinId" to mannequinId,
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
        if (homeViewModel.disposable.size() == 0 && !isUiEventsDisposableSet) {
            homeViewModel.disposable = CompositeDisposable()
            homeViewModel.disposable += homeViewModel.events
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (activity != null && context != null && isAdded) {
                        handleEvent(it)
                    }
                }
        }

        GlobalScope.launch {
            delay(500)
            (activity as BottomNavigationActivity).setToolbar()
            bottomNavViewModel.visibility.set(false)
            toolbarViewModel.isShowTransparentActionBar.set(true)
            toolbarViewModel.isShowActionBar.set(false)
        }
        listenVersionEvents()
        logger.d("HOME, onResume")
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
            RxBus.listen(RxBusEvent.CheckVersion::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.isCheckVersion) {
                        !it.isCheckVersion
                        bottomNavViewModel.showProgress.set(true)
                        homeViewModel.versionCheck()
                    }
                })
        versionDisposable?.plusAssign(RxBus.listen(RxBusEvent.VersionReceived::class.java)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {

                bottomNavViewModel.showProgress.set(false)
                if (it.versionReceived.response.version != null) {
                    versionResult = it.versionReceived
                    showVersionPopup()
                } else {
                    homeViewModel.errorString.set(ERROR_FETCH) ?: ""
                    showAlert()
                }


            })

        versionDisposable?.plusAssign(RxBus.listen(RxBusEvent.VersionErrorReceived::class.java)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                bottomNavViewModel.showProgress.set(false)
                showAlert(it.versionerrorReceived)
            })
    }

    override fun onStop() {
        bottomNavViewModel.visibility.set(false)
        versionDisposable?.clear()
        versionDisposable?.dispose()
        homeViewModel.disposable.clear()
        isUiEventsDisposableSet = false
        super.onStop()
    }

    private fun showVersionPopup() {
        var negativeText = versionResult?.response?.cancel!!
        var positiveText = versionResult?.response?.confirm!!
        var status = Utility.Iconype.WARNING
        if (versionResult?.response?.versionUpdate == false) {
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
//                    findNavController().navigate(R.id.action_home_to_buy_pattern)
                    Utility.redirectToExternalBrowser(context, BuildConfig.DITTO_URL)
                } else {
                    logger.d("OnClickBuyPattern failed")
                }
            }
            is HomeViewModel.Event.OnClickJoann -> {
                if (findNavController().currentDestination?.id == R.id.homeFragment) {
//                    findNavController().navigate(R.id.action_homeFragment_to_ShopFragment)
                    Utility.redirectToExternalBrowser(context, BuildConfig.JOANN_URL)
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
            HomeViewModel.Event.OnResultFailed,HomeViewModel.Event.NoInternet -> {
                bottomNavViewModel.showProgress.set(false)
                showAlert()

            }
            HomeViewModel.Event.OnTrialPatternSuccess -> {
                logger.d("Download, OnTrialPatternSuccess")
                if (dowloadPermissonGranted()) {
                    if (!::job.isInitialized || !job.isActive) {
                        job = downloadTrialPattenImages()
                    } else {

                    }
                } else {
                    if (activity != null && context != null && isAdded) {
                        requestPermissions(
                            REQUIRED_PERMISSIONS_DOWNLOAD,
                            REQUEST_CODE_PERMISSIONS_DOWNLOAD
                        )
                    } else {

                    }
                }
            }
            HomeViewModel.Event.OnImageDownloadComplete -> {

            }
        }

    private fun downloadTrialPattenImages() = GlobalScope.launch {
        homeViewModel.trialPatternData.forEach {
            val map = getPatternPieceListTailornova(it)
            logger.d("Download, OnTrialPatternSuccess forEach >> ${it.patternName}")
            runBlocking {
                try {
                    homeViewModel.prepareDowloadList(
                        homeViewModel.imageFilesToDownload(
                            map,
                            it.patternName
                        ), it.patternName
                    )
                } catch (e: Throwable) {
                    logger.d("Download, Erro! ${e.message}")
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
        if (versionResult?.response?.versionUpdate == true) {
            val packageName = "com.jodito.ditto"
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

        if (versionResult?.response?.forceUpdate == true) {
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
        private val REQUIRED_PERMISSIONS_DOWNLOAD = emptyArray<String>()
//            arrayOf(
////                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            )
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
            logger.d("onReqPermissionsResult, permission granted")
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
            Utility.getAlertDialogue(
                requireContext(),
                getString(R.string.permissions_required),
                getString(R.string.storage_permissions_denied),
                getString(R.string.cancel),
                getString(R.string.go_to_settings),
                this,
                Utility.AlertType.PERMISSION_DENIED
            )
            //Toast.makeText(requireContext(), "Denied", Toast.LENGTH_SHORT)
            logger.d("onReqPermissionsResult, permission denied")
        }

    }

    override fun onPositiveButtonClicked(alertType: Utility.AlertType) {
        if (alertType.equals(Utility.AlertType.PERMISSION_DENIED)) {
            Utility.navigateToAppSettings(requireContext())
        }
    }

    override fun onNegativeButtonClicked(alertType: Utility.AlertType) {}

    override fun onNeutralButtonClicked(alertType: Utility.AlertType) {}
}
