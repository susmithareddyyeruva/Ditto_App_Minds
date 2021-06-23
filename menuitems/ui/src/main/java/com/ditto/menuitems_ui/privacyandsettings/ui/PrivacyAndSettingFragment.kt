package com.ditto.menuitems_ui.privacyandsettings.ui

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.ditto.menuitems_ui.R
import com.ditto.menuitems_ui.databinding.FragmentPrivacyAndSettingBinding
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign


class PrivacyAndSettingFragment : BaseFragment() ,Utility.CustomCallbackDialogListener{

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
        if (core.network.Utility.isNetworkAvailable(requireContext())){
            bottomNavViewModel.showProgress.set(true)
            viewModel.fetchUserData()
        }else{
            bottomNavViewModel.showProgress.set(false)
            showAlert()
        }

        if (savedInstanceState == null) {
            viewModel.disposable += viewModel.events
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    handleEvent(it)
                }
        }
    }
    private fun showAlert() {
        val errorMessage = "No Internet connection available !"
        Utility.getCommonAlertDialogue(requireContext(),"",errorMessage,"",getString(R.string.str_ok),this, Utility.AlertType.NETWORK
            ,Utility.Iconype.FAILED)
    }

    private fun handleEvent(event: PrivacyAndSettingsViewModel.Event) {
        when (event) {
            PrivacyAndSettingsViewModel.Event.onResultSuccess -> {
                binding.webPrivacy.loadDataWithBaseURL(
                    null,
                    viewModel.data,
                    "text/html",
                    "UTF-8",
                    null
                )
                binding.webPrivacy.requestFocus()
                binding.webPrivacy.settings.javaScriptEnabled = true
                binding.webPrivacy.webViewClient = object : WebViewClient() {
                    @RequiresApi(Build.VERSION_CODES.M)
                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                        Log.d("Error", "$error.description")
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)

                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        bottomNavViewModel.showProgress.set(false)

                    }
                }


            }

            PrivacyAndSettingsViewModel.Event.OnShowProgress -> bottomNavViewModel.showProgress.set(true)
            PrivacyAndSettingsViewModel.Event.OnHideProgress -> bottomNavViewModel.showProgress.set(false)
            PrivacyAndSettingsViewModel.Event.NoNetworkError ->{
                bottomNavViewModel.showProgress.set(false)
                showAlert()
            }
        }
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
      //  TODO("Not yet implemented")
    }
}