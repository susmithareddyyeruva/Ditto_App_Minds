package com.ditto.mylibrary.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.domain.model.Filter
import com.ditto.mylibrary.domain.model.FilterItems
import com.ditto.mylibrary.domain.model.FilterMenuItem
import com.ditto.mylibrary.ui.adapter.AllPatternsAdapter
import com.ditto.mylibrary.ui.adapter.FilterActionsAdapter
import com.ditto.mylibrary.ui.adapter.FilterRvAdapter
import com.ditto.mylibrary.ui.databinding.AllPatternsFragmentBinding
import com.ditto.mylibrary.ui.util.ClickListener
import com.ditto.mylibrary.ui.util.RecyclerTouchListener
import core.appstate.AppState
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.all_patterns_fragment.view.*
import javax.inject.Inject


class AllPatternsFragment : BaseFragment(), FilterActionsAdapter.SelectedItemsListener,
    Utility.CustomCallbackDialogListener {


    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(AllPatternsFragment::class.java.simpleName)
    }

    private val viewModel: AllPatternsViewModel by ViewModelDelegate()
    lateinit var binding: AllPatternsFragmentBinding
    private var patternId: Int = 0
    private var isOpen = true
    private var selectedItemsList = ArrayList<String>()
    private lateinit var adapter: FilterRvAdapter
    var menuItems = arrayListOf(
        FilterMenuItem("Category", 1),
        FilterMenuItem("Gender", 2),
        FilterMenuItem("Brand", 3),
        FilterMenuItem("Size", 4),
        FilterMenuItem("Type", 5),
        FilterMenuItem("Season", 6),
        FilterMenuItem("Occasion", 7),
        FilterMenuItem("Age Group", 8),
        FilterMenuItem("Customization", 9)
    )
    var categoryFilterList = arrayListOf(
        FilterItems("SubScribed"),
        FilterItems("Purchased"),
        FilterItems("Trials"),
    )
    val genderList = arrayListOf(
        FilterItems("Male"),
        FilterItems("Female")
    )
    val brandList = arrayListOf(
        FilterItems("Simplicity"),
        FilterItems("Sample")
    )
    val sizeList = arrayListOf(
        FilterItems("32"),
        FilterItems("34"),
        FilterItems("36"),
        FilterItems("38")
    )
    val typeList = arrayListOf(
        FilterItems("Dress"),
        FilterItems("Skirt"),
        FilterItems("Blouse")
    )
    val occasionList = arrayListOf(
        FilterItems("Sleepwear"),
        FilterItems("Lounge"),
        FilterItems("Formal"),
        FilterItems("Sport")
    )
    val suitableList = arrayListOf(
        FilterItems("Children"),
        FilterItems("Men"),
        FilterItems("Women"),
    )
    val customizationList = arrayListOf(
        FilterItems("Yes"),
        FilterItems("No")
    )
    val seasonList = arrayListOf(
        FilterItems("Winter"),
        FilterItems("Summer")
    )

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUIEvents()
        setUpToolbar()
        setUpNavigationDrawer()
        setFilterMenuAdapter(0)
        if (AppState.getIsLogged()) {
            if (!Utility.isTokenExpired()) {
                viewModel.fetchOnPatternData()
            }
        }
        setList()

        // Add Item Touch Listener
        binding.rvCategory.addOnItemTouchListener(RecyclerTouchListener(requireContext(), object :
            ClickListener {
            override fun onClick(view: View, position: Int) {
                setFilterMenuAdapter(position)
                when (position) {
                    0 -> {//category
                        (binding.rvActions.adapter as FilterActionsAdapter).updateList(
                            categoryFilterList,
                            menuItems[position].menuItem
                        )

                    }
                    1 -> {//Gender
                        (binding.rvActions.adapter as FilterActionsAdapter).updateList(
                            genderList,
                            menuItems[position].menuItem
                        )
                    }
                    2 -> {//Brand
                        (binding.rvActions.adapter as FilterActionsAdapter).updateList(
                            brandList,
                            menuItems[position].menuItem
                        )

                    }
                    3 -> {//Size
                        (binding.rvActions.adapter as FilterActionsAdapter).updateList(
                            sizeList,
                            menuItems[position].menuItem
                        )

                    }
                    4 -> {//Type
                        (binding.rvActions.adapter as FilterActionsAdapter).updateList(
                            typeList,
                            menuItems[position].menuItem
                        )

                    }
                    5 -> {//Season
                        (binding.rvActions.adapter as FilterActionsAdapter).updateList(
                            seasonList,
                            menuItems[position].menuItem
                        )

                    }
                    6 -> {//Occasion
                        (binding.rvActions.adapter as FilterActionsAdapter).updateList(
                            occasionList,
                            menuItems[position].menuItem
                        )

                    }
                    7 -> {//Suitable
                        (binding.rvActions.adapter as FilterActionsAdapter).updateList(
                            suitableList,
                            menuItems[position].menuItem
                        )

                    }
                    8 -> {//Customization
                        (binding.rvActions.adapter as FilterActionsAdapter).updateList(
                            customizationList,
                            menuItems[position].menuItem
                        )

                    }
                }


            }
        }))
        binding.closeFilter.setOnClickListener {
            binding.drawerLayout.closeDrawer(Gravity.RIGHT)
            setFilterMenuAdapter(0)
        }
        binding.apply.setOnClickListener {
            //  viewModel.createJson()
            /**
             * API call for getting filter Results....
             */
            if (AppState.getIsLogged()) {
                if (!Utility.isTokenExpired()) {
                    bottomNavViewModel.showProgress.set(true)
                    viewModel.getFilteredPatternsData(viewModel.createJson())
                }
            }
            binding.drawerLayout.closeDrawer(Gravity.RIGHT)
            setFilterMenuAdapter(0)
        }

        binding.clearFilter.setOnClickListener {
            Filter.clearAll()
            setAsDefault()
            setList()
            (binding.rvActions.adapter as FilterActionsAdapter).notifyDataSetChanged()

        }

        binding.imageClearAll.setOnClickListener {
            binding.clearFilter.performClick()
        }
    }


    private fun setAsDefault() {
        categoryFilterList.forEach {
            if (it.isSelected) {
                it.isSelected = false
            }
        }
        brandList.forEach {
            if (it.isSelected) {
                it.isSelected = false
            }
        }
        genderList.forEach {
            if (it.isSelected) {
                it.isSelected = false
            }
        }
        seasonList.forEach {
            if (it.isSelected) {
                it.isSelected = false
            }
        }
        occasionList.forEach {
            if (it.isSelected) {
                it.isSelected = false
            }
        }
        suitableList.forEach {
            if (it.isSelected) {
                it.isSelected = false
            }
        }
        typeList.forEach {
            if (it.isSelected) {
                it.isSelected = false
            }
        }
        customizationList.forEach {
            if (it.isSelected) {
                it.isSelected = false
            }
        }
        sizeList.forEach {
            if (it.isSelected) {
                it.isSelected = false
            }
        }
    }

    private fun setList() {
        Filter.genderList.addAll(genderList)
        Filter.brandList.addAll(brandList)
        Filter.categoryList.addAll(categoryFilterList)
        Filter.sizeList.addAll(sizeList)
        Filter.typeList.addAll(typeList)
        Filter.occasionList.addAll(occasionList)
        Filter.seasonList.addAll(seasonList)
        Filter.suitableList.addAll(suitableList)
        Filter.customizationList.addAll(customizationList)
    }

    private fun setFilterActionAdapter() {
        binding.rvActions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvActions.adapter = FilterActionsAdapter(categoryFilterList, this)
    }

    private fun setFilterMenuAdapter(position: Int) {
        binding.rvCategory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategory.adapter = FilterRvAdapter(menuItems, position)
        setFilterActionAdapter()
    }

    private fun setUpToolbar() {
        (activity as BottomNavigationActivity).hidemenu()
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(false)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back_button)
        binding.toolbar.header_view_title_pattern_count.text =
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


    private fun setPatternAdapter() {
        /*   val adapter = PatternAdapter()
           binding.recyclerViewPatterns.adapter = adapter
           adapter.viewModel = viewModel
           val patternData: List<MyLibraryData>? = viewModel.data.value?.filter { it.status == "New" }

           //for sorting
           Collections.sort(patternData,
               Comparator<MyLibraryData?> { lhs, rhs -> lhs!!.patternName.compareTo(rhs!!.patternName) })

           patternData?.let { adapter.setListData(it) }*/


        val adapter = AllPatternsAdapter()
        binding.recyclerViewPatterns.adapter = adapter
        adapter.viewModel = viewModel
        viewModel.patternList.observe(viewLifecycleOwner, Observer { list ->
            adapter.setListData(items = list)
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
        }

        is AllPatternsViewModel.Event.OnOptionsClicked -> {
            showPopupMenu(event.view, event.patternId)
        }

        is AllPatternsViewModel.Event.OnFilterClick -> {

            binding.drawerLayout.openDrawer(Gravity.RIGHT)
            //setPatternAdapter()
            Log.d("pattern", "onFilterClick : AllPatternsFragment")
            // setDrawerList()
            // open dialog
        }
        is AllPatternsViewModel.Event.OnSearchClick -> {
            //setPatternAdapter()
            Log.d("pattern", "OnSearchClick : AllPatternsFragment")
            // open dialog
        }
        is AllPatternsViewModel.Event.OnSyncClick -> {
            //setPatternAdapter()
            Log.d("pattern", "OnSyncClick : AllPatternsFragment")

            //setPatternAdapter()
            Log.d("pattern", "onFilterClick : AllPatternsFragment")
            // open dialog
        }
        is AllPatternsViewModel.Event.OnSearchClick -> {
            //setPatternAdapter()
            Log.d("pattern", "OnSearchClick : AllPatternsFragment")
            // open dialog
        }
        is AllPatternsViewModel.Event.OnSyncClick -> {
            //setPatternAdapter()
            Log.d("pattern", "OnSyncClick : AllPatternsFragment")
            // open dialog
        }
        AllPatternsViewModel.Event.OnResultSuccess -> {
            bottomNavViewModel.showProgress.set(false)
            setPatternAdapter()
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
        else -> {
            Log.d("event", "Add project")
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


    private fun setUpNavigationDrawer() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            override fun onDrawerClosed(drawerView: View) {
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })

    }

    override fun onItemsSelected(title: String, isSelected: Boolean, menu: String) {
        logger.d("Items==" + title)
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
}
