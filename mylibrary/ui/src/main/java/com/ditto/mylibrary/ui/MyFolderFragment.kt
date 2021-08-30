package com.ditto.mylibrary.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.GridLayoutManager
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.ui.adapter.MyFolderAdapter
import com.ditto.mylibrary.ui.databinding.MyfolderfragmentBinding
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import javax.inject.Inject

class MyFolderFragment : BaseFragment() {

    @Inject
    lateinit var loggerFactory: LoggerFactory

    val logger: Logger by lazy {
        loggerFactory.create(MyLibraryFragment::class.java.simpleName)
    }

    private val viewModel: MyFolderViewModel by ViewModelDelegate()
    lateinit var binding: MyfolderfragmentBinding
    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        binding = MyfolderfragmentBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setAdapter()

    }

    private fun setAdapter() {
        val  gridLayoutManager = GridLayoutManager(requireContext(), 4)
        binding.rvMyFolder.layoutManager = gridLayoutManager
        val adapter=MyFolderAdapter(requireContext(), viewModel.getList())
        binding.rvMyFolder.adapter = adapter
        adapter.viewModel = viewModel
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun handleEvent(event: MyLibraryViewModel.Event) =
        when (event) {
            is MyLibraryViewModel.Event.completedProjects -> {
                Toast.makeText(context, "adadjhf", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Log.d("MyLibraryViewModel", "MyLibraryViewModel.Event undefined")

            }

        }

}