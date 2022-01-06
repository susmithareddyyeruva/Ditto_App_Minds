package com.ditto.splash.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.splash.ui.databinding.SplashActivityBinding
import core.data.model.SoftwareUpdateResult
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import core.ui.rxbus.RxBus
import core.ui.rxbus.RxBusEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

class SplashFragment : BaseFragment(),Utility.CustomCallbackDialogListener {
    private val viewModel: SplashViewModel by ViewModelDelegate()
    private lateinit var binding: SplashActivityBinding
    var versionResult: SoftwareUpdateResult? = null

    @Inject
    lateinit var loggerFactory: LoggerFactory
    var versionDisposable: CompositeDisposable? = null
    val logger: Logger by lazy {
        loggerFactory.create(SplashFragment::class.java.simpleName)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SplashActivityBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bottomNavViewModel.visibility.set(false)
        toolbarViewModel.visibility.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(true)
        toolbarViewModel.isShowActionMenu.set(false)
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    private fun handleEvent(event: SplashViewModel.Event) =
        lifecycleScope.launchWhenResumed {
            when (event) {
                is SplashViewModel.Event.NavigateToLogin -> {
                    if (findNavController()?.currentDestination?.id == com.ditto.login.ui.R.id.destination_splash) {
                        findNavController()?.navigate(R.id.action_splashActivity_to_LoginFragment)
                    } else {
                    }
                }
                is SplashViewModel.Event.NavigateToOnBoarding -> {
                    // navigate to onboarding
                    getUserDetails(false)
//              findNavController().navigate(R.id.action_splashActivity_to_VideoFragment)
                    if (findNavController()?.currentDestination?.id == com.ditto.login.ui.R.id.destination_splash)
                        findNavController()?.navigate(R.id.action_splashActivity_to_Onboarding) else {
                    }
                }
                is SplashViewModel.Event.NavigateToDashboard -> {
                    getUserDetails(false)
                    if (findNavController()?.currentDestination?.id == com.ditto.login.ui.R.id.destination_splash)
                        findNavController()?.navigate(R.id.action_splashActivity_to_HomeFragment) else {
                    }
                }
            }

        }

    private fun getUserDetails(isGuest: Boolean) {
        bottomNavViewModel.isGuestBase.set(isGuest)
        bottomNavViewModel.userEmailBase.set(viewModel.userEmail)
        bottomNavViewModel.userPhoneBase.set(viewModel.userPhone)
        bottomNavViewModel.userFirstNameBase.set(viewModel.userFirstName)
        bottomNavViewModel.userLastNameBase.set(viewModel.userLastName)
        bottomNavViewModel.subscriptionEndDateBase.set(viewModel.subscriptionEndDate)

    }

    override fun onResume() {
        super.onResume()
        listenVersionEvents()
    }
    private fun listenVersionEvents() {
        versionDisposable = CompositeDisposable()

        versionDisposable?.plusAssign(
            RxBus.listen(RxBusEvent.VersionReceived::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    lifecycleScope.launchWhenResumed {
                        versionResult = it.versionReceived
                        if (versionResult?.response?.criticalUpdate == true ||
                            versionResult?.response?.forceUpdate == true
                        ) {
                            showVersionPopup()
                        } else if (versionResult?.response?.versionUpdate == true) {
                            viewModel.continueToApp()
                        } else {
//                    Toast.makeText(context,"Your app is upto date!!",Toast.LENGTH_SHORT).show()
                            viewModel.continueToApp()
                        }
                    }
                })

        versionDisposable?.plusAssign(
            RxBus.listen(RxBusEvent.VersionErrorReceived::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    lifecycleScope.launchWhenResumed {
                        viewModel.continueToApp()
                    }
                })
    }
    private fun showVersionPopup() {
        var negativeText = versionResult?.response?.cancel!!
        var positiveText = versionResult?.response?.confirm!!
        Utility.getCommonAlertDialogue(
            requireContext(),
            versionResult?.response?.title!!,
            versionResult?.response?.body!!,
            negativeText,
            positiveText,
            this,
            Utility.AlertType.SOFTWARE_UPDATE,
            Utility.Iconype.WARNING
        )
    }

    override fun onPause() {
        super.onPause()
        versionDisposable?.clear()
        versionDisposable?.dispose()
    }

    override fun onStop() {
        super.onStop()
        versionDisposable?.clear()
        versionDisposable?.dispose()
    }

    override fun onCustomPositiveButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        val packageName = context?.packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
        requireActivity().finishAffinity()
    }

    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        if (versionResult?.response?.forceUpdate == true) {
            requireActivity().finishAffinity()
        } else {
            viewModel.continueToApp()
        }

    }
}