package com.ditto.workspace.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.ditto.workspace.ui.adapter.WorkspaceAdapter
import com.ditto.workspace.ui.databinding.FragmentWorkspaceBinding
import com.ditto.workspace.ui.util.Utility
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
                arguments?.getString("clickedTailornovaID")?.let { viewModel.patternId.set(it) }
                arguments?.getString("clickedOrderNumber")
                    ?.let { viewModel.clickedOrderNumber.set(it) }
                arguments?.getString("PatternName")?.let { viewModel.patternName.set(it) }
                Log.d("imageUri12345", "PatternName: ${viewModel.patternName.get()}")
                arguments?.getString("mannequinId")?.let { viewModel.mannequinId.set(it) }
                Log.d("ARGUMENTS", "MANNEQUINID: ${viewModel.mannequinId.get()}")
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
//            Utility.isDoubleTapTextVisible.set(true)
            showProgress(true)
            //viewModel.fetchTailernovaDetails("30644ba1e7aa41cfa9b17b857739968a") // fetching data from internal DB
            viewModel.fetchTailernovaDetails(
                viewModel.patternId.get() ?: ""
            ) // fetching data from internal DB
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
            val garmentBundle = bundleOf(
                PATTERN_CATEGORY to getString(R.string.garments),
                PATTERN_ID to viewModel.patternId.get(),
                PATTERN_NAME to viewModel.patternName.get(),
                ORDER_NO to viewModel.clickedOrderNumber.get()
            )
            fragmentGarment = WorkspaceTabFragment()
            fragmentGarment.setArguments(garmentBundle)

            val liningBundle = bundleOf(
                PATTERN_CATEGORY to getString(R.string.lining),
                PATTERN_ID to viewModel.patternId.get(),
                PATTERN_NAME to viewModel.patternName.get(),
                ORDER_NO to viewModel.clickedOrderNumber.get()
            )
            fragmentLining = WorkspaceTabFragment()
            fragmentLining.setArguments(liningBundle)

            val interfacingBundle = bundleOf(
                PATTERN_CATEGORY to getString(R.string.interfacing),
                PATTERN_ID to viewModel.patternId.get(),
                PATTERN_NAME to viewModel.patternName.get(),
                ORDER_NO to viewModel.clickedOrderNumber.get()
            )
            fragmentInterface = WorkspaceTabFragment()
            fragmentInterface.setArguments(interfacingBundle)

            workspacAdapter.addFragment(fragmentGarment, getString(R.string.garments))//Garment
            workspacAdapter.addFragment(fragmentLining, getString(R.string.lining))//Lining
            workspacAdapter.addFragment(
                fragmentInterface,
                getString(R.string.interfacing)
            )//Interfacing

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
                    resetLayout()
                }

            })
        } else {
            val workspacAdapter = binding.viewPager.adapter
            binding.viewPager.adapter = workspacAdapter
            binding.viewPager.isSaveEnabled = false
            binding.tabLayoutWorkspace.getTabAt(Utility.fragmentTabs.get())?.select()
            disableNoItemTabs() // to fix the disable tab issue after coming back from calibration page
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun clearWorkspace() {
        if (viewModel.selectedTab.get() == 0) {
            fragmentGarment.clearWorkspace()
        } else if (viewModel.selectedTab.get() == 1) {
            fragmentLining.clearWorkspace()
        } else {
            fragmentInterface.clearWorkspace()
        }
        viewModel.spliced_pices_visibility.set(false)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun updateData() {
        if (viewModel.selectedTab.get() == 0) {
            viewModel.data.value?.garmetWorkspaceItemOfflines =
                fragmentGarment.fetchWorkspaceData(0)
        } else if (viewModel.selectedTab.get() == 1) {
            viewModel.data.value?.liningWorkspaceItemOfflines = fragmentLining.fetchWorkspaceData(1)
        } else {
            viewModel.data.value?.interfaceWorkspaceItemOfflines =
                fragmentInterface.fetchWorkspaceData(2)
        }
        fragmentGarment.updateTabData(viewModel.data.value)
        fragmentLining.updateTabData(viewModel.data.value)
        fragmentInterface.updateTabData(viewModel.data.value)
    }

    //  To reset connect buttton and pattern piece adapter
    private fun resetLayout() {
        if (viewModel.selectedTab.get() == 0) {
            fragmentGarment.resetWorkspaceUI()
        } else if (viewModel.selectedTab.get() == 1) {
            fragmentLining.resetWorkspaceUI()
        } else {
            fragmentInterface.resetWorkspaceUI()
        }
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

    private fun disableNoItemTabs() {
        if (viewModel.data.value?.patternPieces?.filter {
                it.tabCategory == getString(
                    R.string.garments
                )
            }?.size == 0) {
            (binding.tabLayoutWorkspace.getChildAt(0) as ViewGroup).getChildAt(0).isEnabled = false
            (binding.tabLayoutWorkspace.getChildAt(0) as ViewGroup).getChildAt(0)
                .setBackgroundResource(R.drawable.tab_disabled)

            val tv = LayoutInflater.from(requireContext()).inflate(R.layout.tab, null)
            binding.tabLayoutWorkspace.getTabAt(0)?.customView = tv
            tv.tag = 0
            tv.setOnTouchListener(this)
        }
        if (viewModel.data.value?.patternPieces?.filter {
                it.tabCategory == getString(
                    R.string.lining
                )
            }?.size == 0) {
            (binding.tabLayoutWorkspace.getChildAt(0) as ViewGroup).getChildAt(1).isEnabled = false
            (binding.tabLayoutWorkspace.getChildAt(0) as ViewGroup).getChildAt(1)
                .setBackgroundResource(R.drawable.tab_disabled)

            val tv = LayoutInflater.from(requireContext()).inflate(R.layout.tab, null)
            binding.tabLayoutWorkspace.getTabAt(1)?.customView = tv
            tv.tag = 1
            tv.setOnTouchListener(this)
        }
        if (viewModel.data.value?.patternPieces?.filter {
                it.tabCategory == getString(
                    R.string.interfacing
                )
            }?.size == 0) {
            (binding.tabLayoutWorkspace.getChildAt(0) as ViewGroup).getChildAt(2).isEnabled = false
            (binding.tabLayoutWorkspace.getChildAt(0) as ViewGroup).getChildAt(2)
                .setBackgroundResource(R.drawable.tab_disabled)

            val tv = LayoutInflater.from(requireContext()).inflate(R.layout.tab, null)
            binding.tabLayoutWorkspace.getTabAt(2)?.customView = tv
            tv.tag = 2
            tv.setOnTouchListener(this)
        }
    }

    private fun updateTab() {
        disableNoItemTabs()
        var position = 0
        if (viewModel.data.value?.selectedTab.equals("0")) {
            position = 0
            binding.viewPager.setCurrentItem(position, false)
            viewModel.selectedTab.set(position)
        } else if (viewModel.data.value?.selectedTab.equals("1")) {
            position = 1
            binding.viewPager.setCurrentItem(position, false)
            viewModel.selectedTab.set(position)
        } else if (viewModel.data.value?.selectedTab.equals("2")) {
            position = 2
            binding.viewPager.setCurrentItem(position, false)
            viewModel.selectedTab.set(position)
        } else {
            Log.d("updateTab", "undefined")
            position = 0
            binding.viewPager.setCurrentItem(position, false)
            viewModel.selectedTab.set(position)
        }
    }

    private fun handleEvent(event: WorkspaceViewModel.Event) =
        when (event) {
            is WorkspaceViewModel.Event.OnDataUpdated -> {
                Log.d("OnDataUpdated", " WSFragment OnDataUpdated")
                clearWorkspace()
                updateTab()
                fragmentGarment.updateTabDataAndShowToUI(viewModel.data.value)
                fragmentLining.updateTabDataAndShowToUI(viewModel.data.value)
                fragmentInterface.updateTabDataAndShowToUI(viewModel.data.value)
            }
            is WorkspaceViewModel.Event.ShowProgressLoader -> {
                showProgress(true)
            }
            is WorkspaceViewModel.Event.HideProgressLoader -> {
                showProgress(false)
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
            switchTab()
        }
    }

    private fun switchTab() {
        if (baseViewModel.activeSocketConnection.get()) {
            GlobalScope.launch {
                core.ui.common.Utility.sendDittoImage(
                    requireActivity(),
                    "solid_black"
                )
            }
        }
        clearWorkspace()
        binding.tabLayoutWorkspace.getTabAt(viewModel.selectedTab.get())?.select()
    }

    override fun onNegativeButtonClicked(alertType: core.ui.common.Utility.AlertType) {
        viewModel.selectedTab.set(binding.viewPager.currentItem)
        return
    }

    override fun onNeutralButtonClicked(alertType: core.ui.common.Utility.AlertType) {
        Log.d("alert", "onNeutralButtonClicked")
    }

    //  handle tab touch
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        val action: Int = event?.getAction() ?: MotionEvent.ACTION_UP
        if (action == MotionEvent.ACTION_UP) {
            if (!baseViewModel.isProjecting.get()) {
                if (viewModel.selectedTab.get() != view?.tag as Int) {
                    updateData()
                    viewModel.selectedTab.set(view?.tag as Int)
                    binding.tabLayoutWorkspace.getTabAt(viewModel.selectedTab.get())?.select()
                    //switchTab()
                    return true
                }
                return false
            } else {
                core.ui.common.Utility.showSnackBar(
                    resources.getString(R.string.projection_progress),
                    binding.container
                )
                return true
            }
        }
        return false
    }

    companion object {
        private const val PATTERN_CATEGORY = "PatternCategory"
        private const val PATTERN_ID = "PatternId"
        private const val PATTERN_NAME = "PatternName"
        private const val ORDER_NO = "clickedOrderNumber"
    }

    private fun showProgress(toShow: Boolean) {
        bottomNavViewModel.showProgress.set(toShow)
    }
}
