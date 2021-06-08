package com.ditto.menuitems_ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ditto.menuitems_ui.databinding.WorkspaceSettingsFragmentBinding
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setuptoolbar()
    }
    private fun setuptoolbar(){
        bottomNavViewModel.visibility.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(true)
        toolbarViewModel.isShowActionMenu.set(false)
        (activity as BottomNavigationActivity).hidemenu()
        (activity as BottomNavigationActivity).setToolbarTitle("Workspace settings")
    }
}