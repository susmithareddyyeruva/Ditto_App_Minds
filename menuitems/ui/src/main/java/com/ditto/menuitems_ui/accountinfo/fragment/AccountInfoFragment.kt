package com.ditto.menuitems_ui.accountinfo.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.ditto.menuitems_ui.R
import com.ditto.menuitems_ui.databinding.FragmentAccountInfoBinding
import com.ditto.menuitems_ui.faq.ui.FAQGlossaryFragmentViewModel
import core.appstate.AppState
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign


class AccountInfoFragment : BaseFragment(), Utility.CustomCallbackDialogListener {
    private val viewModel: AccountInfoViewModel by ViewModelDelegate()
    lateinit var binding: FragmentAccountInfoBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountInfoBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }


        return binding.ccContainer
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setuptoolbar()
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)   //Observing UI event
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as BottomNavigationActivity).hideDrawerLayout()
    }

    private fun handleEvent(event: AccountInfoViewModel.Event) =
        when (event) {
            AccountInfoViewModel.Event.onDeleteAccountClick ->{
                deleteAccount()
            }
            AccountInfoViewModel.Event.onLogout -> {
                logoutUser()
            }
            AccountInfoViewModel.Event.NoInternet -> {
                bottomNavViewModel.showProgress.set(false)
                showAlert()
            }
            AccountInfoViewModel.Event.OnResultFailed -> {
                bottomNavViewModel.showProgress.set(false)
                showAlert()
            }
        }

    fun deleteAccount() {
        Utility.getCommonAlertDialogue(
            requireContext(),
            "",
            getString(R.string.delete_msg),
            getString(R.string.str_yes_caps),
            getString(R.string.str_no_caps),
            this,
            Utility.AlertType.SOFTWARE_UPDATE,
            Utility.Iconype.WARNING
        )
    }

    private fun showAlert() {
        val errorMessage = viewModel.errorString.get() ?: ""
        Utility.getCommonAlertDialogue(requireContext(),"",errorMessage,"",getString(R.string.str_ok),this, Utility.AlertType.NETWORK
            ,Utility.Iconype.FAILED)
    }

    private fun logoutUser() {
        bottomNavViewModel.showProgress.set(true)
        AppState.logout()
        AppState.setIsLogged(false)
        bottomNavViewModel?.isGuestBase?.set(true)
        bottomNavViewModel?.userEmailBase?.set("")
        bottomNavViewModel?.userFirstNameBase?.set("")
        bottomNavViewModel?.userLastNameBase?.set("")
        bottomNavViewModel?.userPhoneBase?.set("")
        val id = findNavController().currentDestination?.id
        findNavController().navigate(
            R.id.action_account_info_to_login
        )
    }

    private fun setuptoolbar() {
        bottomNavViewModel.visibility.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(true)
        toolbarViewModel.isShowActionMenu.set(false)
        (activity as BottomNavigationActivity).setToolbarIcon()
        (activity as BottomNavigationActivity).setToolbarTitle("Account Info")
    }

    override fun onCustomPositiveButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {

    }

    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        bottomNavViewModel.showProgress.set(true)
        viewModel.deleteAccount()
    }
}