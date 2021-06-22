package com.ditto.login.ui

import android.content.pm.PackageInfo
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.findNavController
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.login.ui.adapter.LoginViewPagerAdapter
import com.ditto.login.ui.databinding.LoginFragmentBinding
import core.network.NetworkUtility
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.login_fragment.*
import javax.inject.Inject


class LoginFragment : BaseFragment(),Utility.CustomCallbackDialogListener  {

    @Inject
    lateinit var loggerFactory: LoggerFactory

    val logger: Logger by lazy {
        loggerFactory.create(LoginFragment::class.java.simpleName)
    }

    private val viewModel: LoginViewModel by ViewModelDelegate()
    lateinit var binding: LoginFragmentBinding


    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        binding = LoginFragmentBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.rootLayout
    }

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val pinfo: PackageInfo = requireActivity().getPackageManager()
            .getPackageInfo(requireActivity().getPackageName(), 0)
        viewModel.versionName.set("Version " + pinfo.versionName)
        if (savedInstanceState == null) {
            viewModel.disposable += viewModel.events
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    handleEvent(it)   //Observing UI event
                }

        }

        viewModel.fetchViewPagerData()
        Log.d("list123", "${viewModel.viewPagerData.value?.size}")
        setViewpagerImageAdapter()
        setUIEvents()
        //setupKeyboardListener(binding.root) // call in OnCreate or similar

    }

    private fun setViewpagerImageAdapter() {
        val adapter = LoginViewPagerAdapter()
        login_view_pager.adapter = adapter
        adapter.viewModel = viewModel
        login_view_pager.adapter?.notifyDataSetChanged()
        login_tablay.setupWithViewPager(login_view_pager)

        viewModel.viewPagerData.value?.let {
            if (it != null) {
                adapter.setListData(it)
            }
        }
        if (bottomNavViewModel.isLogoutEvent.get()){
            Log.d("LOGIN SCREEN ","LOGOUT HAPPENED")
            viewModel.deleteUserInfo()
            bottomNavViewModel.isLogoutEvent.set(false)
        }


    }

    private fun setUIEvents() {
        binding.edittextPassword.customSelectionActionModeCallback =
            object : ActionMode.Callback {
                override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                    return false
                }

                override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    return false
                }

                override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    return false
                }

                override fun onDestroyActionMode(mode: ActionMode?) {
                    Log.d("actionMode", "onDestroy")
                }
            }

        //for samsung keyboard
        binding.edittextPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.d("onTextChange", "After")

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("onTextChange", "Before")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 1)
                    binding.edittextPassword.setText("")
            }

        })

    }

    private fun handleEvent(event: LoginViewModel.Event) =
        when (event) {
            is LoginViewModel.Event.OnLoginClicked -> {
                getUserDetails(false)
                //Re directing to On_boarding screen
                val bundle = bundleOf("UserId" to 0)
                if (findNavController().currentDestination?.id == R.id.destination_login) {
                    findNavController().navigate(R.id.action_loginFragment_to_VideoFragment, bundle)
                } else {

                }
            }
            is LoginViewModel.Event.OnSeeMoreClicked -> {
                if (NetworkUtility.isNetworkAvailable(requireContext())){
                    val bundle = bundleOf("UserId" to 0)
                    if (findNavController().currentDestination?.id == R.id.destination_login) {
                        getUserDetails(true)
                        findNavController().navigate(R.id.action_loginFragment_to_VideoFragment, bundle)
                    } else {

                    }
                } else {
                    viewModel.errorString.set(getString(R.string.no_internet_available))
                    showAlert()
                }

            }
            is LoginViewModel.Event.OnLoginFailed -> {
                showAlert()
            }
            LoginViewModel.Event.OnHideProgress -> bottomNavViewModel.showProgress.set(false)
            LoginViewModel.Event.OnShowProgress -> bottomNavViewModel.showProgress.set(true)
        }

    private fun setupKeyboardListener(view: View) {
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            view.getWindowVisibleDisplayFrame(r)
            if (Math.abs(view.rootView.height - (r.bottom - r.top)) > 100) { // if more than 100 pixels, its probably a keyboard...
                onKeyboardShow()
            }
        }
    }

    private fun getUserDetails(isGuest : Boolean) {
        bottomNavViewModel.isGuestBase.set(isGuest)
        bottomNavViewModel.userEmailBase.set(viewModel.userEmail)
        bottomNavViewModel.userPhoneBase.set(viewModel.userPhone)
        bottomNavViewModel.userFirstNameBase.set(viewModel.userFirstName)
        bottomNavViewModel.userLastNameBase.set(viewModel.userLastName)
    }

    private fun onKeyboardShow() {
        binding.root.scrollToBottomWithoutFocusChange()
    }

    fun NestedScrollView.scrollToBottomWithoutFocusChange() { // Kotlin extension to scrollView
        val lastChild = getChildAt(childCount - 1)
        val bottom = lastChild.bottom + paddingBottom
        val delta = bottom - (scrollY + height)
        smoothScrollBy(0, delta) //***/2 *****3/2
    }

    private fun showAlert() {
        val errorMessage = viewModel.errorString.get() ?: ""
        Utility.getCommonAlertDialogue(requireContext(),"",errorMessage,"",getString(R.string.str_ok),this, Utility.AlertType.NETWORK
        ,Utility.Iconype.FAILED)
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
