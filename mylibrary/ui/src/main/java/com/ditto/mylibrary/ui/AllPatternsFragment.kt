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
import com.ditto.mylibrary.domain.model.FilterMenuData
import com.ditto.mylibrary.domain.model.FilterResultData
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


class AllPatternsFragment : BaseFragment() {

    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(AllPatternsFragment::class.java.simpleName)
    }

    private val viewModel: AllPatternsViewModel by ViewModelDelegate()
    lateinit var binding: AllPatternsFragmentBinding
    private var patternId: Int = 0
    private var isOpen = true
    private lateinit var adapter: FilterRvAdapter
    var items = arrayListOf(
        FilterMenuData("Category"),
        FilterMenuData("Gender"),
        FilterMenuData("Brand"),
        FilterMenuData("Size"),
        FilterMenuData("Type"),
        FilterMenuData("Season"),
        FilterMenuData("Occasion"),
        FilterMenuData("Age Group"),
        FilterMenuData("Customization")
    )
    var filterList = arrayListOf(
        FilterResultData("SubScribed"),
        FilterResultData("Purchased"),
        FilterResultData("Trials"),
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

        // Add Item Touch Listener
        binding.rvCategory.addOnItemTouchListener(RecyclerTouchListener(requireContext(), object :
            ClickListener {
            override fun onClick(view: View, position: Int) {
                setFilterMenuAdapter(position)
                when (position) {
                    0 -> {
                        (binding.rvActions.adapter as FilterActionsAdapter).updateList(filterList)

                    }
                    1 -> {
                        var filterList = arrayListOf(
                            FilterResultData("Male"),
                            FilterResultData("Female")
                        )
                        (binding.rvActions.adapter as FilterActionsAdapter).updateList(filterList)
                    }
                }


            }
        }))
    }

    private fun setFilterActionAdapter() {
        binding.rvActions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvActions.adapter = FilterActionsAdapter(filterList)
    }

    private fun setFilterMenuAdapter(position: Int) {
        binding.rvCategory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategory.adapter = FilterRvAdapter(items, position)
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

    private fun toggleDrawer() {
        if (isOpen) {
            binding.drawerLayout.openDrawer(Gravity.RIGHT)
            isOpen = false
        } else {
            binding.drawerLayout.closeDrawer(Gravity.RIGHT)
            isOpen = true

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
            toggleDrawer()
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

    private fun setDrawerList() {
        isOpen = true
        toggleDrawer()
        binding.drawerLayout.openDrawer(Gravity.RIGHT)
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

}
