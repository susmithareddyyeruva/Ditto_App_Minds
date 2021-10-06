package com.ditto.menuitems_ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ditto.menuitems_ui.R
import com.ditto.menuitems_ui.databinding.WorkspaceSettingsFragmentBinding
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.workspace_settings_fragment.*

class WSProSettingsFragment : BaseFragment() {

    private val viewModel: WSProSettingViewModel by ViewModelDelegate()
    lateinit var binding: WorkspaceSettingsFragmentBinding

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
        }

    private fun handleSwitchListener(){
        switch_mirroringreminder.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setMirrorReminderData(isChecked)
            //viewModel.postBooleanDataForSettings()
        }
        switch_splicing.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setSplicingNotification(isChecked)
            //viewModel.postBooleanDataForSettings()
        }
        switch_multiple_piece.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setSplicingWithMultiple(isChecked)
            //viewModel.postBooleanDataForSettings()
        }
        switch_cutnumber.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setCutNumberSplicing(isChecked)
            //viewModel.postBooleanDataForSettings()
        }

    }

    private fun setuptoolbar(){
        bottomNavViewModel.visibility.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(true)
        toolbarViewModel.isShowActionMenu.set(false)
        (activity as BottomNavigationActivity).setToolbarTitle(getString(R.string.str_menu_ws_pro_settings))
        (activity as BottomNavigationActivity).setToolbarIcon()
    }
}