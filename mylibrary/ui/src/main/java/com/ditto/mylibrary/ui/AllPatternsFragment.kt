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
import com.ditto.mylibrary.ui.databinding.AllPatternsFragmentBinding
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import java.util.*
import javax.inject.Inject
import kotlin.Comparator

class AllPatternsFragment : BaseFragment() {

    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(AllPatternsFragment::class.java.simpleName)
    }

    private val viewModel: AllPatternsViewModel by ViewModelDelegate()
    lateinit var binding: AllPatternsFragmentBinding

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        binding = AllPatternsFragmentBinding.inflate(
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
        binding.recyclerViewPatterns.adapter = adapter
        adapter.viewModel = viewModel
        val patternData: List<MyLibraryData>? = viewModel.data.value?.filter { it.status == "New" }

        //for sorting
        Collections.sort(patternData,
            Comparator<MyLibraryData?> { lhs, rhs -> lhs!!.patternName.compareTo(rhs!!.patternName) })

        patternData?.let { adapter.setListData(it) }
    }

    private fun handleEvent(event: AllPatternsViewModel.Event) = when (event) {

        is AllPatternsViewModel.Event.OnItemClick -> {
            if (findNavController().currentDestination?.id == R.id.myLibraryFragment) {
                Log.d("Check Check ","--"+viewModel.clickedId.get())
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
        else -> {
            logger.d("OnClickPattern")
        }
    }
}
