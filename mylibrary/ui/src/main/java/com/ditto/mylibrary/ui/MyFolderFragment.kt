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
import androidx.recyclerview.widget.GridLayoutManager
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.ui.adapter.MyFolderAdapter
import com.ditto.mylibrary.ui.databinding.MyfolderfragmentBinding
import com.ditto.mylibrary.ui.util.Utility
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

class MyFolderFragment(val myFolderDetailFragment: MyFolderDetailFragment) : BaseFragment() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUIEvents()
        setAdapter()

    }

    private fun setUIEvents() {
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    private fun setAdapter() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 4)
        binding.rvMyFolder.layoutManager = gridLayoutManager
        val adapter = MyFolderAdapter(requireContext(), viewModel.getList())
        binding.rvMyFolder.adapter = adapter
        adapter.viewModel = viewModel
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun handleEvent(event: MyFolderViewModel.Event) =
        when (event) {
            is MyFolderViewModel.Event.OnCreateFolderClicked -> {
                val layout =
                    activity?.layoutInflater?.inflate(R.layout.create_folder, null)
                layout?.let {
                    Utility.createFolderAlertDialogForMyFolder(
                        requireActivity(),
                        "",
                        "",
                        it, viewModel,
                        "CANCEL",
                        "CREATE FOLDER",
                        object :
                            com.ditto.mylibrary.ui.util.Utility.CallbackCreateFolderDialogListener {
                            override fun onCreateClicked(foldername: String) {

                            }

                            override fun onCancelClicked() {

                            }
                        },
                        core.ui.common.Utility.AlertType.DEFAULT
                    )
                }

            }
            is MyFolderViewModel.Event.OnNavigtaionToFolderDetail -> {
                // Begin the transaction
                /* parentFragmentManager
                      // 3
                      .beginTransaction()
                      // 4
                      .add(binding.parentFragmentContainer.id, myFolderDetailFragment)
                      .addToBackStack("Detail")
                      // 5
                      .commit()
  */
                if (findNavController().currentDestination?.id == R.id.myLibraryFragment || findNavController().currentDestination?.id == R.id.myfolderFragment) {
                    val bundle = bundleOf("clickedID" to viewModel.clickedId.get())
                    //findNavController().navigate(binding.parentFragmentContainer.id)
                    findNavController().navigate(
                        R.id.action_destination_myfolder_detail,
                        bundle
                    )
                } else {

                }
            }

            else -> {
                Log.d("MyLibraryViewModel", "MyLibraryViewModel.Event undefined")

            }

        }

}