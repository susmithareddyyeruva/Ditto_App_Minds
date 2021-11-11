package com.ditto.mylibrary.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
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
import core.network.NetworkUtility
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.create_folder.*
import kotlinx.android.synthetic.main.dialog_addfolder.*
import javax.inject.Inject


class AllPatternsFragment(
    private val setPatternCount: SetPatternCount,
    private val filterIconSetListener: FilterIconSetListener
) : BaseFragment(),
    Utility.CustomCallbackDialogListener,
    com.ditto.mylibrary.ui.util.Utility.CallbackCreateFolderDialogListener {


    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(AllPatternsFragment::class.java.simpleName)
    }

    private val viewModel: AllPatternsViewModel by ViewModelDelegate()
    lateinit var binding: AllPatternsFragmentBinding
    private var patternId: String = "0"
    private val allPatternAdapter = AllPatternsAdapter()
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

        Log.d("Testing", ">>>>>>   All Patterns onActivityCreated")
        initializeAdapter()

        binding.imageClearFilter.setOnClickListener {
            cleaFilterData()
        }

        binding.textviewClear.setOnClickListener {
            cleaFilterData()
        }
    }

    fun cleaFilterData() {
        viewModel.resultMap.clear()
        viewModel.patternList.value = ArrayList()
        viewModel.menuList.clear()
        viewModel.setList()
        currentPage = 1
        isLastPage = false
        viewModel.fetchOnPatternData(viewModel.createJson(currentPage, value = ""))
    }

    fun callSearchResult(terms: String) {
        /**
         * Search is Happened only in filtered results
         */
        //  viewModel.resultMap.clear()
        //viewModel.menuList.clear()
        // viewModel.setList()
        viewModel.patternList.value = ArrayList()
        currentPage = 1
        isLastPage = false
        viewModel.fetchOnPatternData(viewModel.createJson(currentPage, value = terms))
    }

    fun applyFilter() {
        if (AppState.getIsLogged() && !Utility.isTokenExpired()) {
            currentPage = 1
            isLastPage = false
            viewModel.patternList.value = ArrayList()
            bottomNavViewModel.showProgress.set(true)
            viewModel.isLoading.set(true)
            viewModel.fetchOnPatternData(viewModel.createJson(currentPage, value = ""))
        }

    }

    private fun updatePatterns() {
        // Updating the adapter
        allPatternAdapter.setListData(items = viewModel.patternList.value ?: emptyList())
        val count = String.format("%02d", viewModel.totalPatternCount)
        binding.tvFilterResult.text =
            getString(R.string.text_filter_result, count)
        bottomNavViewModel.showProgress.set(false)
        viewModel.isLoading.set(false)
        setPatternCount.onSetCount(
            getString(
                R.string.pattern_library_count,
                viewModel.totalPatternCount
            )
        )
    }

    private fun setUIEvents() {
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    override fun onResume() {
        super.onResume()
        Log.d("Testing", ">>>>>>   All Patterns  onResume ")
        viewModel.disposable = CompositeDisposable()
        setUIEvents()
        if (AppState.getIsLogged()) {
            if (NetworkUtility.isNetworkAvailable(context)) {
                if (!Utility.isTokenExpired()) {
                    if (viewModel.patternList.value.isNullOrEmpty()) {
                        Log.d("Testing", ">>>>>>   All Patterns fetchOnPatternData")
                        bottomNavViewModel.showProgress.set(true)
                        viewModel.isLoading.set(true)
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
                            filterIconSetListener.onFilterApplied(true)
                        } else
                            filterIconSetListener.onFilterApplied(false)
                    }
                }
            } else {
                bottomNavViewModel.showProgress.set(true)
                viewModel.isLoading.set(true)
                viewModel.fetchOfflinePatterns()
            }
        }else{
            bottomNavViewModel.showProgress.set(true)
            viewModel.isLoading.set(true)
            viewModel.fetchTrialPatterns()
        }

    }

    override fun onPause() {
        super.onPause()
        Log.d("Testing", ">>>>>>   All Patterns  onPause ")
        viewModel.disposable.clear()
        viewModel.disposable.dispose()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Testing", ">>>>>>   All Patterns  onDestroyView ")
    }


    private fun initializeAdapter() {
        gridLayoutManager = GridLayoutManager(requireContext(), 4)
        binding.recyclerViewPatterns.layoutManager = gridLayoutManager
        binding.recyclerViewPatterns.adapter = allPatternAdapter
        allPatternAdapter.setListData(emptyList())
        allPatternAdapter.viewModel = viewModel
        binding.recyclerViewPatterns.addOnScrollListener(object :
            PaginationScrollListener(gridLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                //you have to call loadmore items to get more data

                if (currentPage <= viewModel.totalPageCount) {
                    if (AppState.getIsLogged() && !Utility.isTokenExpired()) {
                        bottomNavViewModel.showProgress.set(true)
                        viewModel.isLoading.set(true)
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
                val bundle = bundleOf(
                    "clickedTailornovaID" to viewModel.clickedTailornovaID.get(),
                    "clickedOrderNumber" to viewModel.clickedOrderNumber.get(),//empty here
                    "product" to viewModel.clickedProduct,
                    "ISFROM" to "ALLPATTERN"
                )
                findNavController().navigate(
                    R.id.action_mylibrary_to_patternDescriptionFragment,
                    bundle
                )
            } else {
                logger.d("OnClickPatternDesc failed")
            }
        }

        is AllPatternsViewModel.Event.OnOptionsClicked -> {
            showPopupMenu(event.view, event.patternId)
        }

        is AllPatternsViewModel.Event.OnAllPatternSearchClick -> {
            logger.d("OnSearchClick : AllPatternsFragment")

        }
        is AllPatternsViewModel.Event.OnAllPatternSyncClick -> {
            if (AppState.getIsLogged()) {
                if (NetworkUtility.isNetworkAvailable(context)) {
                    cleaFilterData()
                } else {
                    viewModel.errorString.set(getString(R.string.no_internet_available))
                    showAlert()
                    viewModel.fetchOfflinePatterns()
                }
            } else {
                bottomNavViewModel.showProgress.set(true)
                viewModel.isLoading.set(true)
                viewModel.fetchTrialPatterns()
            }
            logger.d("OnSyncClick : AllPatternsFragment")

        }
        is AllPatternsViewModel.Event.OnAllPatternResultSuccess -> {
            baseViewModel.totalCount = viewModel.totalPatternCount
            setPatternCount.onSetCount(
                getString(
                    R.string.pattern_library_count,
                    viewModel.totalPatternCount
                )
            )
            /**
             * Getting ALL PATTERNS LIST
             */
            isLoading = false
            updatePatterns()
        }
        is AllPatternsViewModel.Event.OnAllPatternShowProgress -> {
            bottomNavViewModel.showProgress.set(true)
            viewModel.isLoading.set(true)
        }
        is AllPatternsViewModel.Event.OnAllPatternHideProgress -> {
            bottomNavViewModel.showProgress.set(false)
            viewModel.isLoading.set(false)
        }
        is AllPatternsViewModel.Event.OnAllPatternResultFailed, is AllPatternsViewModel.Event.NoInternet -> {
            showAlert()
            bottomNavViewModel.showProgress.set(false)
            viewModel.isLoading.set(false)
        }
        is AllPatternsViewModel.Event.OnAddProjectClick -> {
            logger.d("Add project")
        }

        is AllPatternsViewModel.Event.UpdateFilterImage -> {
            filterIconSetListener.onFilterApplied(true)
        }

        is AllPatternsViewModel.Event.OnCreateFolder -> {
            val layout =
                activity?.layoutInflater?.inflate(R.layout.create_folder, createFolderRoot)
            layout?.let {
                com.ditto.mylibrary.ui.util.Utility.createFolderAlertDialog(
                    requireActivity(),
                    "",
                    "",
                    it, viewModel,
                    getString(R.string.cancel_dialog),
                    getString(R.string.create_folder),
                    this@AllPatternsFragment,
                    Utility.AlertType.DEFAULT
                )
            }
        }
        is AllPatternsViewModel.Event.OnFolderCreated -> {
            viewModel.clickedProduct = null
            (parentFragment as MyLibraryFragment?)?.switchToMyFolderFragmentTab()
        }

        is AllPatternsViewModel.Event.UpdateDefaultFilter -> {
            filterIconSetListener.onFilterApplied(false)

        }
        is AllPatternsViewModel.Event.OnPopupClick -> {
            Log.d("Testing", ">>>>>>  OnPopupClick ")
            bottomNavViewModel.showProgress.set(false)
            viewModel.isLoading.set(false)
            /**
             * CREATE  FOLDER POP UP WITH  ALL FOLDERS CREATED ALONG WITH NEW FOLDER
             */
            logger.d("OnPopupClick")
            getAlertDialogFolder(
                requireActivity(), viewModel.folderMainList, viewModel
            )

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
            Utility.AlertType.NETWORK,
            Utility.Iconype.FAILED
        )
    }

    private fun showPopupMenu(view: View, patternId: String) {
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

    }

    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {

    }

    fun onSyncClick() {
        if (viewModel != null) {
            logger.d("onSyncClick : All Pattern")
            viewModel.onSyncClick()
        }
    }

    fun onSearchClick() {
        if (viewModel != null) {
            logger.d("onSearchClick : All Pattern")
            viewModel.onSearchClick()
        }
    }

    interface SetPatternCount {
        fun onSetCount(tittle: String)
    }

    interface FilterIconSetListener {
        fun onFilterApplied(isApplied: Boolean)
    }


    fun getMenuListItems(): HashMap<String, ArrayList<FilterItems>> {
        return viewModel.menuList
    }

    override fun onCreateClicked(newFolderName: String, parent: String) {
        /**
         * Pop up  for Create Folder
         */
        addFolder(newFolderName, parent)

    }

    override fun onCancelClicked() {
        logger.d("Cancel Clicked")
    }

    private fun addFolder(newFolderName: String, parent: String) {
        if (AppState.getIsLogged() && !Utility.isTokenExpired()) {
            viewModel.addToFolder(
                product = viewModel.clickedProduct,
                folderName = newFolderName
            )
        }
    }


}
