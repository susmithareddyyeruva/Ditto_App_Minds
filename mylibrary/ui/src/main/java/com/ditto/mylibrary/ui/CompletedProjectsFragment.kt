package com.ditto.mylibrary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.ui.databinding.CompletedProjectsFragmentBinding
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.my_library_fragment.*
import javax.inject.Inject

class CompletedProjectsFragment : BaseFragment() {

    @Inject
    lateinit var loggerFactory: LoggerFactory

    val logger: Logger by lazy {
        loggerFactory.create(ActiveProjectsFragment::class.java.simpleName)
    }
    private val viewModel: AllPatternsViewModel by ViewModelDelegate()
    lateinit var binding: CompletedProjectsFragmentBinding

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        binding = CompletedProjectsFragmentBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUIEvents()
       // viewModel.fetchOnPatternData(viewModel.createJson(1,value = ""))
    }

    private fun setUIEvents() {
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }
    
    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun handleEvent(event: AllPatternsViewModel.Event) =
        when (event) {
            is AllPatternsViewModel.Event.OnItemClick -> {
                if (findNavController().currentDestination?.id == R.id.myLibraryFragment) {
                    val bundle = bundleOf("clickedID" to viewModel.clickedTailornovaID.get())
                    findNavController().navigate(
                        R.id.action_allPatternsFragment_to_patternDescriptionFragment,
                        bundle
                    )
                } else {
                    logger.d("OnClickPatternDesc failed")
                }
            }



            is AllPatternsViewModel.Event.OnAddProjectClick -> {
                view_pager.setCurrentItem(2)
            }

            is AllPatternsViewModel.Event.OnOptionsClicked -> {
                logger.d("error,instruction error")
            }
            is AllPatternsViewModel.Event.OnAllPatternSyncClick -> {}
            is AllPatternsViewModel.Event.OnAllPatternSearchClick -> {}
            AllPatternsViewModel.Event.OnAllPatternResultSuccess ->{}
            AllPatternsViewModel.Event.OnAllPatternShowProgress -> {}
            AllPatternsViewModel.Event.OnAllPatternHideProgress -> {}
            AllPatternsViewModel.Event.OnAllPatternResultFailed -> {}
            AllPatternsViewModel.Event.NoInternet ->{}
            AllPatternsViewModel.Event.UpdateFilterImage -> {}
            AllPatternsViewModel.Event.UpdateDefaultFilter ->{}
            else ->{

            }
        }

}