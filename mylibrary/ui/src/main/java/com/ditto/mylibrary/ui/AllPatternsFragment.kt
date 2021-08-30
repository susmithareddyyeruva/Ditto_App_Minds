package com.ditto.mylibrary.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.domain.model.FilterItems
import com.ditto.mylibrary.ui.adapter.AllPatternsAdapter
import com.ditto.mylibrary.ui.databinding.AllPatternsFragmentBinding
import com.ditto.mylibrary.ui.util.PaginationScrollListener
import com.ditto.mylibrary.ui.util.Utility.Companion.getAlertDialogFolder
import core.appstate.AppState
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.search_dialog.*
import javax.inject.Inject


class AllPatternsFragment(
    private val setPatternCount: SetPatternCount,
    private val filterIcons: setFilterIcons
) : BaseFragment(),
    Utility.CustomCallbackDialogListener {


    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(AllPatternsFragment::class.java.simpleName)
    }

    private val viewModel: AllPatternsViewModel by ViewModelDelegate()
    lateinit var binding: AllPatternsFragmentBinding
    private var patternId: Int = 0
    private val allPatternAdapter = AllPatternsAdapter()
    private var clickedMenu: String = ""
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    private var currentPage = 1
    lateinit var gridLayoutManager: GridLayoutManager

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
        return binding.container

    }

    @SuppressLint("WrongConstant")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        AndroidInjection.inject(requireActivity())
        setUIEvents()
        initializeAdapter()
        if (AppState.getIsLogged() && !Utility.isTokenExpired()) {
            if (viewModel.patternArrayList.isEmpty()) {
                bottomNavViewModel.showProgress.set(true)
                viewModel.fetchOnPatternData(
                    viewModel.createJson(
                        currentPage,
                        value = ""
                    )
                )  //Initial API call
            } else {
                updatePatterns()
                //  setFilterMenuAdapter(0)
                if (viewModel.isFilter == true) {
                    filterIcons.onFilterApplied(true)
                } else
                    filterIcons.onFilterApplied(false)
            }


        }


        /*  getBackStackData<String>("KEY_SEARCH", true) { it ->
              logger.d("SEARCH TERM : $it")
              callSearchResult()
          }*/

        binding.imageClearFilter.setOnClickListener {
            viewModel.resultMap.clear()
            viewModel.patternArrayList.clear()
            viewModel.menuList.clear()
            viewModel.setList()
            currentPage = 1
            isLastPage = false
            viewModel.fetchOnPatternData(viewModel.createJson(currentPage, value = ""))
        }

        binding.textviewClear.setOnClickListener {
            cleaFilterData()
        }

    }

    fun cleaFilterData() {
        viewModel.resultMap.clear()
        viewModel.patternArrayList.clear()
        viewModel.menuList.clear()
        viewModel.setList()
        currentPage = 1
        isLastPage = false
        viewModel.fetchOnPatternData(viewModel.createJson(currentPage, value = ""))
    }

    private fun callSearchResult(terms: String) {
        viewModel.resultMap.clear()
        viewModel.patternArrayList.clear()
        viewModel.menuList.clear()
        viewModel.setList()
        currentPage = 1
        isLastPage = false
        viewModel.fetchOnPatternData(viewModel.createJson(currentPage, value = terms))
    }

    fun applyFilter() {
        if (AppState.getIsLogged() && !Utility.isTokenExpired()) {
            currentPage = 1
            isLastPage = false
            viewModel.patternArrayList.clear()
            bottomNavViewModel.showProgress.set(true)
            viewModel.fetchOnPatternData(viewModel.createJson(currentPage, value = ""))
        }

    }

    private fun updatePatterns() {
        // Updating the adapter
        allPatternAdapter.setListData(items = viewModel.patternArrayList)
        binding.tvFilterResult.text =
            getString(R.string.text_filter_result, viewModel.totalPatternCount)
    }

    private fun setUIEvents() {
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }


    private fun initializeAdapter() {
        gridLayoutManager = GridLayoutManager(requireContext(), 4)
        binding.recyclerViewPatterns.layoutManager = gridLayoutManager
        binding.recyclerViewPatterns.adapter = allPatternAdapter
        allPatternAdapter.viewModel = viewModel
        binding.recyclerViewPatterns?.addOnScrollListener(object :
            PaginationScrollListener(gridLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true;
                currentPage++
                //you have to call loadmore items to get more data

                if (currentPage <= viewModel.totalPageCount) {
                    if (AppState.getIsLogged() && !Utility.isTokenExpired()) {
                        bottomNavViewModel.showProgress.set(true)
                        viewModel.fetchOnPatternData(viewModel.createJson(currentPage, value = ""))
                    }
                } else {
                    isLastPage = true
                    isLoading = false
                    currentPage = 1
                }

            }
        })
    }
    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun handleEvent(event: AllPatternsViewModel.Event) = when (event) {

        is AllPatternsViewModel.Event.OnItemClick -> {
            if (findNavController().currentDestination?.id == R.id.myLibraryFragment || findNavController().currentDestination?.id == R.id.allPatternsFragment) {
                val bundle = bundleOf("clickedID" to viewModel.clickedId.get())
                findNavController().navigate(
                    R.id.action_mylibrary_to_patternDescriptionFragment,
                    bundle
                )
            } else {
                logger.d("OnClickPatternDesc failed")
            }
        }

        is AllPatternsViewModel.Event.OnDataUpdated -> {
            bottomNavViewModel.showProgress.set(false)
            setPatternCount.onSetCount(viewModel.totalPatternCount)

        }

        is AllPatternsViewModel.Event.OnOptionsClicked -> {
            showPopupMenu(event.view, event.patternId)
        }

        is AllPatternsViewModel.Event.OnSearchClick -> {
            //setPatternAdapter()
            Log.d("pattern", "OnSearchClick : AllPatternsFragment")
            if (findNavController().currentDestination?.id == R.id.myLibraryFragment) {
                val alertDialog = Dialog(
                    requireContext(),
                    R.style.DialogTheme
                )

                alertDialog.setContentView(R.layout.search_dialog);
                binding.viewModel = viewModel
                alertDialog.window?.setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
                );
                alertDialog.show()
                val watcher = alertDialog.editSearch.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        logger.d("afterTextChanged")
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        logger.d("beforeTextChanged")
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        logger.d("onTextChanged")
                        if (s.toString().isNotEmpty()) {
                            alertDialog.imageCloseSearch.visibility = View.VISIBLE
                        } else {
                            alertDialog.imageCloseSearch.visibility = View.GONE
                        }
                    }
                })
                alertDialog.tvCAncelDialog.setOnClickListener {
                    requireActivity().window
                        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    alertDialog.cancel()


                }

                alertDialog.editSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (alertDialog.editSearch.text.toString().isNotEmpty()) {
                            requireActivity().getWindow()
                                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            callSearchResult(alertDialog.editSearch.text.toString())
                            /*   setBackStackData(
                                   "KEY_SEARCH",
                                   alertDialog.editSearch.text.toString(),
                                   true
                               )*/
                            alertDialog.cancel()
                        } else
                            alertDialog.cancel()
                        //   targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK,  activity?.intent?.putExtras(bundle));

                        return@OnEditorActionListener true
                    }
                    false
                })
                alertDialog.imageCloseSearch.setOnClickListener {
                    alertDialog.editSearch.text?.clear()
                }
                //  findNavController().navigate(R.id.action_mylibrary_to_search)
            } else {
                Log.d("pattern", "OnSearchClick : ELSE")

            }
        }
        is AllPatternsViewModel.Event.OnSyncClick -> {
            cleaFilterData()
            Log.d("pattern", "OnSyncClick : AllPatternsFragment")

            Log.d("pattern", "onFilterClick : AllPatternsFragment")
            // open dialog
        }
        AllPatternsViewModel.Event.OnResultSuccess -> {
            bottomNavViewModel.showProgress.set(false)
            baseViewModel.totalCount = viewModel.totalPatternCount
            setPatternCount.onSetCount(viewModel.totalPatternCount)

            /**
             * Getting ALL PATTERNS LIST
             */
            isLoading = false
            updatePatterns()
        }
        AllPatternsViewModel.Event.OnShowProgress -> {
            bottomNavViewModel.showProgress.set(true)
        }
        AllPatternsViewModel.Event.OnHideProgress -> {
            bottomNavViewModel.showProgress.set(false)
        }
        AllPatternsViewModel.Event.OnResultFailed -> {
            bottomNavViewModel.showProgress.set(false)
            showAlert()
        }
        AllPatternsViewModel.Event.NoInternet -> {
            bottomNavViewModel.showProgress.set(false)
            showAlert()
        }
        /* else -> {
             Log.d("event", "Add project")
         }*/
        AllPatternsViewModel.Event.OnAddProjectClick -> {
            Log.d("event", "Add project")
        }
        AllPatternsViewModel.Event.OnUpdateFilter -> {
            Log.d("event", "OnUpdateFilter")

        }
        AllPatternsViewModel.Event.UpdateFilterImage -> {
            filterIcons.onFilterApplied(true)
        }
        AllPatternsViewModel.Event.OnCreateFolder -> {
            val layout =
                activity?.layoutInflater?.inflate(R.layout.create_folder, null)
            layout?.let {
                com.ditto.mylibrary.ui.util.Utility.createFolderAlertDialog(
                    requireActivity(),
                    resources.getString(com.ditto.workspace.ui.R.string.save_and_exit_dialog_title),
                    "",
                    it,
                   "CANCEL",
                  "CREATE FOLDER",
                    object : com.ditto.mylibrary.ui.util.Utility.CallbackCreateFolderDialogListener{
                        override fun onCreateClicked(projectName: String, isCompleted: Boolean?) {

                        }

                        override fun onCancelClicked() {

                        }
                    },
                    Utility.AlertType.DEFAULT
                )
            }
        }
        AllPatternsViewModel.Event.UpdateDefaultFilter -> {
            filterIcons.onFilterApplied(false)

        }
        is AllPatternsViewModel.Event.OnPopupClick -> {


            // open dialog
            val layout =
                activity?.layoutInflater?.inflate(R.layout.dialog_addfolder, null)
            layout?.let {
                getAlertDialogFolder(
                    requireActivity(),viewModel.folderMainList,viewModel,
                    object : com.ditto.workspace.ui.util.Utility.CallbackDialogListener {
                        override fun onSaveButtonClicked(
                            projectName: String,
                            isCompleted: Boolean?
                        ) {
                            Log.d("onSaveButtonClicked", "Allpattern")

                        }
                        override fun onExitButtonClicked() {
                            Log.d("onExitButtonClicked", "Allpattern")
                        }
                    },
                    Utility.AlertType.DEFAULT
                )
            }

        }
        else -> {

        }
    }


    private fun showAlert() {
        val errorMessage = viewModel.errorString.get() ?: ""
        Utility.getCommonAlertDialogue(
            requireContext(),
            "",
            errorMessage,
            "",
            getString(R.string.str_ok),
            this,
            Utility.AlertType.NETWORK
            ,
            Utility.Iconype.FAILED
        )
    }

    private fun showPopupMenu(view: View, patternId: Int) {
        this.patternId = patternId
        val popup = PopupMenu(requireContext(), view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.actions, popup.menu)
        popup.show()
    }

    override fun onCustomPositiveButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        //TODO("Not yet implemented")
    }

    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        // TODO("Not yet implemented")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (data?.data.toString() == "KEY_SEARCH") {
                Log.d("MAP  RESULT== ", "IF")
                //Re directing to Video Screen

            }
        }
    }


    fun onSyncClick() {
        if (viewModel != null) {
            Log.d("pattern", "onSyncClick : viewModel")
            viewModel.onSyncClick()
        }
    }

    fun onSearchClick() {
        if (viewModel != null) {
            Log.d("pattern", "onSearchClick : viewModel")
            viewModel.onSearchClick()
        }
    }

    interface SetPatternCount {
        fun onSetCount(totalPatternCount: Int)
    }

    interface setFilterIcons {
        fun onFilterApplied(isApplied: Boolean)
    }

    fun getMenuListItems(): HashMap<String, ArrayList<FilterItems>> {
        val item = viewModel.menuList
        return item
    }


}
