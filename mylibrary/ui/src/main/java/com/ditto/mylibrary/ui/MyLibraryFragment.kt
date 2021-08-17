package com.ditto.mylibrary.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.ui.adapter.MyLibraryAdapter
import com.ditto.mylibrary.ui.databinding.MyLibraryFragmentBinding
import com.google.android.material.tabs.TabLayout
import core.ui.BaseFragment
import core.ui.BaseViewModel
import core.ui.ViewModelDelegate
import core.ui.ViewModelScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.my_library_fragment.*
import kotlinx.android.synthetic.main.my_library_fragment.view.*
import javax.inject.Inject

class MyLibraryFragment : BaseFragment(),AllPatternsFragment.SetPatternCount {

    @Inject
    lateinit var loggerFactory: LoggerFactory

    val baseViewModel1: BaseViewModel by ViewModelDelegate(ViewModelScope.ACTIVITY)

    val logger: Logger by lazy {
        loggerFactory.create(MyLibraryFragment::class.java.simpleName)
    }

    private val viewModel: MyLibraryViewModel by ViewModelDelegate()
    lateinit var binding: MyLibraryFragmentBinding
    private lateinit var allPatternsFragment: AllPatternsFragment
    private lateinit var myFolderFragment: MyFolderFragment

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
        setUIEvents()
        setUpToolbar()
        setTabsAdapter()
        toolbarViewModel.visibility.set(false)
        bottomNavViewModel.visibility.set(false)
        toolbarViewModel.isShowActionBar.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(false)

    }

    fun setUIEvents() {
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    private fun setUpToolbar() {
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(false)
        binding.toolbar?.setNavigationIcon(R.drawable.ic_back_button)
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setTabsAdapter() {
        allPatternsFragment = AllPatternsFragment(this)
        myFolderFragment = MyFolderFragment()
        val cfManager: FragmentManager = childFragmentManager
        val adapter = MyLibraryAdapter(cfManager,this)
        adapter.addFragment(
            allPatternsFragment, getString(
                R.string.all_patterns
            ),this
        )
        adapter.addFragment(
            myFolderFragment, getString(
                R.string.my_folders
            ),this
        )
        view_pager.adapter = adapter
        tabLayout.setupWithViewPager(view_pager)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    binding.toolbar.header_view_title.text = getString(R.string.pattern_library)
                } else {
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
                if (tabPosition == 0)
                    allPatternsFragment.onFilterClick()
                else {

                }
            }
            MyLibraryViewModel.Event.OnSyncClick -> {
                val tabPosition = binding.tabLayout.selectedTabPosition
                if (tabPosition == 0)
                    allPatternsFragment.onSyncClick()
                else {

                }
            }
            MyLibraryViewModel.Event.OnSearchClick -> {
                val tabPosition = binding.tabLayout.selectedTabPosition
                if (tabPosition == 0)
                    allPatternsFragment.onSearchClick()
                else {

                }

            }
            else -> {
                Log.d("MyLibraryViewModel", "MyLibraryViewModel.Event undefined")

            }
        }


    override fun onSetCount(totalPatternCount: Int) {
        binding.toolbar.header_view_title.text =
            getString(R.string.pattern_library_count, baseViewModel1.totalCount)
    }

}
