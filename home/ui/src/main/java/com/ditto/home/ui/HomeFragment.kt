package com.ditto.home.ui

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.ditto.home.ui.adapter.HomeAdapter
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
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
import javax.inject.Inject


class HomeFragment : BaseFragment(), Utility.CustomCallbackDialogListener {

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
        setHomeAdapter()

        /**
         * API call for getting pattern details....
         */
        if (AppState.getIsLogged()) {
            if (NetworkUtility.isNetworkAvailable(context)) {
                bottomNavViewModel.showProgress.set(true)
                homeViewModel.fetchData()
                homeViewModel.fetchTailornovaTrialPattern()

            } else {
                homeViewModel.fetchOfflineData()
            }

        } else {
            homeViewModel.setHomeItems()
            if (recycler_view != null) {
                (recycler_view.adapter as HomeAdapter).setListData(homeViewModel.homeItem)
                (recycler_view.adapter as HomeAdapter).notifyDataSetChanged()
            }
        }

        homeViewModel.disposable += homeViewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }


    override fun onResume() {
        super.onResume()
        listenVersionEvents()
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
        }

    private fun showAlert() {
        val errorMessage = homeViewModel.errorString.get() ?: ""
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
}
