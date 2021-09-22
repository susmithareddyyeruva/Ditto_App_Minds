package com.ditto.splash.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.splash.ui.databinding.SplashActivityBinding
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

class SplashFragment : BaseFragment() {
    private val viewModel: SplashViewModel by ViewModelDelegate()
    private lateinit var binding: SplashActivityBinding

    @Inject
    lateinit var loggerFactory: LoggerFactory

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
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    override fun onResume() {
        super.onResume()
        viewModel.initialApiCall()
    }

    private fun handleEvent(event: SplashViewModel.Event) =
        when (event) {
            is SplashViewModel.Event.NavigateToLogin -> {
                if (findNavController().currentDestination?.id == com.ditto.login.ui.R.id.destination_splash) {
                    findNavController().navigate(R.id.action_splashActivity_to_LoginFragment)
                } else{}
            }
            is SplashViewModel.Event.NavigateToOnBoarding -> {
                // navigate to onboarding
                getUserDetails(false)
//              findNavController().navigate(R.id.action_splashActivity_to_VideoFragment)
                if (findNavController().currentDestination?.id == com.ditto.login.ui.R.id.destination_splash)
                    findNavController().navigate(R.id.action_splashActivity_to_Onboarding) else{}
            }
            is SplashViewModel.Event.NavigateToDashboard -> {
                getUserDetails(false)
                if (findNavController().currentDestination?.id == com.ditto.login.ui.R.id.destination_splash)
                    findNavController().navigate(R.id.action_splashActivity_to_HomeFragment) else{}
            }
        }

    private fun getUserDetails(isGuest : Boolean) {
        bottomNavViewModel.isGuestBase.set(isGuest)
        bottomNavViewModel.userEmailBase.set(viewModel.userEmail)
        bottomNavViewModel.userPhoneBase.set(viewModel.userPhone)
        bottomNavViewModel.userFirstNameBase.set(viewModel.userFirstName)
        bottomNavViewModel.userLastNameBase.set(viewModel.userLastName)
        bottomNavViewModel.subscriptionEndDateBase.set(viewModel.subscriptionEndDate)

    }
}