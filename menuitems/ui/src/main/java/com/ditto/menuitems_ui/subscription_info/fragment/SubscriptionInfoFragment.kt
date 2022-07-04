package com.ditto.menuitems_ui.subscription_info.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ditto.menuitems_ui.databinding.FragmentSubscriptionInfoBinding
import core.lib.BuildConfig
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign


class SubscriptionInfoFragment : BaseFragment() {

    private val viewModel: SubscriptionInfoViewModel by ViewModelDelegate()

    lateinit var binding: FragmentSubscriptionInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as BottomNavigationActivity).hideDrawerLayout()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubscriptionInfoBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.ccContainer
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setuptoolbar()
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    private fun handleEvent(event: SubscriptionInfoViewModel.Event) =
        when (event) {
            SubscriptionInfoViewModel.Event.onRenewSubscriptionClick -> {
                //logger.d("onSubscriptionClicked")
                Utility.redirectToExternalBrowser(
                    requireContext(), BuildConfig.SUBSCRIPTION_URL
                    //"https://development.dittopatterns.com/on/demandware.store/Sites-ditto-Site/default/Recurly-GetSubscriptionPlan"
                )
            }

        }


    private fun setuptoolbar() {
        bottomNavViewModel.visibility.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(true)
        toolbarViewModel.isShowActionMenu.set(false)
        (activity as BottomNavigationActivity).setToolbarIcon()
        (activity as BottomNavigationActivity).setToolbarTitle("Subscription Info")
    }

}