package com.ditto.tutorial.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.tutorial.R
import com.ditto.tutorial.TUTORIAL
import com.ditto.tutorial.databinding.FragmentTutorialBinding
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class TutorialFragment : BaseFragment() {

    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(TutorialFragment::class.java.simpleName)
    }
    private val viewModel: TutorialViewModel by ViewModelDelegate()
    lateinit var binding: FragmentTutorialBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTutorialBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.tutorialroot
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setuptoolbar()
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleResponse(it)
            }
    }

    private fun handleResponse(event: TutorialViewModel.Event) =
        when (event) {
            is TutorialViewModel.Event.onItemClick -> {
                val bundle = bundleOf(
                    "InstructionId" to viewModel.clickID.get(),
                    "isFromHome" to true
                )
                if (findNavController().currentDestination?.id == R.id.destination_tutorial) {
                    navigateToBeamSetpOrCaliberation(bundle)
                }
                Unit
            }
            is TutorialViewModel.Event.onHowToClick -> {//Navigate to How to screen

                if (findNavController().currentDestination?.id == R.id.destination_tutorial) {
                    findNavController().navigate(
                        R.id.action_destination_tutorial_to_howto_navigation,
                        bundleOf(
                            "InstructionId" to viewModel.clickID.get(),
                            "isFromHome" to true
                        )
                    )
                }
                Unit
            }


        }

    private fun navigateToBeamSetpOrCaliberation(bundle: Bundle) {
        if (viewModel.clickID.get() == TUTORIAL.BEAMSETUP.id) {//Beam setup and Takedown
            if (findNavController().currentDestination?.id == R.id.destination_tutorial) {
                findNavController().navigate(
                    R.id.action_destination_tutorial_to_beamsetupFragment,
                    bundle
                )
            }
        } else {
            if (findNavController().currentDestination?.id == R.id.destination_tutorial) {
                findNavController().navigate(
                    R.id.action_destination_tutorial_to_calibrationFragment,
                    bundle
                )
            }
        }

    }

    private fun setuptoolbar() {
        toolbarViewModel.isShowActionBar.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(false)
        bottomNavViewModel.visibility.set(true)
    }
}
