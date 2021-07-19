package com.ditto.home.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.ditto.home.ui.adapter.HomeAdapter
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.example.home_ui.R
import com.example.home_ui.databinding.HomeFragmentBinding
import core.appstate.AppState
import core.event.RxBus
import core.event.RxBusEvent
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.home_fragment.*
import javax.inject.Inject


class HomeFragment : BaseFragment(), Utility.CustomCallbackDialogListener {

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

    @SuppressLint("CheckResult")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bottomNavViewModel.visibility.set(false)
        bottomNavViewModel.refreshMenu(context)
        (activity as BottomNavigationActivity)?.refreshMenuItem()
        if (AppState.getIsLogged()) {
            bottomNavViewModel.isGuestBase.set(false)
        } else {
            bottomNavViewModel.isGuestBase.set(true)
        }
        toolbarViewModel.isShowActionBar.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(true)
       setHomeAdapter()

        /**
         * API call for getting pattern details....
         */
        if (AppState.getIsLogged()) {
            if (!Utility.isTokenExpired()) {
                bottomNavViewModel.showProgress.set(true)
                homeViewModel.fetchData()
            }
        }

        homeViewModel.disposable += homeViewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }

        RxBus.listen(RxBusEvent.isTokenRefreshed::class.java)
            .subscribe {
                Log.d("TOKEN======", "SUCCESSS")
                if (AppState.getIsLogged()) {
                    homeViewModel.fetchData()
                }
            }


    }


    private fun handleEvent(event: HomeViewModel.Event) =
        when (event) {
            is HomeViewModel.Event.OnClickDitto -> {
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
            is HomeViewModel.Event.OnClickMyPatterns -> {
                val list=homeViewModel.homeDataResponse.value!!.prod

                if (findNavController().currentDestination?.id == R.id.homeFragment) {
                    val bundle = bundleOf(
                        "clickedID" to context?.let { Utility.getSharedPref(it) },
                        "isFrom" to "RESUME_RECENT","PATTERNS" to  list
                    )
                    findNavController().navigate(R.id.action_home_to_my_library, bundle)
                } else {
                    logger.d("OnClickResumeRecent failed")
                }
            }
            HomeViewModel.Event.OnClickTutorial -> {
                if (findNavController().currentDestination?.id == R.id.homeFragment) {
                    val bundle = bundleOf("isFromHome" to true)
                    findNavController().navigate(R.id.action_home_to_tutorial, bundle)
                } else {
                    logger.d("OnClickJoann failed")
                }
            }
            HomeViewModel.Event.OnResultSuccess -> {
                bottomNavViewModel.showProgress.set(false)
                (recycler_view.adapter as HomeAdapter).setListData(homeViewModel.homeItem)
                (recycler_view.adapter as HomeAdapter).notifyDataSetChanged()
                logger.d("PATTERNS=  :  $homeViewModel.homeDataResponse")

            }
            HomeViewModel.Event.OnShowProgress -> {
                bottomNavViewModel.showProgress.set(true)

            }
            HomeViewModel.Event.OnHideProgress -> {
                bottomNavViewModel.showProgress.set(false)


            }
            HomeViewModel.Event.OnResultFailed -> {
                bottomNavViewModel.showProgress.set(false)
                showAlert()

            }
            HomeViewModel.Event.NoInternet -> {
                bottomNavViewModel.showProgress.set(false)
                showAlert()
            }
        }

    private fun showAlert() {
        val errorMessage = homeViewModel.errorString.get() ?: ""
        Utility.getCommonAlertDialogue(
            requireContext(),
            "",
            errorMessage,
            "",
            getString(R.string.str_ok),
            this,
            Utility.AlertType.NETWORK
            ,
            Utility.Iconype.FAILED
        )
    }

    private fun setHomeAdapter() {
        val adapter = HomeAdapter(requireContext())
        recycler_view.adapter = adapter
        adapter.viewModel = homeViewModel
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
        // TODO("Not yet implemented")
    }

}
