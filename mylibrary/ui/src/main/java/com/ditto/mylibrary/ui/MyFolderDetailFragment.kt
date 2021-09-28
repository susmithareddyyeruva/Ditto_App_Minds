package com.ditto.mylibrary.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.domain.model.FilterItems
import com.ditto.mylibrary.ui.adapter.MyFolderDetailListAdapter
import com.ditto.mylibrary.ui.databinding.MyfolderdetailfragmentBinding
import com.ditto.mylibrary.ui.util.PaginationScrollListener
import core.appstate.AppState
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

class MyFolderDetailFragment : BaseFragment(), Utility.CustomCallbackDialogListener {
    @Inject
    lateinit var loggerFactory: LoggerFactory
    private val viewModel: MyFolderViewModel by ViewModelDelegate()
    lateinit var binding: MyfolderdetailfragmentBinding

    val logger: Logger by lazy {
        loggerFactory.create(MyFolderDetailFragment::class.java.simpleName)
    }
    private val myFolderDetailListAdapter = MyFolderDetailListAdapter()
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    private var currentPage = 1
    lateinit var gridLayoutManager: GridLayoutManager
    private var tittle: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MyfolderdetailfragmentBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        return binding.folderdetailRoot
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            val args = arguments
            tittle = args?.getString("TITTLE", "")
            (parentFragment as MyLibraryFragment?)?.showFilterComponents()
            (parentFragment as MyLibraryFragment?)?.setToolbarTittle(
                tittle ?: ""
            )
            viewModel.myFolderDetailHeader = tittle ?: ""
    }

    @SuppressLint("FragmentBackPressedCallback")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUIEvents()
        val args = arguments
        viewModel.folderTitle = args?.getString("TITTLE", "")
        (parentFragment as MyLibraryFragment?)?.showFilterComponents()
        viewModel.folderTitle?.let { (parentFragment as MyLibraryFragment?)?.setToolbarTittle(it) }
        initializeAdapter()
        if (AppState.getIsLogged() && !Utility.isTokenExpired()) {
            viewModel.myfolderList.value = emptyList()
            myFolderDetailListAdapter.setListData(
                items = viewModel.myfolderList.value ?: emptyList()
            )
            bottomNavViewModel.showProgress.set(true)
            viewModel.fetchOnPatternData(
                viewModel.createJson(
                    currentPage,
                    value = ""
                )
            )  //Initial API call


        }

        binding.imageClearFilter.setOnClickListener {
            viewModel.resultmapFolder.clear()
            // viewModel.myfolderArryList.clear()
            viewModel.myfolderMenu.clear()
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
        viewModel.resultmapFolder.clear()
        // viewModel.myfolderArryList.clear()
        viewModel.myfolderMenu.clear()
        viewModel.setList()
        currentPage = 1
        isLastPage = false
        viewModel.fetchOnPatternData(viewModel.createJson(currentPage, value = ""))
    }

    fun callSearchResult(terms: String) {
        //   viewModel.resultmapFolder.clear()
        // viewModel.myfolderArryList.clear()
        // viewModel.myfolderMenu.clear()
        // viewModel.setList()
        currentPage = 1
        isLastPage = false
        viewModel.fetchOnPatternData(viewModel.createJson(currentPage, value = terms))
    }

    fun applyFilter() {
        if (AppState.getIsLogged() && !Utility.isTokenExpired()) {
            currentPage = 1
            isLastPage = false
            // viewModel.myfolderArryList.clear()
            bottomNavViewModel.showProgress.set(true)
            val menu = viewModel.myfolderMenu
            viewModel.fetchOnPatternData(viewModel.createJson(currentPage, value = ""))
        }

    }

    private fun updatePatterns() {
        // Updating the adapter
        myFolderDetailListAdapter.setListData(items = viewModel.myfolderList.value ?: emptyList())
        binding.tvFilterResult.text =
            context?.getString(R.string.text_filter_result, viewModel.totalPatternCount)
        (parentFragment as MyLibraryFragment?)?.onSetCount(getString(R.string.myfolder_detail_count,viewModel.folderTitle,viewModel.totalPatternCount))
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
        binding.rvMyfolderlist.layoutManager = gridLayoutManager
        binding.rvMyfolderlist.adapter = myFolderDetailListAdapter
        myFolderDetailListAdapter.viewModel = viewModel
        binding.rvMyfolderlist.addOnScrollListener(object :
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
    private fun handleEvent(event: MyFolderViewModel.Event) = when (event) {
        is MyFolderViewModel.Event.OnMyFolderItemClick -> {
            if (findNavController().currentDestination?.id == R.id.myLibraryFragment || findNavController().currentDestination?.id == R.id.myfolderFragment) {
                val bundle = bundleOf("clickedTailornovaID" to viewModel.clickedTailornovaID.get(),
                    "clickedOrderNumber" to viewModel.clickedOrderNumber.get())
                findNavController().navigate(
                    R.id.action_mylibrary_to_patternDescriptionFragment,
                    bundle
                )
            } else {
                logger.d("OnClickPatternDesc failed")
            }
        }
        is MyFolderViewModel.Event.OnMyFolderSearchClick -> {

            Log.d("pattern", " MyFolderDetailSearchClick")

        }
        is MyFolderViewModel.Event.MyFolderSyncClick -> {
            cleaFilterData()
            Log.d("pattern", "OnSyncClick : MyFolderDetail")

        }
        is MyFolderViewModel.Event.OnMyFolderResultSuccess -> {
            bottomNavViewModel.showProgress.set(false)
            baseViewModel.totalCount = viewModel.totalPatternCount
            (parentFragment as MyLibraryFragment?)?.setToolbarTittle(
                getString(
                    R.string.myfolder_detail_count,
                    tittle,
                    viewModel.totalPatternCount
                )
            )

            /**
             * Getting ALL PATTERNS LIST
             */
            isLoading = false
            updatePatterns()
        }
        is MyFolderViewModel.Event.OnMyFolderShowProgress -> {
            bottomNavViewModel.showProgress.set(true)
        }
        is MyFolderViewModel.Event.OnMyFolderHideProgress -> {
            bottomNavViewModel.showProgress.set(false)
        }
        is MyFolderViewModel.Event.OnMyFolderResultFailed, MyFolderViewModel.Event.NoInternet -> {
            bottomNavViewModel.showProgress.set(false)
            showAlert()
        }
        is MyFolderViewModel.Event.OnMyFolderUpdateFilterImage -> {
            (parentFragment as MyLibraryFragment?)?.onFilterApplied(true)
        }
        is MyFolderViewModel.Event.OnMyFolderUpdateDefaultFilter -> {
            (parentFragment as MyLibraryFragment?)?.onFilterApplied(false)

        }

        else -> {
            logger.d("undefined")

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

    override fun onCustomPositiveButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        logger.d("onCustomPositiveButtonClicked")

    }

    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        logger.d("onCustomNegativeButtonClicked")
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

    fun getMenuListItems(): HashMap<String, ArrayList<FilterItems>> {
        val item = viewModel.myfolderMenu
        return item
    }

}