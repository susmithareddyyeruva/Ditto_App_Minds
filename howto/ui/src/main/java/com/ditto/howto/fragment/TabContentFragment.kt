package com.ditto.howto.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.ditto.howto.adapter.TabContentAdapter
import com.ditto.howto.ui.HowtoViewModel
import com.ditto.howto.utils.Common
import com.ditto.howto_ui.databinding.TabcontentFragmentBinding
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

//to show the content inside the tabs
class TabContentFragment (val vm: HowtoViewModel, var pos: Int): BaseFragment() {

    @Inject
    lateinit var loggerFactory: LoggerFactory

    val logger: Logger by lazy {
        loggerFactory.create(TabContentFragment::class.java.simpleName)
    }

    lateinit var binding: TabcontentFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = TabcontentFragmentBinding.inflate(
            inflater
        ).also {

            it.viewModel = vm
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as BottomNavigationActivity).setToolbarTitle("How to")
        setInstructionadapter()
        vm.disposable += vm.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    private fun setInstructionadapter() {
        val adapter = context?.let { TabContentAdapter(it) }
        binding.instructionViewPager.adapter = adapter
        binding.instructionViewPager.currentItem = 0
        adapter?.viewModel = vm
        adapter?.setPos(pos)
        vm.data.value?.instructions1?.get(pos)?.instructions.let {
            if (it != null) {
                adapter?.setListData(it)
            }
        }
        binding.tablay.setupWithViewPager(binding.instructionViewPager)
        binding.instructionViewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                Log.d("PageScroll","onPageScrollStateChanged")
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                Common.isShowingVideoPopup.set(false)
            }

            override fun onPageSelected(position: Int) {
                val listsize = vm.data.value?.instructions1?.get(Common.currentSelectedTab.get())?.instructions?.size
                if (listsize != null) {
                    if (position == (listsize - 1)) {
                        vm.isFinalPage.set(true)
                        vm.isStartingPage.set(false)
                    } else if (position == 0) {
                        vm.isFinalPage.set(false)
                        vm.isStartingPage.set(true)
                    } else {
                        vm.isFinalPage.set(false)
                        vm.isStartingPage.set(false)
                    }
                }
            }
        })

    }

    /**
     * [Function] Current view pager selected item
     */
    private fun getItem(i: Int): Int {
        return binding.instructionViewPager.currentItem + i
    }

    /**
     * [Function] Previous view pager selected item
     */
    private fun getprevItem(i: Int): Int {
        return binding.instructionViewPager.currentItem - i
    }
    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun handleEvent(event: HowtoViewModel.Event) =
        when (event) {
            is HowtoViewModel.Event.OnNextButtonClicked -> {
                if(pos== Common.currentSelectedTab.get()) {
                    binding.instructionViewPager.setCurrentItem(getItem(+1), true)
                } else {
                    Log.d("event","OnNextButtonClicked")
                }
            }
            is HowtoViewModel.Event.OnPreviousButtonClicked -> {
                if(pos== Common.currentSelectedTab.get()) {
                binding.instructionViewPager.setCurrentItem(getprevItem(+1), true)
                } else {
                    Log.d("event","OnPreviousButtonClicked")
                }
            }
            is HowtoViewModel.Event.OnPlayVideoClicked -> {
                Log.d("event","OnPlayVideoClicked")
            }
            is HowtoViewModel.Event.OnShowError -> {
                Log.d("event","OnShowError")
            }
            is HowtoViewModel.Event.OnDataUpdated -> {
                Log.d("event","OnDataUpdated")
            }
            is HowtoViewModel.Event.OnItemClick -> {
                Log.d("event12", "OnVideoPlay")
            }
            HowtoViewModel.Event.OnSkipTutorial -> {
                Log.d("event","OnSkipTutorial")
            }
            HowtoViewModel.Event.OnSpinchAndZoom -> {
                Log.d("event","OnSpinchAndZoom")
            }
        }

    override fun onResume() {
        super.onResume()
        if (Common.isNextTabSelected.get()){
            Common.isNextTabSelected.set(false)
            binding.instructionViewPager.currentItem=0
            vm.isStartingPage.set(true)
            vm.isFinalPage.set(false)
        }
    }

}