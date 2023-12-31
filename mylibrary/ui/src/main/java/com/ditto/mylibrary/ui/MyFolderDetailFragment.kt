package com.ditto.mylibrary.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.domain.model.FilterItems
import com.ditto.mylibrary.domain.model.ProdDomain
import com.ditto.mylibrary.ui.adapter.MyFolderDetailListAdapter
import com.ditto.mylibrary.ui.databinding.MyfolderdetailfragmentBinding
import com.ditto.mylibrary.ui.util.PaginationScrollListener
import core.appstate.AppState
import core.network.NetworkUtility
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

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
        logger.d("***********onViewCreated")
        val args = arguments
        tittle = args?.getString("TITTLE", "")
        logger.d("***********$tittle")
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
        logger.d("***********${viewModel.folderTitle}")
        (parentFragment as MyLibraryFragment?)?.showFilterComponents()
        viewModel.folderTitle?.let { (parentFragment as MyLibraryFragment?)?.setToolbarTittle(it) }
        initializeAdapter()
        binding.imageClearFilter.setOnClickListener {
            cleaFilterDataWithApi()
        }

        binding.textviewClear.setOnClickListener {
            cleaFilterDataWithApi()
        }


    }

    override fun onResume() {
        super.onResume()
        logger.d("***********onResume")
        viewModel.disposable = CompositeDisposable()
        if (AppState.getIsLogged()) {
            /* viewModel.myfolderList.value = emptyList()
             myFolderDetailListAdapter.setListData(
                 items = viewModel.myfolderList.value ?: emptyList()
             )*/
            bottomNavViewModel.showProgress.set(true)
            viewModel.isLoading.set(true)
            if ((parentFragment as MyLibraryFragment).isFolderDetailsClicked) {
                cleaFilterDataWithApi()
                (parentFragment as MyLibraryFragment).isFolderDetailsClicked = false
            } else {
                if (viewModel.myfolderList.value.isNullOrEmpty()) {
                    viewModel.fetchOnPatternData(viewModel.createJson(currentPage, value = ""))
                } else {
                    updatePatterns(viewModel.myfolderList)
                }
            }
            //updatePatterns(viewModel.myfolderList)
        }
    }

    override fun onPause() {
        super.onPause()
        logger.d("***********onPause")
        viewModel.disposable.clear()
        viewModel.disposable.dispose()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    fun clearFilterData() {
        viewModel.resultmapFolder.clear()
        // viewModel.myfolderArryList.clear()
        viewModel.myfolderMenu.clear()
        viewModel.setList()
        currentPage = 1
        isLastPage = false
    }

    fun cleaFilterDataWithApi() {
        viewModel.resultmapFolder.clear()
        // viewModel.myfolderArryList.clear()
        viewModel.myfolderList.value = ArrayList()
        viewModel.myfolderMenu.clear()
        viewModel.setList()
        currentPage = 1
        isLastPage = false
        viewModel.fetchOnPatternData(viewModel.createJson(currentPage, value = ""))
        (parentFragment as MyLibraryFragment?)?.hideSearchLayout()
    }

    fun callSearchResult(terms: String) {
        //   viewModel.resultmapFolder.clear()
        // viewModel.myfolderArryList.clear()
        // viewModel.myfolderMenu.clear()
        // viewModel.setList()
        viewModel.myfolderList.value = ArrayList()
        currentPage = 1
        isLastPage = false
        viewModel.fetchOnPatternData(viewModel.createJson(currentPage, value = terms))
    }

    fun applyFilter() {
        if (AppState.getIsLogged() ) {
            currentPage = 1
            isLastPage = false
            // viewModel.myfolderArryList.clear()
            viewModel.myfolderList.value = ArrayList()
            bottomNavViewModel.showProgress.set(true)
            viewModel.isLoading.set(true)
            viewModel.fetchOnPatternData(viewModel.createJson(currentPage, value = ""))
        }

    }

    private fun updatePatterns(myfolderList: MutableLiveData<List<ProdDomain>>) {
        bottomNavViewModel.showProgress.set(false)
        // Updating the adapter
        myFolderDetailListAdapter.setListData(items = myfolderList.value ?: emptyList())
        val count = String.format("%02d", viewModel.totalPatternCount)
        binding.tvFilterResult.text =
            context?.getString(R.string.text_filter_result, count)
        (parentFragment as MyLibraryFragment?)?.onSetCount(
            getString(
                R.string.myfolder_detail_count,
                viewModel.folderTitle,
                viewModel.totalPatternCount
            )
        )
        if (viewModel.isFilterResult.get()) {
            (parentFragment as MyLibraryFragment?)?.onFilterApplied(true)
        } else {
            (parentFragment as MyLibraryFragment?)?.onFilterApplied(false)
        }
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
                    if (AppState.getIsLogged()) {
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
    private fun handleEvent(event: MyFolderViewModel.Event) = when (event) {
        is MyFolderViewModel.Event.OnMyFolderItemClick -> {
            if (findNavController().currentDestination?.id == R.id.myLibraryFragment || findNavController().currentDestination?.id == R.id.myfolderFragment) {
                val bundle = bundleOf(
                    "clickedTailornovaID" to viewModel.clickedTailornovaID.get(),
                    "clickedOrderNumber" to viewModel.clickedOrderNumber.get(),
                    "product" to viewModel.clickedProduct,
                )
                findNavController().navigate(
                    R.id.action_mylibrary_to_patternDescriptionFragment,
                    bundle
                )
            } else {
                logger.d("OnClickPatternDesc failed")
            }
        }
        is MyFolderViewModel.Event.OnMyFolderSearchClick -> {

            logger.d("pattern,  MyFolderDetailSearchClick")

        }
        is MyFolderViewModel.Event.MyFolderSyncClick -> {
            cleaFilterDataWithApi()
            logger.d("pattern, OnSyncClick : MyFolderDetail")

        }
        is MyFolderViewModel.Event.OnMyFolderResultSuccess -> {
            logger.d("***********OnMyFolderResultSuccess")
            bottomNavViewModel.showProgress.set(false)
            viewModel.isLoading.set(false)
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
            updatePatterns(viewModel.myfolderList)
        }
        is MyFolderViewModel.Event.OnMyFolderShowProgress -> {
            bottomNavViewModel.showProgress.set(true)
            viewModel.isLoading.set(true)
        }
        is MyFolderViewModel.Event.OnMyFolderHideProgress -> {
            bottomNavViewModel.showProgress.set(false)
            viewModel.isLoading.set(false)
        }
        is MyFolderViewModel.Event.OnMyFolderResultFailed, MyFolderViewModel.Event.NoInternet -> {
            bottomNavViewModel.showProgress.set(false)
            viewModel.isLoading.set(false)
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
            logger.d("pattern, onSyncClick : viewModel")
            if (AppState.getIsLogged() && NetworkUtility.isNetworkAvailable(context)) {
                bottomNavViewModel.showProgress.set(true)
                viewModel.onSyncClick()
            }
        }
    }

    fun onSearchClick() {
        if (viewModel != null) {
            logger.d("pattern, onSearchClick : viewModel")
            viewModel.onSearchClick()
        }
    }

    fun getMenuListItems(): TreeMap<String, ArrayList<FilterItems>> {
        val item = TreeMap(viewModel.myfolderMenu)
        return item
    }


}