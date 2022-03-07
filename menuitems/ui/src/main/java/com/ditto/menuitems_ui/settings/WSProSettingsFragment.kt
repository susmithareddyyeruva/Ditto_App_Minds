package com.ditto.menuitems_ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.menuitems_ui.R
import com.ditto.menuitems_ui.databinding.WorkspaceSettingsFragmentBinding
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.workspace_settings_fragment.*
import javax.inject.Inject

class WSProSettingsFragment : BaseFragment(), Utility.CustomCallbackDialogListener {

    private val viewModel: WSProSettingViewModel by ViewModelDelegate()
    lateinit var binding: WorkspaceSettingsFragmentBinding

    @Inject
    lateinit var loggerFactory: LoggerFactory

    val logger: Logger by lazy {
        loggerFactory.create(WSProSettingsFragment::class.java.simpleName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = WorkspaceSettingsFragmentBinding.inflate(
            inflater
        ).also {
            it.viewmodel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as BottomNavigationActivity).hideDrawerLayout()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setuptoolbar()
        viewModel.fetchUserData()
        if (savedInstanceState == null) {
            viewModel.disposable += viewModel.events
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    handleEvent(it)
                }

        }
    }

    private fun handleEvent(event: WSProSettingViewModel.Event) =
        when (event) {
            WSProSettingViewModel.Event.OnShowProgress -> bottomNavViewModel.showProgress.set(true)
            WSProSettingViewModel.Event.OnHideProgress -> bottomNavViewModel.showProgress.set(false)
            WSProSettingViewModel.Event.OnFetchComplete -> handleSwitchListener()
            WSProSettingViewModel.Event.NoInternet -> showAlert()
            WSProSettingViewModel.Event.OnResultFailed -> showAlert()
        }

    private fun showAlert() {
        val errorMessage = viewModel.errorString.get() ?: ""
        Utility.getCommonAlertDialogue(
            requireContext(),
            "",
            errorMessage,
            "",
            getString(R.string.str_ok),
            this,
            Utility.AlertType.NETWORK,
            Utility.Iconype.FAILED
        )
    }

    private fun handleSwitchListener() {
        switch_mirroringreminder.setOnCheckedChangeListener { _, isChecked ->
            if (!viewModel.isFromErrorPopUp.get()) {
                viewModel.setMirrorReminderData(isChecked)
            }else{
                viewModel.isFromErrorPopUp.set(false)
            }
            //viewModel.postBooleanDataForSettings()
        }
        switch_splicing.setOnCheckedChangeListener { _, isChecked ->
            if (!viewModel.isFromErrorPopUp.get()) {
                viewModel.setSplicingNotification(isChecked)
            }else{
                viewModel.isFromErrorPopUp.set(false)
            }
            //viewModel.postBooleanDataForSettings()
        }
        switch_multiple_piece.setOnCheckedChangeListener { _, isChecked ->
            if (!viewModel.isFromErrorPopUp.get()) {
                viewModel.setSplicingWithMultiple(isChecked)
            }else{
                viewModel.isFromErrorPopUp.set(false)
            }
            //viewModel.postBooleanDataForSettings()
        }
        switch_cutnumber.setOnCheckedChangeListener { _, isChecked ->

            if (!viewModel.isFromErrorPopUp.get()) {
                viewModel.setCutNumberSplicing(isChecked)
            }else{
                viewModel.isFromErrorPopUp.set(false)
            }
            //viewModel.postBooleanDataForSettings()
        }

    }

    private fun setuptoolbar() {
        bottomNavViewModel.visibility.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(true)
        toolbarViewModel.isShowActionMenu.set(false)
        (activity as BottomNavigationActivity).setToolbarTitle(getString(R.string.str_menu_ws_pro_settings))
        (activity as BottomNavigationActivity).setToolbarIcon()
    }

    override fun onCustomPositiveButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        viewModel.isFromErrorPopUp.set(true)
        viewModel.fetchUserData()
    }

    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        logger.d("onCustomNegativeButtonClicked")
    }
}