package com.ditto.mylibrary.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.ui.adapter.AllPatternsAdapter
import com.ditto.mylibrary.ui.adapter.FilterDetailsAdapter
import com.ditto.mylibrary.ui.adapter.FilterRvAdapter
import com.ditto.mylibrary.ui.databinding.AllPatternsFragmentBinding
import com.ditto.mylibrary.ui.util.PaginationScrollListener
import com.ditto.mylibrary.ui.util.getBackStackData
import core.appstate.AppState
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.all_patterns_fragment.view.*
import javax.inject.Inject


class AllPatternsFragment : BaseFragment(),
    Utility.CustomCallbackDialogListener {


    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(AllPatternsFragment::class.java.simpleName)
    }

    private val viewModel: AllPatternsViewModel by ViewModelDelegate()
    lateinit var binding: AllPatternsFragmentBinding
    private var patternId: String = "0"
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
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
   /*     currentPage = 1
        isLastPage = false
        viewModel.patternArrayList.clear()
        viewModel.resultMap.clear()*/
    }

    @SuppressLint("WrongConstant")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUIEvents()
        setUpToolbar()
        setUpNavigationDrawer()
        initializeAdapter()


        binding.closeFilter.setOnClickListener {
            binding.drawerLayout.closeDrawer(Gravity.END)
            setFilterMenuAdapter(0)
            binding.rvCategory.smoothScrollToPosition(0)
            binding.rvActions.smoothScrollToPosition(0)
        }
        binding.apply.setOnClickListener {
            //  viewModel.createJson()
            /**
             * API call for getting filter Results....
             */
            if (AppState.getIsLogged() && !Utility.isTokenExpired()) {
                currentPage = 1
                isLastPage = false
                viewModel.patternArrayList.clear()
                bottomNavViewModel.showProgress.set(true)
                viewModel.fetchOnPatternData(viewModel.createJson(currentPage, value = ""))
            }
            binding.drawerLayout.closeDrawer(Gravity.END)
            setFilterMenuAdapter(0)
        }

        binding.clearFilter.setOnClickListener {
            viewModel.resultMap.clear()
            viewModel.patternArrayList.clear()
            viewModel.menuList.clear()
            viewModel.setList()
            currentPage = 1
            isLastPage = false
            viewModel.fetchOnPatternData(viewModel.createJson(currentPage, value = ""))
            if (binding?.rvActions.adapter != null) {
                binding.rvActions.adapter?.notifyDataSetChanged()
                binding.drawerLayout.closeDrawer(Gravity.END)
            }

        }

        binding.imageClearAll.setOnClickListener {
            binding.clearFilter.performClick()
        }
        binding.textviewClear.setOnClickListener {
            binding.clearFilter.performClick()
        }
        if (AppState.getIsLogged() && !Utility.isTokenExpired()) {
            if (viewModel.resultMap.isEmpty()) {
                bottomNavViewModel.showProgress.set(true)
                viewModel.fetchOnPatternData(
                    viewModel.createJson(
                        currentPage,
                        value = ""
                    )
                )  //Initial API call
            } else {
                updatePatterns()
                setFilterMenuAdapter(0)
               if (viewModel.isFilter==true){
                   binding.viewDot.setImageResource(R.drawable.ic_filter_selected)
               }else
                   binding.viewDot.setImageResource(R.drawable.ic_filter)
            }


        }
        getBackStackData<String>("KEY_SEARCH", true) { it ->
            logger.d("SEARCH TERM : $it")
            viewModel.resultMap.clear()
            viewModel.patternArrayList.clear()
            viewModel.menuList.clear()
            viewModel.setList()
            currentPage = 1
            isLastPage = false
            viewModel.fetchOnPatternData(viewModel.createJson(currentPage, value = it))
            if (binding?.rvActions.adapter != null) {
               // binding.rvActions.adapter?.notifyDataSetChanged()
                setFilterMenuAdapter(0)
                binding.drawerLayout.closeDrawer(Gravity.END)
            }
        }


    }

    private fun updatePatterns() {// Updating the adapter

        /*  viewModel.patternList.observe(viewLifecycleOwner, Observer { list ->
              allPatternAdapter.setListData(items = list)
          })*/
        allPatternAdapter.setListData(items = viewModel.patternArrayList)
        binding.toolbar.header_view_title.text =
            getString(R.string.pattern_library_count, AppState.getPatternCount())
        binding.tvFilterResult.text =
            getString(R.string.text_filter_result, viewModel.totalPatternCount)
    }


    private fun setFilterMenuAdapter(position: Int) {
        val result = viewModel.menuList.keys.toList()   //setting menus
        if (result.isNotEmpty()) {
            binding.rvCategory.layoutManager = LinearLayoutManager(requireContext())
            binding.rvCategory.adapter =
                FilterRvAdapter(result, position, object : FilterRvAdapter.MenuClickListener {
                    override fun onMenuSelected(menu: String) {
                        Log.d("CLICKED===", menu)
                        clickedMenu = menu
                        (binding.rvActions.adapter as FilterDetailsAdapter).updateList(menu)

                    }

                })
            setFilterActionAdapter(result[0])  //set menu items
        }
    }

    private fun setFilterActionAdapter(keys: String) {
        val filterDetailsAdapter = FilterDetailsAdapter(object :
            FilterDetailsAdapter.SelectedItemsListener {
            override fun onItemsSelected(title: String, isSelected: Boolean) {
                logger.d("Items==" + title)
                for ((key, value) in viewModel.menuList) {
                    logger.d("After  clik selection : $key = $value")
                }
            }
        }, viewModel.menuList, keys)
        binding.rvActions.adapter = filterDetailsAdapter
        filterDetailsAdapter.viewModel = viewModel
        // filterDetailsAdapter.updateList(keys)

    }

    private fun setUpToolbar() {
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(false)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back_button)
        binding.toolbar.header_view_title.text =
            getString(R.string.pattern_library_count, AppState.getPatternCount())
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

    private fun handleEvent(event: AllPatternsViewModel.Event) = when (event) {

        is AllPatternsViewModel.Event.OnItemClick -> {
            if (findNavController().currentDestination?.id == R.id.myLibraryFragment || findNavController().currentDestination?.id == R.id.allPatternsFragment) {
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
            bottomNavViewModel.showProgress.set(false)
            /* binding.toolbar.header_view_title.text =
                 getString(R.string.pattern_library_count, viewModel.totalPatternCount)*/
        }

        is AllPatternsViewModel.Event.OnOptionsClicked -> {
            showPopupMenu(event.view, event.patternId)
        }

        is AllPatternsViewModel.Event.OnFilterClick -> {

            binding.drawerLayout.openDrawer(Gravity.RIGHT)
            Log.d("pattern", "onFilterClick : AllPatternsFragment")
        }
        is AllPatternsViewModel.Event.OnSearchClick -> {
            //setPatternAdapter()
            Log.d("pattern", "OnSearchClick : AllPatternsFragment")
            if (findNavController().currentDestination?.id == R.id.allPatternsFragment) {
                findNavController().navigate(R.id.action_fragments_to_search)
            } else {
                Log.d("pattern", "OnSearchClick : ELSE")

            }
        }
        is AllPatternsViewModel.Event.OnSyncClick -> {
            binding.clearFilter.performClick()
       /*     isLastPage = false
            currentPage = 1
            viewModel.patternArrayList.clear()
            viewModel.resultMap.clear()
            viewModel.map.clear()
            viewModel.fetchOnPatternData(viewModel.createJson(currentPage, value = ""))*/
            Log.d("pattern", "OnSyncClick : AllPatternsFragment")

            Log.d("pattern", "onFilterClick : AllPatternsFragment")
            // open dialog
        }
        is AllPatternsViewModel.Event.OnSearchClick -> {
            Log.d("pattern", "OnSearchClick : AllPatternsFragment")
            // open dialog
        }
        AllPatternsViewModel.Event.OnResultSuccess -> {
            bottomNavViewModel.showProgress.set(false)
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

            if (binding?.rvCategory?.adapter == null || binding?.rvActions.adapter == null) {
                Log.d("MAP  RESULT== ", "IF")
                setFilterMenuAdapter(0)   //Setting Menu Items
            } else {
                Log.d("MAP  RESULT== ", "ELSE")
                /*  (binding.rvActions.adapter as FilterDetailsAdapter).setListData(
                      viewModel.menuList[clikedMenu]?.toList() ?: emptyList()
                  )*/
                binding.rvCategory.adapter?.notifyDataSetChanged()
                // binding.rvActions.adapter?.notifyDataSetChanged()


            }

        }
        AllPatternsViewModel.Event.UpdateFilterImage -> {
            binding.viewDot.setImageResource(R.drawable.ic_filter_selected)
        }
        AllPatternsViewModel.Event.UpdateDefaultFilter -> {
            binding.viewDot.setImageResource(R.drawable.ic_filter)

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

    private fun showPopupMenu(view: View, patternId: String) {
        this.patternId = patternId
        val popup = PopupMenu(requireContext(), view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.actions, popup.menu)
        popup.show()
    }


    private fun setUpNavigationDrawer() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }

            override fun onDrawerClosed(drawerView: View) {
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

            override fun onDrawerStateChanged(newState: Int) {
                logger.d("OnDarwerState changed")
            }
        })

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

}
