package com.ditto.howto.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.ditto.howto.adapter.TabsPagerAdapter
import com.ditto.howto.utils.Common
import com.ditto.howto_ui.R
import com.ditto.howto_ui.databinding.HowtoFragmentBinding
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.videoplayer.CustomPlayerControlActivity
import com.ditto.workspace.ui.PinchAndZoom
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.howto_fragment.*
import javax.inject.Inject


/**
 * Created by Sesha on  15/08/2020.
 * Fragment class for loading  How To Screen
 */
class HowtoFragment : BaseFragment(), Utility.CustomCallbackDialogListener {

    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(HowtoFragment::class.java.simpleName)
    }

    private val viewModel: HowtoViewModel by ViewModelDelegate()

    lateinit var binding: HowtoFragmentBinding

    /**
     * [Function] onCreateView where setting up the viewmodel and binding to the layout
     */
    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        if (!::binding.isInitialized) {
            binding = HowtoFragmentBinding.inflate(
                inflater
            ).also {
                arguments?.getInt("InstructionId")?.let { viewModel.instructionID.set(it) }
                arguments?.getBoolean("isFromHome")?.let { viewModel.isFromHome.set(it) }
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
            Common.currentSelectedTab.set(0)
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
     * [Function] Handling events from view model
     */
    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun handleEvent(event: HowtoViewModel.Event) =
        when (event) {
            is HowtoViewModel.Event.OnDataUpdated -> {
                viewModel.toolbarTitle.set(getString(R.string.toolbar_title_how_to))
                setTabsAdapter()
            }

            is HowtoViewModel.Event.OnShowError -> {
                showerrorpopup()
            }

            is HowtoViewModel.Event.OnSkipTutorial -> {
                if (findNavController().currentDestination?.id == R.id.destination_howto) {
                    findNavController().navigate(R.id.action_destination_howto_to_nav_graph_id_home)
                }
                Unit
            }
            is HowtoViewModel.Event.OnHideProgress -> bottomNavViewModel.showProgress.set(false)
            is HowtoViewModel.Event.OnShowProgress -> bottomNavViewModel.showProgress.set(true)
            is HowtoViewModel.Event.OnSpinchAndZoom -> {
                showPinchZoomPopup(viewModel.imagePath)
            }
            is HowtoViewModel.Event.OnDownloadPdfClicked -> {
                downloadPdfClick()
            }
            is HowtoViewModel.Event.OnItemClick -> {

                //if (findNavController().currentDestination?.id == com.example.home_ui.R.id.destination_howto && Common.currentSelectedTab.get() != 0) {
                if (findNavController().currentDestination?.id == com.example.home_ui.R.id.destination_howto ) {

                    var title =
                        viewModel.data.value?.instructions1?.get(Common.currentSelectedTab.get())?.title

                    if(viewModel.videoUrl.isNotEmpty()) {
                        val bundle = bundleOf(
                            "videoPath" to viewModel.videoUrl,
                            "title" to title,
                            "from" to "tutorial"
                        )

                        /*    findNavController().navigate(
                                com.example.home_ui.R.id.action_destination_howto_to_nav_graph_id_video,
                                bundle
                            )*/

                        val intent = Intent(requireContext(), CustomPlayerControlActivity::class.java)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    } else {
                        Utility.getCommonAlertDialogue(
                            requireContext(),
                            "",
                            getString(core.lib.R.string.no_video_available),
                            "",
                            getString(core.lib.R.string.str_ok),
                            this,
                            Utility.AlertType.NETWORK,
                            Utility.Iconype.FAILED
                        )
                    }


                } else {
                    Utility.getCommonAlertDialogue(
                        requireContext(),
                        "",
                        getString(core.lib.R.string.no_video_available),
                        "",
                        getString(core.lib.R.string.str_ok),
                        this,
                        Utility.AlertType.NETWORK,
                        Utility.Iconype.FAILED
                    )
                }
            }
            else -> {
                logger.d("button event, Button clicked except onSkip")
            }
        }

    private fun downloadPdfClick() {
        if (findNavController().currentDestination?.id == com.example.home_ui.R.id.destination_howto) {

            val title =
                viewModel.data.value?.instructions1?.get(Common.currentSelectedTab.get())?.title

            if (viewModel.tutorialPdfUrl.isNotEmpty()) {
                var bundle = Bundle()
                bundle =
                    bundleOf(
                        "InstructionPdfUrl" to viewModel.tutorialPdfUrl,
                        "InstructionPdfTitle" to title
                    )
                findNavController().navigate(
                    R.id.action_instructionFragment_to_howToInstructionsFragment,
                    bundle
                )
            } else {
                Utility.getCommonAlertDialogue(
                    requireContext(),
                    "",
                    getString(core.lib.R.string.no_pdf_available),
                    "",
                    getString(core.lib.R.string.str_ok),
                    this,
                    Utility.AlertType.NETWORK,
                    Utility.Iconype.FAILED
                )
            }

        } else {

        }
    }

    fun showPinchZoomPopup(imagePath: String?) {
        val intent = Intent(context, PinchAndZoom::class.java)
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        intent.putExtra("ImageURL", imagePath)
        intent.putExtra("isFrom", "Howto")
        context?.let { ContextCompat.startActivity(it, intent, null) }
    }

    /**
     * [Function] To show error popup
     */
    private fun showerrorpopup() {
        viewModel.isErrorString.get()?.let {
            view?.rootView?.let { it1 ->
                Common.showSnackBar(
                    it,
                    it1
                )
            }
        }
    }

    /**
     * [Function] Setting the tool bar
     */
    private fun setupToolbar() {
        arguments?.getBoolean("isFromHome")?.let { viewModel.isFromHome.set(it) }
        if (viewModel.isFromHome.get()) {
            bottomNavViewModel.visibility.set(false)
            toolbarViewModel.isShowActionBar.set(false)
            toolbarViewModel.isShowTransparentActionBar.set(false)
            viewModel.toolbarTitle.set(getString(R.string.toolbar_title_how_to))
            toolbar.setNavigationIcon(R.drawable.ic_back_button)
            (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            bottomNavViewModel.visibility.set(false)
            toolbarViewModel.isShowActionBar.set(false)
            toolbarViewModel.isShowTransparentActionBar.set(false)
            toolbar.setNavigationIcon(R.drawable.ic_back_button)
            (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    /**
     * [Function] Setting the tabs
     */
    private fun setTabsAdapter() {
        val fragmentAdapter = activity?.supportFragmentManager?.let {
            TabsPagerAdapter(
                it
            )
        }
        binding.viewPager.adapter = fragmentAdapter
        viewModel.data.value?.instructions1?.let { fragmentAdapter?.setListData(it) }
        viewModel?.let { fragmentAdapter?.setViewModel(it) }
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        viewModel.isShowindicator.set(false)
        // disabling previous next button inside pager if count is 1
        if (viewModel.data.value?.instructions1?.get(Common.currentSelectedTab.get())?.instructions?.size ?: 0 < 2) {
            viewModel.isFinalPage.set(true)
            viewModel.isStartingPage.set(true)
        }
        binding.viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                Log.d("onPageScroll", "state changed")
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                logger.d("onPageScroll, onPageScrolled")
            }

            override fun onPageSelected(position: Int) {
                Common.currentSelectedTab.set(position)
                // disabling previous next button inside pager if count is 1
                if (viewModel.data.value?.instructions1?.get(Common.currentSelectedTab.get())?.instructions?.size ?: 0 < 2) {
                    viewModel.isFinalPage.set(true)
                    viewModel.isStartingPage.set(true)
                }
                if (position == 4 ) {
                    viewModel.isShowPlaceholder.set(true)
                } else {
                    viewModel.isShowPlaceholder.set(false)
                }
                Common.isShowingVideoPopup.set(false)
            }
        })


        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Common.isNextTabSelected.set(true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                logger.d("onTabUnselected, tab selected")
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                logger.d("onTabReselected, re-selected")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.isWatchVideoClicked.set(false)
    }

    override fun onCustomPositiveButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType,
    ) {
    }

    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType,
    ) {
    }
}