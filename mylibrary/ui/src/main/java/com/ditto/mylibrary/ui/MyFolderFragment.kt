package com.ditto.mylibrary.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
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

class MyFolderFragment(private val myFolderDetailFragment: MyFolderDetailFragment) : BaseFragment(),
    core.ui.common.Utility.CustomCallbackDialogListener {

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
        setUIEvents()
        bottomNavViewModel.showProgress.set(true)
        Handler(Looper.getMainLooper()).postDelayed({
            setAdapter()
        }, 2000) //millis

    }

    fun onSyncClick() {
        if (viewModel != null) {
            Log.d("MyFolder", "Sync")
            bottomNavViewModel.showProgress.set(true)
            Handler(Looper.getMainLooper()).postDelayed({
                setAdapter()
            }, 3500) //millis


        }
    }

    private fun setUIEvents() {
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    private fun setAdapter() {
        bottomNavViewModel.showProgress.set(false)
        val gridLayoutManager = GridLayoutManager(requireContext(), 4)
        binding.rvMyFolder.layoutManager = gridLayoutManager
        val adapter = MyFolderAdapter(
            requireContext(),
            viewModel.getList(),
            object : MyFolderAdapter.OnRenameListener {
                override fun onRenameClicked() {
                    val layout =
                        activity?.layoutInflater?.inflate(R.layout.layout_rename, null)
                    layout?.let {
                        Utility.renameFolderAlertDialog(
                            requireActivity(),
                            it,
                            viewModel,
                            "CANCEL",
                            "RENAME FOLDER",
                            object :
                                Utility.CallbackCreateFolderDialogListener {
                                override fun onCreateClicked(foldername: String) {

                                }

                                override fun onCancelClicked() {

                                }
                            },
                            core.ui.common.Utility.AlertType.DEFAULT
                        )
                    }
                }

            },
            object : MyFolderAdapter.OnDeleteClicked {
                override fun onDeleteClicked() {
                    core.ui.common.Utility.getCommonAlertDialogue(
                        requireContext(),
                        "",
                        getString(R.string.are_you_sure_delete),
                        getString(R.string.cancel_dialog),
                        getString(R.string.str_ok),
                        this@MyFolderFragment,
                        core.ui.common.Utility.AlertType.DELETE,
                        core.ui.common.Utility.Iconype.FAILED
                    )
                }
            })
        binding.rvMyFolder.adapter = adapter
        adapter.viewModel = viewModel
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun handleEvent(event: MyFolderViewModel.Event) =
        when (event) {
            is MyFolderViewModel.Event.OnMyFolderCreateFolderClicked -> {
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

                val args = Bundle()
                args?.putString("TITTLE", viewModel?.clickedFolderName)
                myFolderDetailFragment?.arguments = args
                parentFragmentManager
                    ?.beginTransaction()
                    .addToBackStack(null)
                    ?.replace(R.id.detail_nav_fragment, myFolderDetailFragment, "DETAIL")
                    ?.commit()
                println("FRAMENT ADDED" + childFragmentManager.fragments.size)

            }

            else -> {
                Log.d("MyLibraryViewModel", "MyLibraryViewModel.Event undefined")

            }

        }

    override fun onCustomPositiveButtonClicked(
        iconype: core.ui.common.Utility.Iconype,
        alertType: core.ui.common.Utility.AlertType
    ) {

    }

    override fun onCustomNegativeButtonClicked(
        iconype: core.ui.common.Utility.Iconype,
        alertType: core.ui.common.Utility.AlertType
    ) {

    }

}