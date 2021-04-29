package com.ditto.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.home_ui.databinding.HomeFragmentBinding
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.example.home_ui.R
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject


class HomeFragment : BaseFragment() {

    @Inject
    lateinit var loggerFactory: LoggerFactory

    val logger: Logger by lazy {
        loggerFactory.create(HomeFragment::class.java.simpleName)
    }
    private val homeViewModel: HomeViewModel by ViewModelDelegate()
    lateinit var binding: HomeFragmentBinding
    var toolbar: Toolbar? = null

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(
            inflater
        ).also {
            it.viewModel = homeViewModel
            it.lifecycleOwner = this
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bottomNavViewModel.visibility.set(true)
        bottomNavViewModel.refreshMenu(context)
        (activity as BottomNavigationActivity)?.setMenuItem(bottomNavViewModel.isGuestBase.get())
        toolbarViewModel.isShowActionBar.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(true)
        homeViewModel.disposable += homeViewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    private fun handleEvent(event: HomeViewModel.Event) =
        when (event) {
            is HomeViewModel.Event.OnClickBuyPattern -> {
                if (findNavController().currentDestination?.id == R.id.homeFragment) {
                    findNavController().navigate(R.id.action_home_to_buy_pattern)
                } else {
                    logger.d("OnClickBuyPattern failed")
                }
            }
            is HomeViewModel.Event.OnClickJoann -> {
                if (findNavController().currentDestination?.id == R.id.homeFragment) {
                    findNavController().navigate(R.id.action_homeFragment_to_ShopFragment)
                } else {
                    logger.d("OnClickJoann failed")
                }
            }
            is HomeViewModel.Event.OnClickResumeRecent -> {
                if (findNavController().currentDestination?.id == R.id.homeFragment) {
                    if(context?.let { Utility.getSharedPref(it) }!=0){
                        val bundle = bundleOf("clickedID" to context?.let { Utility.getSharedPref(it) },"isFrom" to "RESUME_RECENT")
                        findNavController().navigate(R.id.action_home_to_pattern_details,bundle)
                    }else{
                        logger.d("OnClickResumeRecent - No Recent Resume Items")
                    }
                } else {
                    logger.d("OnClickResumeRecent failed")
                }
            }
        }

}
