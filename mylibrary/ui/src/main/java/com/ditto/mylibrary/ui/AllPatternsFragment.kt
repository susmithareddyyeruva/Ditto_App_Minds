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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.domain.model.Filter
import com.ditto.mylibrary.domain.model.FilterItems
import com.ditto.mylibrary.domain.model.FilterMenuItem
import com.ditto.mylibrary.domain.model.MyLibraryData
import com.ditto.mylibrary.ui.adapter.FilterActionsAdapter
import com.ditto.mylibrary.ui.adapter.FilterRvAdapter
import com.ditto.mylibrary.ui.adapter.GridSpacingItemDecoration
import com.ditto.mylibrary.ui.adapter.PatternAdapter
import com.ditto.mylibrary.ui.databinding.AllPatternsFragmentBinding
import com.ditto.mylibrary.ui.util.ClickListener
import com.ditto.mylibrary.ui.util.RecyclerTouchListener
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import java.util.*
import javax.inject.Inject
import kotlin.Comparator
import kotlin.collections.ArrayList


class AllPatternsFragment : BaseFragment(), FilterActionsAdapter.SelectedItemsListener {

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
        FilterItems("Lee"),
        FilterItems("Addidas")
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
        viewModel.fetchOnPatternData()
        Filter.genderList.addAll(genderList)
        Filter.brandList.addAll(brandList)
        Filter.categoryList.addAll(categoryFilterList)


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
                }


            }
        }))
        binding.closeFilter.setOnClickListener {
            binding.drawerLayout.closeDrawer(Gravity.RIGHT)
        }
        binding.apply.setOnClickListener {
            val genderAsString = Filter.genderList
            viewModel.createJson()
        }
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
        val adapter = PatternAdapter()
        val spanCount = 3// 3 columns
        val spacing = 50 // 50px
        val includeEdge = false
        binding.recyclerViewPatterns.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        )
        binding.recyclerViewPatterns.adapter = adapter
        adapter.viewModel = viewModel
        val patternData: List<MyLibraryData>? = viewModel.data.value?.filter { it.status == "New" }

        //for sorting
        Collections.sort(patternData,
            Comparator<MyLibraryData?> { lhs, rhs -> lhs!!.patternName.compareTo(rhs!!.patternName) })

        patternData?.let { adapter.setListData(it) }
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
            setPatternAdapter()
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
            // open dialog
        }
        else -> {
            logger.d("OnClickPattern")
        }
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
        /*  if (isSelected) {
              if (menu.equals("Category")) {

              }

          }*/
        /*        if (isSelected) {
                    if (menu.equals("Category")) {
                     Filter.categoryList=
                    } else if (menu.equals("Gender")) {
                        //FilterResultCalss.genderList?.add(title)
                    }
                } else {
                    if (menu.equals("Category")) {
                        if (!Filter.categoryList?.isNullOrEmpty()) {
                            Filter.categoryList?.forEach {
                                if (it.equals(title)) {
                                  //  FilterResultCalss.categoryList?.remove(title)
                                }

                            }
                        }
                    } else if (menu.equals("Gender")) {
                        if ( !Filter.genderList?.isNullOrEmpty()) {
                            Filter.genderList?.forEach {
                                if (it.equals(title)) {
                                  //  FilterResultCalss.genderList?.remove(title)
                                }

                            }
                        }
                    }


                }
  */
    }
    /*      if (FilterResultCalss.categoryList?.contains(title)) {
        selectedItemsList.add(title)
        Log.d("category", menu)
        Log.d("Items", title)

    }

} else {
    if (selectedItemsList.contains(title)) {
        selectedItemsList.remove(title)
    }

}*/


}
