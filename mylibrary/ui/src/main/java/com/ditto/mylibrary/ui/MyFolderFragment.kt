package com.ditto.mylibrary.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.GridLayoutManager
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.domain.model.ProdDomain
import com.ditto.mylibrary.ui.adapter.MyFolderAdapter
import com.ditto.mylibrary.ui.databinding.MyfolderfragmentBinding
import com.ditto.mylibrary.ui.util.Utility
import core.appstate.AppState
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
        if (AppState.getIsLogged() && !core.ui.common.Utility.isTokenExpired()) {
            /**
             * API call for getting Folders List
             */
            bottomNavViewModel.showProgress.set(true)
            viewModel.getFoldersList()
        }
        setUIEvents()
    }

    fun onSyncClick() {
        if (viewModel != null) {
            Log.d("MyFolder", "Sync")
            bottomNavViewModel.showProgress.set(true)
            viewModel.getFoldersList()


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
            viewModel.folderList,
            object : MyFolderAdapter.OnRenameListener {
                override fun onRenameClicked(oldFolderName: String) {
                    /**
                     * Rename PopUp Displayed
                     */
                    viewModel.folderToRename = oldFolderName
                    val layout =
                        activity?.layoutInflater?.inflate(R.layout.layout_rename, null)
                    layout?.let {
                        Utility.renameFolderAlertDialog(
                            requireActivity(),
                            it,
                            viewModel,
                            getString(R.string.cancel_dialog),
                            getString(R.string.rename_folder_dialog),
                            object :
                                Utility.CallbackCreateFolderDialogListener {
                                override fun onCreateClicked(foldername: String, action: String) {
                                    /**
                                     * API call for Rename Folder
                                     */
                                    if (AppState.getIsLogged() && !core.ui.common.Utility.isTokenExpired()) {
                                        bottomNavViewModel.showProgress.set(true)
                                        viewModel.addToFolder(
                                            product = ProdDomain(),
                                            newFolderName = foldername,
                                            action = viewModel.rename
                                        )
                                    }

                                }

                                override fun onCancelClicked() {
                                    logger.d("onCancelClicked")

                                }
                            },
                            core.ui.common.Utility.AlertType.DEFAULT
                        )
                    }
                }

            },
            object : MyFolderAdapter.OnDeleteClicked {
                override fun onDeleteClicked(title: String) {
                    viewModel.folderToDelete = title
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
                        getString(R.string.cancel_dialog),
                        getString(R.string.create_folder),
                        object :
                            Utility.CallbackCreateFolderDialogListener {
                            override fun onCreateClicked(foldername: String, parent: String) {
                                if (AppState.getIsLogged() && !core.ui.common.Utility.isTokenExpired()) {
                                    bottomNavViewModel.showProgress.set(true)
                                    viewModel.folderList.clear()
                                    viewModel.addToFolder(
                                        product = ProdDomain(),
                                        newFolderName = foldername,
                                        action = parent
                                    )
                                }
                            }

                            override fun onCancelClicked() {
                                logger.d("onCancelClicked")


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


            }
            is MyFolderViewModel.Event.OnMyFolderListUpdated -> {
                setAdapter()
                (binding.rvMyFolder.adapter as MyFolderAdapter).notifyDataSetChanged()
                bottomNavViewModel.showProgress.set(false)
            }
            is MyFolderViewModel.Event.OnNewFolderAdded, MyFolderViewModel.Event.OnFolderRemoved, MyFolderViewModel.Event.OnFolderRenamed -> {
                if (AppState.getIsLogged() && !core.ui.common.Utility.isTokenExpired()) {
                    bottomNavViewModel.showProgress.set(true)
                    viewModel.folderList.clear()
                    viewModel.getFoldersList()
                } else {
                    logger.d("")

                }

            }
            else -> {
                Log.d("MyLibraryViewModel", "MyLibraryViewModel.Event undefined")

            }

        }

    override fun onCustomPositiveButtonClicked(
        iconype: core.ui.common.Utility.Iconype,
        alertType: core.ui.common.Utility.AlertType
    ) {
        if (AppState.getIsLogged() && !core.ui.common.Utility.isTokenExpired()) {
            bottomNavViewModel.showProgress.set(true)
            viewModel.addToFolder(
                product = ProdDomain(),
                newFolderName = "",
                action = viewModel.delete
            )
        }

    }

    override fun onCustomNegativeButtonClicked(
        iconype: core.ui.common.Utility.Iconype,
        alertType: core.ui.common.Utility.AlertType
    ) {

    }

    fun getFoldersList() {
        if (AppState.getIsLogged() && !core.ui.common.Utility.isTokenExpired()) {
            bottomNavViewModel.showProgress.set(true)
            viewModel.folderList.clear()
            viewModel.getFoldersList()
        } else {

        }
    }

}