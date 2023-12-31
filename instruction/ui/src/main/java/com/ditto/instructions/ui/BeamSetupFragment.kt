package com.ditto.instructions.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.ditto.instructions.ui.adapter.TabsAdapter
import com.ditto.instructions.ui.databinding.FragmentBeamSetupBinding
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.google.android.material.tabs.TabLayout
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.fragment_beam_setup.*
import javax.inject.Inject

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

    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(BeamSetupFragment::class.java.simpleName)
    }

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
                    arguments?.getBoolean("isFromHome")
                        ?.let { viewModel.isFromHome?.set(it) }
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
            bottomNavViewModel.showProgress.set(true)
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
            bottomNavViewModel.visibility.set(false)
            toolbarViewModel.isShowActionBar.set(false)
            toolbarViewModel.isShowTransparentActionBar.set(false)
            viewModel.toolbarTitle.set("Beam Setup & Takedown")
            toolbar.setNavigationIcon(R.drawable.ic_back_button)
            (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            bottomNavViewModel.visibility.set(false)
            toolbarViewModel.isShowActionBar.set(false)
            toolbarViewModel.isShowTransparentActionBar.set(false)
            toolbar.setNavigationIcon(R.drawable.ic_back_button)
            viewModel.toolbarTitle.set("Beam Setup & Takedown")
            (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    /**
     * [Function] Handling events from view model
     */

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun handleEvent(event: InstructionViewModel.Event) =
        when (event) {
            is InstructionViewModel.Event.OnInstructionDataUpdated -> {
                setTabsAdapter()
            }

            is InstructionViewModel.Event.OnShowError -> {
                logger.d("error,instruction error")

            }
            is InstructionViewModel.Event.OnSkipTutorial -> {
                clickSkipTutorial()
            }

            else -> {
                logger.d("instruction,event ")
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
                arguments?.getBoolean("isFromHome")!!
            )
        }
        binding.viewPager.adapter = fragmentAdapter
        viewModel.data.value?.instructions?.let { fragmentAdapter?.setListData(it) }
        viewModel.let { fragmentAdapter?.setViewModel(it) }
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        viewModel.isShowindicator.set(false)
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                logger.d("onPageScrollStateChanged")
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                logger.d("onPageScroll,onPageScrolled")
            }

            override fun onPageSelected(position: Int) {
                logger.d("onPageScroll,onPageSelected")
            }
        })


        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                logger.d("onTabSelection,onTabSelected")
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                logger.d("onTabSelection,onTabUnselected")
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                logger.d("onTabSelection,onTabReselected")
            }
        })
    }
}
