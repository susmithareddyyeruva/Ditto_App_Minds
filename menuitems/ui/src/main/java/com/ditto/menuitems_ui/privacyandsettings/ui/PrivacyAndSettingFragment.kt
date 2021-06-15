package com.ditto.menuitems_ui.privacyandsettings.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.ditto.menuitems_ui.R
import com.ditto.menuitems_ui.databinding.FragmentPrivacyAndSettingBinding
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate


class PrivacyAndSettingFragment : BaseFragment() {

    private val viewModel: PrivacyAndSettingsViewModel by ViewModelDelegate()
    lateinit var binding: FragmentPrivacyAndSettingBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrivacyAndSettingBinding.inflate(inflater).also {
            it.viewmodel = viewModel
            it.lifecycleOwner=viewLifecycleOwner
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setuptoolbar()
    }

    private fun setuptoolbar() {
       bottomNavViewModel.visibility.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(true)
        toolbarViewModel.isShowActionMenu.set(false)
        (activity as BottomNavigationActivity).hidemenu()
        (activity as BottomNavigationActivity).setToolbarTitle(getString(R.string.privacy_policy))
        (activity as BottomNavigationActivity).setToolbarIcon()
    }
}