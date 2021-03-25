package com.ditto.instructions.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import com.ditto.instructions.ui.adapter.TabsAdapter
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import com.ditto.instructions.ui.R
import com.ditto.instructions.ui.databinding.FragmentBeamSetupBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BeamSetupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BeamSetupFragment : BaseFragment() {
    private val viewModel: InstructionViewModel by ViewModelDelegate()

    lateinit var binding: FragmentBeamSetupBinding

    /**
     * [Function] onCreateView where setting up the viewmodel and binding to the layout
     */
    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        if (!::binding.isInitialized) {
            binding = FragmentBeamSetupBinding.inflate(inflater)
                .also {
                    arguments?.getInt("InstructionId")?.let { viewModel.instructionID.set(it) }
                    arguments?.getBoolean("isFromOnBoarding")
                        ?.let { viewModel.isFromOnboardinScreen.set(it) }
                    arguments?.getBoolean("isFromHome")
                        ?.let { viewModel.isFromOnboardinScreen.set(it) }
                }
        }
        return binding.root
    }

    /**
     * [Function] onActivityCreated where setting up the toolbar and initial DB calls
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (viewModel.data.value == null) {
            viewModel.fetchInstructionData()
            viewModel.disposable += viewModel.events
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    handleEvent(it)
                }
        }
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setupToolbar()
    }

    /**
     * [Function] Setting the tool bar
     */
    private fun setupToolbar() {
        if (arguments?.getBoolean("isFromHome")!!) {
            bottomNavViewModel.visibility.set(true)
            toolbarViewModel.isShowActionBar.set(true)
            toolbarViewModel.isShowTransparentActionBar.set(false)
            (activity as BottomNavigationActivity).setToolbarTitle("Beam Setup & Takedown")
            (activity as BottomNavigationActivity).showmenu()
        } else {
            bottomNavViewModel.visibility.set(false)
            toolbarViewModel.isShowActionBar.set(false)
            toolbarViewModel.isShowTransparentActionBar.set(false)
            viewModel.toolbarTitle.set("Beam Setup & Takedown")
            viewModel.isFromOnboardinScreen.set(true)
            (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            (activity as BottomNavigationActivity).hidemenu()
        }
    }

    /**
     * [Function] Handling events from view model
     */
    private fun handleEvent(event: InstructionViewModel.Event) =
        when (event) {
            is InstructionViewModel.Event.OnInstructionDataUpdated -> {
                setTabsAdapter()
            }

            is InstructionViewModel.Event.OnShowError -> {

            }
            is InstructionViewModel.Event.OnSkipTutorial -> {
                clickSkipTutorial()
            }

            else -> {
            }
        }

    /**
     * [Function] Skip Tutorial text clicked
     */
    private fun clickSkipTutorial() {
        if (findNavController().currentDestination?.id == R.id.destination_instruction
            || findNavController().currentDestination?.id == R.id.destination_instruction_calibration_fragment
        ) {
            findNavController().navigate(R.id.action_instructionFragment_to_homefragment)
        }
        Unit
    }

    /**
     * [Function] Setting the tabs
     */
    private fun setTabsAdapter() {
        val fragmentAdapter = activity?.supportFragmentManager?.let {
            TabsAdapter(
                it,
                arguments?.getBoolean("isFromHome")!!,
                arguments?.getBoolean("isFromOnBoarding")!!
            )
        }
        binding.viewPager.adapter = fragmentAdapter
        viewModel.data.value?.instructions?.let { fragmentAdapter?.setListData(it) }
        viewModel.let { fragmentAdapter?.setViewModel(it) }
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        viewModel.isShowindicator.set(false)
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
            }
        })


        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}
