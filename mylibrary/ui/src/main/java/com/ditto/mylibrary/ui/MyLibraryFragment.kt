package com.ditto.mylibrary.ui

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.domain.model.FilterItems
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
    private var myfolderDetail: MyFolderDetailFragment = MyFolderDetailFragment()
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
            val tabPosition = binding.tabLayout.selectedTabPosition
            if (tabPosition == 0)
                allPatternsFragment.cleaFilterData()
            else
                myfolderDetail.cleaFilterData()

            if (binding?.rvActions?.adapter != null) {
                binding.rvActions?.adapter?.notifyDataSetChanged()
                binding.drawerLayoutMylib?.closeDrawer(Gravity.RIGHT)
            }

        }

        binding.imageClearAll?.setOnClickListener {
            binding.clearFilter?.performClick()
        }
        binding.toolbar.setNavigationOnClickListener {

            val tabPosition = binding.tabLayout.selectedTabPosition

            removeBackstack()
            val fr = childFragmentManager.fragments.size
            requireActivity().onBackPressed()
            if (tabPosition == 1) {
                hideFilterComponents()
                setToolbarTittle(getString(R.string.my_folders))  //My Folder fragment will visible
            }

        }


    }

    private fun removeBackstack() {
        if (childFragmentManager.fragments.size > 2) {
            var list = ArrayList<Fragment>()
            for (i in 2 until childFragmentManager?.fragments.size) {
                list.add(childFragmentManager.fragments[i])
            }
            list.let {
                if (it.isNotEmpty()) {
                    childFragmentManager.beginTransaction().apply {
                        for (fragment in it) {
                            remove(fragment)
                        }
                        commit()
                    }
                }
                childFragmentManager.popBackStack()
            }
        }
       println("FRAGMENt==="+childFragmentManager.fragments.size)
    }

    fun setToolbarTittle(tittle: String) {
        binding.toolbar.header_view_title.text = tittle
    }

    private fun applyFilter() {
        val tabPosition = binding.tabLayout.selectedTabPosition
        if (tabPosition == 0) {
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

    fun hideToolbar() {
        //  binding.tvSearch.visibility = View.GONE
        binding.patternLibraryAppBar.visibility = View.INVISIBLE
        binding.searchContainer?.visibility = View.VISIBLE
    }

    fun showToolbar() {
        // binding.tvSearch.visibility = View.VISIBLE
        binding.patternLibraryAppBar.visibility = View.VISIBLE
        binding.searchContainer?.visibility = View.GONE
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
                    /**
                     * To Show My Folders screen always  While Switching tab between all pattern and myfolder
                     * without showing My Folder Detail Screen
                     */
                    showFilterComponents()
                    setToolbarTittle(
                        getString(
                            R.string.pattern_library_count,
                            AppState.getPatternCount()
                        )
                    )
                    val currentFragment = fragmentManager?.fragments?.last()

                    //  childFragmentManager.fragments[1].fragmentManager?.popBackStackImmediate()
                    removeBackstack()
                    val fragmentsize = childFragmentManager?.fragments?.size


                } else if (tab?.position == 1) {
                    if (childFragmentManager.fragments.size==2) {
                        hideFilterComponents()
                        setToolbarTittle(getString(R.string.my_folders))
                    }
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
                } else { //tab postion 1
                    if (childFragmentManager?.fragments?.size == 2) {
                        myFolderFragment.onSyncClick()
                    } else
                        myfolderDetail.onSyncClick()
                }
            }
            MyLibraryViewModel.Event.OnSearchClick -> {
                hideToolbar()
                binding.editSearch?.requestFocus()
                val imgr: InputMethodManager =
                    requireActivity()?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imgr.showSoftInput(binding.editSearch, InputMethodManager.SHOW_IMPLICIT)

                val watcher = binding.editSearch?.addTextChangedListener(object : TextWatcher {
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
                            binding?.imageCloseSearch?.visibility = View.VISIBLE
                        } else {
                            binding?.imageCloseSearch?.visibility = View.GONE
                        }
                    }
                })
                binding?.tvCAncelDialog?.setOnClickListener {
                    showToolbar()
                    requireActivity().window
                        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                    if (binding?.editSearch?.text.toString().isNotEmpty()) {
                        binding?.editSearch?.text?.clear()
                        //  allPatternsFragment.cleaFilterData()
                        val tabPosition = binding.tabLayout.selectedTabPosition
                        if (tabPosition == 0) {
                            allPatternsFragment.callSearchResult(binding?.editSearch?.text.toString())
                        } else {
                            myfolderDetail.callSearchResult(binding?.editSearch?.text.toString())
                        }
                    }


                }

                binding?.editSearch?.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (binding?.editSearch?.text.toString().isNotEmpty()) {
                            val imm =
                                requireActivity()?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(binding?.editSearch?.getWindowToken(), 0)
                            val tabPosition = binding.tabLayout.selectedTabPosition
                            if (tabPosition == 0) {
                                allPatternsFragment.callSearchResult(binding?.editSearch?.text.toString())
                            } else {
                                myfolderDetail.callSearchResult(binding?.editSearch?.text.toString())
                            }


                            /*   setBackStackData(
                                   "KEY_SEARCH",
                                   alertDialog.editSearch.text.toString(),
                                   true
                               )*/

                        } else


                            return@OnEditorActionListener true
                    }
                    false
                })
                binding?.imageCloseSearch?.setOnClickListener {
                    binding?.editSearch?.editSearch?.text?.clear()
                }
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

    override fun onSetCount(tittle: String) {
        binding.toolbar.header_view_title.text = tittle
    }

    override fun onFilterApplied(isApplied: Boolean) {
        if (isApplied) {
            binding.viewDot.setImageResource(R.drawable.ic_tabfilter)
        } else
            binding.viewDot.setImageResource(R.drawable.ic_filter_default)
    }

    private fun setFilterMenuAdapter(position: Int) {
        val tabPosition = binding.tabLayout.selectedTabPosition
        val menulist = if (tabPosition == 0) {
            allPatternsFragment.getMenuListItems()
        } else {
            myfolderDetail.getMenuListItems()
        }
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
        val menuList: HashMap<String, ArrayList<FilterItems>>
        val tabPosition = binding.tabLayout.selectedTabPosition
        if (tabPosition == 0) {
            menuList = allPatternsFragment.getMenuListItems()
        } else {
            menuList = myfolderDetail.getMenuListItems()
        }
        val filterDetailsAdapter = FilterDetailsAdapter(object :
            FilterDetailsAdapter.SelectedItemsListener {
            override fun onItemsSelected(title: String, isSelected: Boolean) {
                logger.d("Items==" + title)
                for ((key, value) in menuList) {
                    logger.d("After  clik selection : $key = $value")
                }
            }
        }, menuList, keys)
        binding.rvActions?.adapter = filterDetailsAdapter
        filterDetailsAdapter.viewModel = viewModel
        // filterDetailsAdapter.updateList(keys)

    }

    fun switchtoMyFolderFragmentTab() {
        val tabPosition = binding.tabLayout.selectedTabPosition
        binding.viewPager.currentItem = 1

    }

}
