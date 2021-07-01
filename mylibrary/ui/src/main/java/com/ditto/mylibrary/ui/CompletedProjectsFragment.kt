package com.ditto.mylibrary.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.domain.model.MyLibraryData
import com.ditto.mylibrary.ui.adapter.PatternAdapter
import com.ditto.mylibrary.ui.databinding.CompletedProjectsFragmentBinding
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.my_library_fragment.*
import java.util.*
import javax.inject.Inject
import kotlin.Comparator

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
        viewModel.fetchOnPatternData()
    }

    private fun setUIEvents() {
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    private fun setPatternAdapter() {
        val adapter = PatternAdapter()
        binding.completedProjectsList.adapter = adapter
        adapter.viewModel = viewModel
        val patternData: List<MyLibraryData>? =
            viewModel.data.value?.filter { it.status == "Completed" }
        if (patternData?.isNotEmpty() == true) {
            binding.emptyText.visibility = View.GONE
        }

        //for sorting
        Collections.sort(patternData,
            Comparator<MyLibraryData?> { lhs, rhs -> lhs!!.patternName.compareTo(rhs!!.patternName) })

        patternData?.let { adapter.setListData(it) }
    }
    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun handleEvent(event: AllPatternsViewModel.Event) =
        when (event) {
            is AllPatternsViewModel.Event.OnItemClick -> {
                if (findNavController().currentDestination?.id == R.id.myLibraryFragment) {
                    val bundle = bundleOf("clickedID" to viewModel.clickedId.get())
                    findNavController().navigate(
                        R.id.action_allPatternsFragment_to_patternDescriptionFragment,
                        bundle
                    )
                } else {
                    logger.d("OnClickPatternDesc failed")
                }
            }

            is AllPatternsViewModel.Event.OnDataUpdated -> {
                setPatternAdapter()
            }

            is AllPatternsViewModel.Event.OnAddProjectClick -> {
                view_pager.setCurrentItem(2)
            }

            is AllPatternsViewModel.Event.OnOptionsClicked -> {
                Log.d("error","instruction error")
            }
            is AllPatternsViewModel.Event.OnFilterClick -> {TODO()}
            is AllPatternsViewModel.Event.OnSyncClick -> {TODO()}
            is AllPatternsViewModel.Event.OnSearchClick -> {TODO()}
            is AllPatternsViewModel.Event.OnLoadingStarts -> {TODO()}
            is AllPatternsViewModel.Event.OnLoadingCompleted -> {TODO()}
        }

}