package com.ditto.workspace.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.ditto.workspace.ui.adapter.WorkspaceAdapter
import com.ditto.workspace.ui.databinding.FragmentWorkspaceBinding
import com.ditto.workspace.ui.util.Utility
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import com.ditto.workspace.ui.R
import javax.inject.Inject


class WorkspaceFragment : BaseFragment(), core.ui.common.Utility.CallbackDialogListener,
    View.OnTouchListener {

    @Inject
    lateinit var loggerFactory: LoggerFactory

    val logger: Logger by lazy {
        loggerFactory.create(WorkspaceFragment::class.java.simpleName)
    }

    private val viewModel: WorkspaceViewModel by ViewModelDelegate()
    lateinit var binding: FragmentWorkspaceBinding
    lateinit var fragmentGarment: WorkspaceTabFragment
    lateinit var fragmentLining: WorkspaceTabFragment
    lateinit var fragmentInterface: WorkspaceTabFragment


    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        if (!::binding.isInitialized) {
            binding = FragmentWorkspaceBinding.inflate(
                inflater
            ).also {
                arguments?.getInt("PatternId")?.let { viewModel.patternId.set(it) }
            }
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (viewModel.data.value == null) {
            Utility.fragmentTabs.set(0)
            logger.d("TRACE: Setting progress")
            Utility.progressCount.set(0)
            Utility.mPatternPieceList.clear()
            Utility.isFirstTimeLoad.set(true)
            viewModel.fetchWorkspaceData()
        }
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        toolbarViewModel.isShowActionBar.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(false)
        bottomNavViewModel.visibility.set(false)
        setUIEvents()
        setWorkspaceAdapter()
        setTabTouchListener()
    }

    private fun setUIEvents() {
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun setWorkspaceAdapter() {
        val cfManager: FragmentManager = childFragmentManager
        if (binding.viewPager.adapter == null) {
            val workspacAdapter =
                WorkspaceAdapter(cfManager)
            val bundle_one = bundleOf(
                "PatternCategory" to getString(R.string.garments),
                "PatternId" to viewModel.patternId.get()
            )
            fragmentGarment = WorkspaceTabFragment()
            fragmentGarment.setArguments(bundle_one)

            val bundle_two = bundleOf(
                "PatternCategory" to getString(R.string.lining),
                "PatternId" to viewModel.patternId.get()
            )
            fragmentLining = WorkspaceTabFragment()
            fragmentLining.setArguments(bundle_two)

            val bundle_three = bundleOf(
                "PatternCategory" to getString(R.string.interfacing),
                "PatternId" to viewModel.patternId.get()
            )
            fragmentInterface = WorkspaceTabFragment()
            fragmentInterface.setArguments(bundle_three)

            workspacAdapter.addFragment(fragmentGarment, getString(R.string.garments))
            workspacAdapter.addFragment(fragmentLining, getString(R.string.lining))
            workspacAdapter.addFragment(fragmentInterface, getString(R.string.interfacing))

            binding.viewPager.adapter = workspacAdapter
            binding.viewPager.isSaveEnabled = false
            binding.viewPager.offscreenPageLimit = 3
            binding.tabLayoutWorkspace.setupWithViewPager(binding.viewPager)
            binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                    logger.d("onPageScrollStateChanged")
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    logger.d("onPageScrolled$position")
                }

                override fun onPageSelected(position: Int) {
                    logger.d("onPageSelected$position")
                    Utility.fragmentTabs.set(position)
                }

            })
        } else {
            val workspacAdapter = binding.viewPager.adapter
            binding.viewPager.adapter = workspacAdapter
            binding.viewPager.isSaveEnabled = false
            binding.tabLayoutWorkspace.getTabAt(Utility.fragmentTabs.get())?.select()
        }

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun clearWorkspace() {
        fragmentGarment.clearWorkspace()
        fragmentLining.clearWorkspace()
        fragmentInterface.clearWorkspace()
        viewModel.spliced_pices_visibility.set(false)
    }

    private fun setTabTouchListener() {
        (binding.tabLayoutWorkspace.getChildAt(0) as ViewGroup)?.getChildAt(0).tag = 0
        (binding.tabLayoutWorkspace.getChildAt(0) as ViewGroup).getChildAt(0)
            .setOnTouchListener(this)
        (binding.tabLayoutWorkspace.getChildAt(0) as ViewGroup)?.getChildAt(1).tag = 1
        (binding.tabLayoutWorkspace.getChildAt(0) as ViewGroup).getChildAt(1)
            .setOnTouchListener(this)
        (binding.tabLayoutWorkspace.getChildAt(0) as ViewGroup)?.getChildAt(2).tag = 2
        (binding.tabLayoutWorkspace.getChildAt(0) as ViewGroup).getChildAt(2)
            .setOnTouchListener(this)
    }

    private fun updateTab() {
        if (viewModel.data.value?.patternPieces?.filter { it.tabCategory == getString(
                R.string.garments
            ) }?.size == 0) {
            (binding.tabLayoutWorkspace.getChildAt(0) as ViewGroup).getChildAt(0).isEnabled = false
            (binding.tabLayoutWorkspace.getChildAt(0) as ViewGroup).getChildAt(0)
                .setBackgroundResource(R.drawable.tab_disabled)

            val tv = LayoutInflater.from(requireContext()).inflate(R.layout.tab, null)
            binding.tabLayoutWorkspace.getTabAt(0)?.customView = tv
            tv.tag = 0
            tv.setOnTouchListener(this)
        }
        if (viewModel.data.value?.patternPieces?.filter { it.tabCategory == getString(
                R.string.lining
            ) }?.size == 0) {
            (binding.tabLayoutWorkspace.getChildAt(0) as ViewGroup).getChildAt(1).isEnabled = false
            (binding.tabLayoutWorkspace.getChildAt(0) as ViewGroup).getChildAt(1)
                .setBackgroundResource(R.drawable.tab_disabled)

            val tv = LayoutInflater.from(requireContext()).inflate(R.layout.tab, null)
            binding.tabLayoutWorkspace.getTabAt(1)?.customView = tv
            tv.tag = 1
            tv.setOnTouchListener(this)
        }
        if (viewModel.data.value?.patternPieces?.filter { it.tabCategory == getString(
                R.string.interfacing
            ) }?.size == 0) {
            (binding.tabLayoutWorkspace.getChildAt(0) as ViewGroup).getChildAt(2).isEnabled = false
            (binding.tabLayoutWorkspace.getChildAt(0) as ViewGroup).getChildAt(2)
                .setBackgroundResource(R.drawable.tab_disabled)

            val tv = LayoutInflater.from(requireContext()).inflate(R.layout.tab, null)
            binding.tabLayoutWorkspace.getTabAt(2)?.customView = tv
            tv.tag = 2
            tv.setOnTouchListener(this)
        }

        var position = 0
        if (viewModel.data.value?.selectedTab.equals("0")) {
            position = 0
            binding.viewPager.currentItem = position
            viewModel.selectedTab.set(position)
        } else if (viewModel.data.value?.selectedTab.equals("1")) {
            position = 1
            binding.viewPager.currentItem = position
            viewModel.selectedTab.set(position)
        } else if (viewModel.data.value?.selectedTab.equals("2")) {
            position = 2
            binding.viewPager.currentItem = position
            viewModel.selectedTab.set(position)
        } else {
            position = 0
            binding.viewPager.currentItem = position
            viewModel.selectedTab.set(position)
        }

        /*viewModel.data.value?.completedPieces?.let {
            viewModel.setCompletedCount(
                it
            )
        }*/
    }

    private fun handleEvent(event: WorkspaceViewModel.Event) =
        when (event) {
            is WorkspaceViewModel.Event.OnDataUpdated -> {
                updateTab()
            }
            else -> logger.d("Invalid Event")
        }

    override fun onDestroyView() {
        super.onDestroyView()
        logger.d("onDestroyView")
    }

    private fun onTabSwitchAlert() {
       core.ui.common.Utility.getAlertDialogue(
            requireContext(),
            getString(R.string.switching),
            getString(R.string.switching_message),
            getString(R.string.cancel),
            getString(R.string.ok),
            this,
           core.ui.common.Utility.AlertType.TAB_SWITCH
        )

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onPositiveButtonClicked(alertType: core.ui.common.Utility.AlertType) {
        if (alertType == core.ui.common.Utility.AlertType.TAB_SWITCH) {
            if (baseViewModel.activeSocketConnection.get()) {
                GlobalScope.launch {core.ui.common.Utility.sendDittoImage(requireActivity(), "solid_black")}
            }
            binding.tabLayoutWorkspace.getTabAt(viewModel.selectedTab.get())?.select()
            clearWorkspace()
        }

    }

    override fun onNegativeButtonClicked(alertType: core.ui.common.Utility.AlertType) {
        viewModel.selectedTab.set(binding.viewPager.currentItem)
        return
    }

    override fun onNeutralButtonClicked() {
    }

    //  handle tab touch
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        val action: Int = event?.getAction() ?: MotionEvent.ACTION_UP
        if (action == MotionEvent.ACTION_UP) {
            if (!baseViewModel.isProjecting.get()) {
                if (viewModel.selectedTab.get() != view?.tag as Int) {
                    viewModel.selectedTab.set(view?.tag as Int)
                    onTabSwitchAlert()
                    return true
                }
                return false
            } else {
               core.ui.common.Utility.showSnackBar("Projection is under process.. Please wait",binding.container)
                return true
            }
        }
        return false
    }
}
