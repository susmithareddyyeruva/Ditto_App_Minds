package com.ditto.mylibrary.ui

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.ui.adapter.FilterDetailsAdapter
import com.ditto.mylibrary.ui.adapter.FilterRvAdapter
import com.ditto.mylibrary.ui.adapter.MyLibraryAdapter
import com.ditto.mylibrary.ui.databinding.MyLibraryFragmentBinding
import com.google.android.material.tabs.TabLayout
import core.appstate.AppState
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.my_library_fragment.*
import kotlinx.android.synthetic.main.my_library_fragment.view.*
import javax.inject.Inject


class MyLibraryFragment : BaseFragment(), AllPatternsFragment.SetPatternCount,
    AllPatternsFragment.setFilterIcons {

    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(MyLibraryFragment::class.java.simpleName)
    }

    private val viewModel: MyLibraryViewModel by ViewModelDelegate()
    lateinit var binding: MyLibraryFragmentBinding
    private var allPatternsFragment: AllPatternsFragment = AllPatternsFragment(this, this)
    private var myfolderDetail: MyFolderDetailFragment= MyFolderDetailFragment()
    private var myFolderFragment: MyFolderFragment = MyFolderFragment(myfolderDetail)

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        binding = MyLibraryFragmentBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.getInt("UserId")?.let { viewModel.userId = (it) }
        setTabsAdapter()
        setUpToolbar()
        setUpNavigationDrawer()
        toolbarViewModel.visibility.set(false)
        bottomNavViewModel.visibility.set(false)
        toolbarViewModel.isShowActionBar.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(false)
        setUIEvents()
        binding.closeFilter?.setOnClickListener {
            binding.drawerLayoutMylib?.closeDrawer(Gravity.RIGHT)
            //  setFilterMenuAdapter(0)
            binding.rvCategory?.smoothScrollToPosition(0)
            binding.rvActions?.smoothScrollToPosition(0)
        }
        binding.apply?.setOnClickListener {
            //  viewModel.createJson()
            /**
             * API call for getting filter Results....
             */
            applyFilter()
        }

        binding.clearFilter?.setOnClickListener {
            allPatternsFragment.cleaFilterData()
            if (binding?.rvActions?.adapter != null) {
                binding.rvActions?.adapter?.notifyDataSetChanged()
                binding.drawerLayoutMylib?.closeDrawer(Gravity.RIGHT)
            }

        }

        binding.imageClearAll?.setOnClickListener {
            binding.clearFilter?.performClick()
        }


    }

    private fun applyFilter() {
        val tabPosition = binding.tabLayout.selectedTabPosition
        if (tabPosition==0) {
            allPatternsFragment.applyFilter()
            binding.drawerLayoutMylib?.closeDrawer(Gravity.RIGHT)
            setFilterMenuAdapter(0)
        } else {
            myfolderDetail.applyFilter()
            binding.drawerLayoutMylib?.closeDrawer(Gravity.RIGHT)
            setFilterMenuAdapter(0)
        }
    }

    private fun setUpNavigationDrawer() {
        binding.drawerLayoutMylib?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.drawerLayoutMylib?.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
                binding.drawerLayoutMylib?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }

            override fun onDrawerClosed(drawerView: View) {
                binding.drawerLayoutMylib?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

            override fun onDrawerStateChanged(newState: Int) {
                logger.d("OnDarwerState changed")
            }
        })

    }

    private fun setUIEvents() {
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

     fun hideFilterComponents() {
        binding.tvFilter.visibility = View.INVISIBLE
        binding.tvSearch.visibility = View.INVISIBLE
        binding.viewDot.visibility = View.INVISIBLE
    }

     fun showFilterComponents() {
        binding.tvFilter.visibility = View.VISIBLE
        binding.tvSearch.visibility = View.VISIBLE
        binding.viewDot.visibility = View.VISIBLE
    }

    private fun setUpToolbar() {
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(false)
        binding.toolbar?.setNavigationIcon(R.drawable.ic_back_button)
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.header_view_title.text =
            getString(R.string.pattern_library_count, AppState.getPatternCount())
    }

    private fun setTabsAdapter() {
        showFilterComponents()
        /* allPatternsFragment = AllPatternsFragment(this, this)
         myFolderFragment = MyFolderFragment()*/
        val cfManager: FragmentManager = childFragmentManager
        val adapter = MyLibraryAdapter(cfManager)
        adapter.addFragment(
            allPatternsFragment, getString(
                R.string.all_patterns
            ), this, this
        )
        adapter.addFragment(
            myFolderFragment, getString(
                R.string.my_folders
            ), this, this
        )
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(view_pager)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

                if (tab?.position == 0) {
                    showFilterComponents()
                    binding.toolbar.header_view_title.text =
                        getString(R.string.pattern_library_count, AppState.getPatternCount())
                } else {
                    hideFilterComponents()
                    binding.toolbar.header_view_title.text = getString(R.string.my_folders)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun handleEvent(event: MyLibraryViewModel.Event) =
        when (event) {
            is MyLibraryViewModel.Event.completedProjects -> {
                Toast.makeText(context, "adadjhf", Toast.LENGTH_SHORT).show()
            }
            MyLibraryViewModel.Event.OnFilterClick -> {
                val tabPosition = binding.tabLayout.selectedTabPosition
                if (tabPosition == 0) {
                    setFilterMenuAdapter(0)
                    binding.drawerLayoutMylib?.openDrawer(Gravity.RIGHT)
                    Log.d("pattern", "onFilterClick : AllPatternsFragment")
                } else {
                    setFilterMenuAdapter(0)
                    binding.drawerLayoutMylib?.openDrawer(Gravity.RIGHT)
                    Log.d("pattern", "onFilterClick : MyFolder Detail")
                }
            }
            MyLibraryViewModel.Event.OnSyncClick -> {
                val tabPosition = binding.tabLayout.selectedTabPosition
                if (tabPosition == 0) {
                    allPatternsFragment.onSyncClick()
                } else {
                    myfolderDetail.onSyncClick()
                }
            }
            MyLibraryViewModel.Event.OnSearchClick -> {
                val tabPosition = binding.tabLayout.selectedTabPosition
                if (tabPosition == 0)
                    allPatternsFragment.onSearchClick()
                else {
                    myfolderDetail.onSearchClick()

                }

            }
            else -> {
                Log.d("MyLibraryViewModel", "MyLibraryViewModel.Event undefined")

            }
        }


    override fun onSetCount(totalPatternCount: Int) {
        binding.toolbar.header_view_title.text =
            getString(R.string.pattern_library_count, totalPatternCount)
    }

    override fun onFilterApplied(isApplied: Boolean) {
        if (isApplied) {
            binding.viewDot.setImageResource(R.drawable.ic_filter_selected)
        } else
            binding.viewDot.setImageResource(R.drawable.ic_filter)
    }

    private fun setFilterMenuAdapter(position: Int) {
        val menulist = allPatternsFragment.getMenuListItems()
        val result = menulist.keys.toList()   //setting menus
        if (result.isNotEmpty()) {
            binding.rvCategory?.layoutManager = LinearLayoutManager(requireContext())
            binding.rvCategory?.adapter =
                FilterRvAdapter(result, position, object : FilterRvAdapter.MenuClickListener {
                    override fun onMenuSelected(menu: String) {
                        Log.d("CLICKED===", menu)
                        (binding.rvActions?.adapter as FilterDetailsAdapter).updateList(menu)

                    }

                })
            setFilterActionAdapter(result[0])  //set menu items
        }
    }

    private fun setFilterActionAdapter(keys: String) {
        val menulist = allPatternsFragment.getMenuListItems()
        val filterDetailsAdapter = FilterDetailsAdapter(object :
            FilterDetailsAdapter.SelectedItemsListener {
            override fun onItemsSelected(title: String, isSelected: Boolean) {
                logger.d("Items==" + title)
                for ((key, value) in menulist) {
                    logger.d("After  clik selection : $key = $value")
                }
            }
        }, menulist, keys)
        binding.rvActions?.adapter = filterDetailsAdapter
        filterDetailsAdapter.viewModel = viewModel
        // filterDetailsAdapter.updateList(keys)

    }
    fun switchtoMyFolderFragmentTab(){
        val tabPosition = binding.tabLayout.selectedTabPosition
        binding.viewPager.currentItem = 1

    }
}
