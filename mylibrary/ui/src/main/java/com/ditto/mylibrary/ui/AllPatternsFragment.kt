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
    private val allPatternAdapter = AllPatternsAdapter()
/*    var menuItems = arrayListOf(
        FilterMenuItem("Category", 1),
        FilterMenuItem("Gender", 2),
        FilterMenuItem("Brand", 3),
        FilterMenuItem("Size", 4),
        FilterMenuItem("Type", 5),
        FilterMenuItem("Season", 6),
        FilterMenuItem("Occasion", 7),
        FilterMenuItem("Age Group", 8),
        FilterMenuItem("Customization", 9)
    )*/
    /* var categoryFilterList = arrayListOf(
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
     )*/

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
        setPatternAdapter()
        if (AppState.getIsLogged()) {
            if (!Utility.isTokenExpired()) {
                if (viewModel.patternList.value == null) {
                    bottomNavViewModel.showProgress.set(true)
                    viewModel.fetchOnPatternData()
                } else {
                    updatePatterns()
                }
            }
        }
        setList()

        // Add Item Touch Listener
        binding.rvCategory.addOnItemTouchListener(RecyclerTouchListener(requireContext(), object :
            ClickListener {
            override fun onClick(view: View, position: Int) {
                setFilterMenuAdapter(position)
                val menuArray= ArrayList<FilterMenuItem>()  //menus
                val menuValArray= ArrayList<FilterItems>()  //menus
                val menuIte= viewModel.menuItem!![position]
                viewModel.menuItem!!.forEach {
                   menuArray.add(FilterMenuItem(it,0))
                }

                val list = viewModel.menuValues?.get(position)
                list?.forEach {
                   menuValArray.add( FilterItems(it.toString()))
                }


                /*         when (position) {
                             0 -> {//category
                                 (binding.rvActions.adapter as FilterActionsAdapter).updateList(
                                     categoryFilterList,
                                     viewModel.menuItem!![position]
                                 )

                             }
                             1 -> {//Gender
                                 (binding.rvActions.adapter as FilterActionsAdapter).updateList(
                                     genderList,
                                     viewModel.menuItem!![position]
                                 )
                             }
                             2 -> {//Brand
                                 (binding.rvActions.adapter as FilterActionsAdapter).updateList(
                                     brandList,
                                     viewModel.menuItem!![position]
                                 )

                             }
                             3 -> {//Size
                                 (binding.rvActions.adapter as FilterActionsAdapter).updateList(
                                     sizeList,
                                     viewModel.menuItem!![position]
                                 )

                             }
                             4 -> {//Type
                                 (binding.rvActions.adapter as FilterActionsAdapter).updateList(
                                     typeList,
                                     viewModel.menuItem!![position]
                                 )

                             }
                             5 -> {//Season
                                 (binding.rvActions.adapter as FilterActionsAdapter).updateList(
                                     seasonList,
                                     viewModel.menuItem!![position]
                                 )

                             }
                             6 -> {//Occasion
                                 (binding.rvActions.adapter as FilterActionsAdapter).updateList(
                                     occasionList,
                                     viewModel.menuItem!![position]
                                 )

                             }
                             7 -> {//Suitable
                                 (binding.rvActions.adapter as FilterActionsAdapter).updateList(
                                     suitableList,
                                     viewModel.menuItem!![position]
                                 )

                             }
                             8 -> {//Customization
                                 (binding.rvActions.adapter as FilterActionsAdapter).updateList(
                                     customizationList,
                                     viewModel.menuItem!![position]
                                 )

                             }
                         }*/
                when (viewModel.menuItem!![position]) {
                    "category" -> {

                        (binding.rvActions.adapter as FilterActionsAdapter).updateList(
                            viewModel.categoryList!!,
                            viewModel.menuItem!![position]
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

    private fun updatePatterns() {// Updating the adapter

        viewModel.patternList.observe(viewLifecycleOwner, Observer { list ->
            allPatternAdapter.setListData(items = list)
        })
        setFilterMenuAdapter(0)   //Setting Menu Items
    }

    private fun setAsDefault() {
        /*   categoryFilterList.forEach {
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
           }*/
    }

    private fun setList() {
        Filter.categoryList.addAll(viewModel.categoryList!!.toMutableList())
        /*   Filter.genderList.addAll(genderList)
           Filter.brandList.addAll(brandList)
           Filter.categoryList.addAll(categoryFilterList)
           Filter.sizeList.addAll(sizeList)
           Filter.typeList.addAll(typeList)
           Filter.occasionList.addAll(occasionList)
           Filter.seasonList.addAll(seasonList)
           Filter.suitableList.addAll(suitableList)
           Filter.customizationList.addAll(customizationList)*/
    }

    private fun setFilterActionAdapter() {
        binding.rvActions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvActions.adapter = FilterActionsAdapter(arrayListOf(), this)
    }

    private fun setFilterMenuAdapter(position: Int) {
        if (viewModel.menuItem != null) {
            binding.rvCategory.layoutManager = LinearLayoutManager(requireContext())
            binding.rvCategory.adapter = FilterRvAdapter(viewModel.menuItem!!, position)
            setFilterActionAdapter()
        }
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


        binding.recyclerViewPatterns.adapter = allPatternAdapter
        allPatternAdapter.viewModel = viewModel
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
            /**
             * Getting ALL PATTERNS LIST
             */
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
