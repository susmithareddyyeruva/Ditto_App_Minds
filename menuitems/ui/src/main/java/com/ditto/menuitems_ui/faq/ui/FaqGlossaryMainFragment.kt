package com.ditto.menuitems_ui.faq.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.ViewPager
import com.ditto.menuitems.domain.model.faq.FaqGlossaryResponseDomain
import com.ditto.menuitems_ui.R
import com.ditto.menuitems_ui.databinding.FaqGlossaryMainfragmentBinding
import com.ditto.menuitems_ui.faq.ui.adapters.TabFaqAdapter
import com.google.android.material.tabs.TabLayout
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign


class FaqGlossaryMainFragment : BaseFragment(), Utility.CustomCallbackDialogListener  {

    private val faqGlossaryfragmentViewModel: FAQGlossaryfragmentViewModel by ViewModelDelegate()
    lateinit var binding: FaqGlossaryMainfragmentBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::binding.isInitialized) {
            binding = FaqGlossaryMainfragmentBinding.inflate(
                inflater
            ).also {
                it.viewModel = faqGlossaryfragmentViewModel
                it.lifecycleOwner = viewLifecycleOwner

            }
        }

        return binding.mainContainer
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        /**
         * Api call for fetching FAQ and Glossary Details...
         */
        if (faqGlossaryfragmentViewModel.data.value==null) {
            bottomNavViewModel.showProgress.set(true)
            faqGlossaryfragmentViewModel.fetchData()
        }
        setUIEvents()
        setuptoolbar()
    }


    private fun setUIEvents() {
        faqGlossaryfragmentViewModel.disposable += faqGlossaryfragmentViewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)

            }
    }

    private fun setuptoolbar() {
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(false)
        bottomNavViewModel.visibility.set(false)
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back_button)
        (activity as? BottomNavigationActivity)?.hidemenu()
        toolbarViewModel?.isShowActionMenu.set(false)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    private fun handleEvent(event: FAQGlossaryfragmentViewModel.Event) =
        when (event) {
            is FAQGlossaryfragmentViewModel.Event.OnResultSuccess -> {
                if (faqGlossaryfragmentViewModel.data.value != null)
                    setTabsAdapter(faqGlossaryfragmentViewModel.data.value)

                Unit
            }
            FAQGlossaryfragmentViewModel.Event.OnShowProgress -> {
                bottomNavViewModel.showProgress.set(true)

            }
            FAQGlossaryfragmentViewModel.Event.OnHideProgress -> {
                bottomNavViewModel.showProgress.set(false)

            }
            FAQGlossaryfragmentViewModel.Event.OnResultFailed -> {
              showAlert()
            }
            FAQGlossaryfragmentViewModel.Event.NoInternet ->{
             showAlert()
            }
        }

    private fun showAlert() {
        val errorMessage = faqGlossaryfragmentViewModel.errorString.get() ?: ""
        Utility.getCommonAlertDialogue(requireContext(),"",errorMessage,"",getString(R.string.str_ok),this, Utility.AlertType.NETWORK
            ,Utility.Iconype.FAILED)
    }
    private fun setTabsAdapter(data: FaqGlossaryResponseDomain?) {
        val fragmentAdapter = activity?.supportFragmentManager?.let {
            TabFaqAdapter(
                it, data
            )
        }
        binding.viewPager.adapter = fragmentAdapter
        binding.tabLayoutFaq.setupWithViewPager(binding.viewPager)
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                Log.d("onPageScroll", "onPageScrollStateChanged")
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                Log.d("onPageScroll", "onPageScrolled")
            }

            override fun onPageSelected(position: Int) {
                Log.d("onPageScroll", "onPageSelected")
            }
        })

        try {
            val tabLayout =
                ( binding.tabLayoutFaq.getChildAt(0) as ViewGroup).getChildAt(0) as LinearLayout
            val tabTextView = tabLayout.getChildAt(1) as TextView
            val typeface = ResourcesCompat.getFont(requireContext(), R.font.avenir_next_lt_pro_demi)
            tabTextView.setTypeface(typeface)
        } catch (e: Exception) {
            Log.d("Error",e.localizedMessage)
        }
        binding.tabLayoutFaq.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Log.d("onTabSelection", "onTabSelected")
                val tabLayout =
                    ( binding.tabLayoutFaq.getChildAt(0) as ViewGroup).getChildAt(tab.position) as LinearLayout
                val tabTextView = tabLayout.getChildAt(1) as TextView
                val typeface = ResourcesCompat.getFont(context!!, R.font.avenir_next_lt_pro_demi)
                tabTextView.setTypeface(typeface)

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                Log.d("onTabSelection", "onTabUnselected")
                val tabLayout =
                    ( binding.tabLayoutFaq.getChildAt(0) as ViewGroup).getChildAt(tab.position) as LinearLayout
                val tabTextView = tabLayout.getChildAt(1) as TextView
                val typeface = ResourcesCompat.getFont(context!!, R.font.avenir_next_lt_pro_regular)
                tabTextView.setTypeface(typeface)

            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                Log.d("onTabSelection", "onTabReselected")
            }
        })
    }

    override fun onCustomPositiveButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        //TODO("Not yet implemented")
    }

    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        //TODO("Not yet implemented")
    }
}
