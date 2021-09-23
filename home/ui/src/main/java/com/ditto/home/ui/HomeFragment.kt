package com.ditto.home.ui

import android.content.ActivityNotFoundException
import android.content.Intent
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
import core.ui.*
import core.ui.common.Utility
import core.ui.rxbus.RxBus
import core.ui.rxbus.RxBusEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.home_fragment.*
import javax.inject.Inject


class HomeFragment : BaseFragment(),Utility.CustomCallbackDialogListener  {

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
        homeViewModel.disposable += homeViewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    override fun onResume() {
        super.onResume()
        listenVersionEvents()
    }

    private fun listenVersionEvents() {
        versionDisposable = CompositeDisposable()
        versionDisposable?.plusAssign(RxBus.listen(RxBusEvent.checkVersion::class.java)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isCheckVersion){
                    !it.isCheckVersion
                    bottomNavViewModel.showProgress.set(true)
                    homeViewModel.versionCheck()
                }
            })
        versionDisposable?.plusAssign(RxBus.listen(RxBusEvent.versionReceived::class.java)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{

                bottomNavViewModel.showProgress.set(false)
                versionResult = it.versionReceived
                showVersionPopup()

            })

        versionDisposable?.plusAssign(RxBus.listen(RxBusEvent.versionErrorReceived::class.java)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                bottomNavViewModel.showProgress.set(false)
            })
    }

    override fun onStop() {
        super.onStop()
        versionDisposable?.clear()
        versionDisposable?.dispose()
    }

    private fun showVersionPopup() {
        Utility.getCommonAlertDialogue(
            requireContext(),
            getString(R.string.str_menu_softwareupdate),
            versionResult?.response?.body!!,
            versionResult?.response?.cancel!!,
            versionResult?.response?.confirm!!,
            this,
            Utility.AlertType.SOFTWARE_UPDATE
            ,
            Utility.Iconype.SUCCESS
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
                if (findNavController().currentDestination?.id == R.id.homeFragment) {
                        val bundle = bundleOf("clickedID" to context?.let { Utility.getSharedPref(it) },"isFrom" to "RESUME_RECENT")
                        findNavController().navigate(R.id.action_home_to_my_library,bundle)
                } else {
                    logger.d("OnClickResumeRecent failed")
                }
            }
            HomeViewModel.Event.OnClickTutorial -> {
                if (findNavController().currentDestination?.id == R.id.homeFragment) {
                    val bundle = bundleOf("isFromHome" to true)
                    findNavController().navigate(R.id.action_home_to_tutorial,bundle)
                } else {
                    logger.d("OnClickJoann failed")
                }
            }
        }

    private fun setHomeAdapter() {
        val adapter = HomeAdapter()
        recycler_view.adapter = adapter
        adapter.viewModel = homeViewModel
    }

    override fun onCustomPositiveButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        val  packageName = "com.joann.ditto"
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }
    }

    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {

    }

}
