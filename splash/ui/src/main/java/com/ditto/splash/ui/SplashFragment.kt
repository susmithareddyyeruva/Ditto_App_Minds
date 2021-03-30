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

    private fun handleEvent(event: SplashViewModel.Event) =
        when (event) {
            is SplashViewModel.Event.NavigateToLogin -> {
                findNavController().navigate(R.id.action_splashActivity_to_LoginFragment)
            }
            is SplashViewModel.Event.NavigateToOnBoarding -> {
                findNavController().navigate(R.id.action_splashActivity_to_Onboarding)
            }is SplashViewModel.Event.NavigateToDashboard -> {
                findNavController().navigate(R.id.action_splashActivity_to_HomeFragment)
            }
        }
}