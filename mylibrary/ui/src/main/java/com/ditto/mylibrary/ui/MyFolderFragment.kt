package com.ditto.mylibrary.ui

import android.os.Bundle
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
import core.network.NetworkUtility
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.create_folder.*
import kotlinx.android.synthetic.main.layout_rename.*
import javax.inject.Inject

class MyFolderFragment : BaseFragment(),
    core.ui.common.Utility.CustomCallbackDialogListener,
    Utility.CallbackCreateFolderDialogListener, MyFolderAdapter.OnRenameListener,
    MyFolderAdapter.OnDeleteClicked {

    private lateinit var myFolderDetailFragmentObj: MyFolderDetailFragment

    fun setMyFolderDetailFragmentObj(myFolderDetailFragment: MyFolderDetailFragment) {
        myFolderDetailFragmentObj = myFolderDetailFragment
    }

    @Inject
    lateinit var loggerFactory: LoggerFactory

    val logger: Logger by lazy {
        loggerFactory.create(MyLibraryFragment::class.java.simpleName)
    }
    private val viewModel: MyFolderViewModel by ViewModelDelegate()
    lateinit var binding: MyfolderfragmentBinding
    private var isDelete = false
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
        logger.d("Myfolder onActivityCreated")

    }

    override fun onResume() {
        super.onResume()
        if (AppState.getIsLogged()) {
            logger.d("Myfolder  onResume")
            viewModel.disposable = CompositeDisposable()
            setUIEvents()
            if (AppState.getIsLogged()) {
                /**
                 * API call for getting Folders List
                 */
                if (viewModel.folderList.isNullOrEmpty()) {
                    bottomNavViewModel.showProgress.set(true)
                    viewModel.getFoldersList()
                } else {
                    setAdapter()
                    (binding.rvMyFolder.adapter as MyFolderAdapter).notifyDataSetChanged()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        logger.d("Myfolder  onPause")
        viewModel.disposable.clear()
        viewModel.disposable.dispose()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logger.d("Myfolder onDestroyView")
    }

    fun onSyncClick() {
        if (viewModel != null) {
            if (AppState.getIsLogged() && NetworkUtility.isNetworkAvailable(context)) {
                logger.d("onSyncClick : MyFolder Fragment")
                bottomNavViewModel.showProgress.set(true)
                viewModel.getFoldersList()
            }
        }
    }

    private fun showAlert() {
        bottomNavViewModel.showProgress.set(false)
        val errorMessage = viewModel.errorString.get() ?: ""
        core.ui.common.Utility.getCommonAlertDialogue(
            requireContext(),
            "",
            errorMessage,
            "",
            getString(R.string.str_ok),
            this,
            core.ui.common.Utility.AlertType.NETWORK,
            core.ui.common.Utility.Iconype.FAILED
        )
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
        val adapter = MyFolderAdapter(
            requireContext(),
            viewModel.folderList,
            this@MyFolderFragment,
            this@MyFolderFragment
        )
        binding.rvMyFolder.adapter = adapter
        adapter.viewModel = viewModel
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun handleEvent(event: MyFolderViewModel.Event) =
        when (event) {
            is MyFolderViewModel.Event.OnMyFolderCreateFolderClicked -> {
                logger.d("Myfolder createFolderEvent")
                val layout =
                    activity?.layoutInflater?.inflate(R.layout.create_folder, createFolderRoot)
                layout?.let {
                    Utility.createFolderAlertDialogForMyFolder(
                        requireActivity(),
                        "",
                        "",
                        it, viewModel,
                        getString(R.string.cancel_dialog),
                        getString(R.string.create_folder),
                        this@MyFolderFragment,
                        core.ui.common.Utility.AlertType.DEFAULT
                    )
                }

            }
            is MyFolderViewModel.Event.OnNavigtaionToFolderDetail -> {
                (parentFragment as MyLibraryFragment).isFolderDetailsClicked = true
                logger.d("Myfolder OnNavigtaionToFolderDetail")
                val args = Bundle()
                args?.putString("TITTLE", viewModel?.clickedFolderName)
                myFolderDetailFragmentObj?.arguments = args
                parentFragmentManager
                    ?.beginTransaction()
                    ?.addToBackStack(null)
                    ?.replace(R.id.detail_nav_fragment, myFolderDetailFragmentObj, "DETAIL")
                    ?.commit()


            }
            is MyFolderViewModel.Event.OnMyFolderListUpdated -> {
                setAdapter()
                (binding.rvMyFolder.adapter as MyFolderAdapter).notifyDataSetChanged()
                bottomNavViewModel.showProgress.set(false)
            }
            is MyFolderViewModel.Event.OnNewFolderAdded, MyFolderViewModel.Event.OnFolderRemoved, MyFolderViewModel.Event.OnFolderRenamed -> {
                if (AppState.getIsLogged()) {
                    bottomNavViewModel.showProgress.set(true)
                    viewModel.folderList.clear()
                    viewModel.getFoldersList()
                } else {
                    logger.d("")
                }

            }
            is MyFolderViewModel.Event.OnMyFolderResultFailed, MyFolderViewModel.Event.NoInternet -> {
                bottomNavViewModel.showProgress.set(false)
                viewModel.isLoading.set(false)
                showAlert()
            }
            is MyFolderViewModel.Event.OnMyFolderShowAlert -> {
                viewModel.errorString.set("Folder already exists !")
                showAlert()
            }

            else -> {
                logger.d("onSyncClick : MyLibraryViewModel.Event undefined")

            }

        }

    override fun onCustomPositiveButtonClicked(
        iconype: core.ui.common.Utility.Iconype,
        alertType: core.ui.common.Utility.AlertType
    ) {
        when (alertType) {
            core.ui.common.Utility.AlertType.DELETE ->
                if (AppState.getIsLogged()) {
                    bottomNavViewModel.showProgress.set(true)
                    viewModel.addToFolder(
                        product = ProdDomain(),
                        newFolderName = "",
                        action = viewModel.delete
                    )
                }
            core.ui.common.Utility.AlertType.GUEST_MYFOLDER -> (parentFragment as MyLibraryFragment?)?.setTabsAdapter("GUEST")
            else -> {}
        }
    }

    override fun onCustomNegativeButtonClicked(
        iconype: core.ui.common.Utility.Iconype,
        alertType: core.ui.common.Utility.AlertType
    ) {

    }

    fun getFoldersList() {
        if (AppState.getIsLogged()) {
            bottomNavViewModel.showProgress.set(true)
            viewModel.folderList.clear()
            viewModel.getFoldersList()
        } else {

        }
    }

    override fun onCreateClicked(newFolderName: String, parent: String) {
        /**
         * API call for Rename Folder
         */
        if (AppState.getIsLogged()) {
            bottomNavViewModel.showProgress.set(true)
            viewModel.addToFolder(
                product = ProdDomain(),
                newFolderName = newFolderName,
                action = parent
            )

        }
    }

    override fun onCancelClicked() {
        logger.d("onCancelClicked")
    }

    override fun onRenameClicked(oldFolderName: String) {
        /**
         * Rename PopUp Displayed
         */
        viewModel.folderToRename = oldFolderName
        val layout =
            activity?.layoutInflater?.inflate(R.layout.layout_rename, renameRoot)
        layout?.let {
            Utility.renameFolderAlertDialog(
                requireActivity(),
                it,
                viewModel,
                getString(R.string.cancel_dialog),
                getString(R.string.rename_folder_dialog),
                this@MyFolderFragment,
                core.ui.common.Utility.AlertType.DEFAULT
            )
        }
    }

    override fun onDeleteClicked(title: String) {
        viewModel.folderToDelete = title
        isDelete = true
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

}