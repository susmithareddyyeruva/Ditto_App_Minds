package com.ditto.login.ui

import android.content.Intent
import android.content.pm.PackageInfo
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
import com.bumptech.glide.Glide
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.login.ui.databinding.LoginFragmentBinding
import com.ditto.videoplayer.CustomPlayerControlActivity
import core.appstate.AppState
import core.network.NetworkUtility
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject


class LoginFragment : BaseFragment(), Utility.CustomCallbackDialogListener {

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data?.data.toString().equals("SUCCESS")) {
            getUserDetails(false)
            //Re directing to Video Screen

            val bundle = bundleOf("UserId" to 0)
            if (findNavController().currentDestination?.id == R.id.destination_login) {
              if (AppState.getIsLogged()){
                  getUserDetails(false)
              }else{
                  getUserDetails(true)
              }

                findNavController().navigate(
                    R.id.action_loginFragment_to_OnboardingFragment,
                    bundle
                )
            }
        }
    }

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val pinfo: PackageInfo = requireActivity().getPackageManager()
            .getPackageInfo(requireActivity().getPackageName(), 0)
        viewModel.versionName.set("Version " + pinfo.versionName)
        bottomNavViewModel.showProgress.set(true)
        viewModel.getLandingScreenDetails()/**Fetch Landing screen Details.....*/
        if (savedInstanceState == null) {
            viewModel.disposable += viewModel.events
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    handleEvent(it)   //Observing UI event
                }

        }

        Log.d("list123", "${viewModel.viewPagerData.value?.size}")
        setUIEvents()
        //setupKeyboardListener(binding.root) // call in OnCreate or similar

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
                //Re directing to Video Screen
                if (findNavController().currentDestination?.id == R.id.destination_login) {
                    val bundle = bundleOf(
                        "UserId" to 0,
                        "videoPath" to viewModel.videoUrl,
                        "title" to "Ditto application overview",
                        "from" to "LOGIN"
                    )
                    val intent = Intent(requireContext(), CustomPlayerControlActivity::class.java).putExtras(bundle)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                    startActivityForResult(intent, 200)

                    //  findNavController().navigate(R.id.action_loginFragment_to_OnboardingFragment, bundle)
                } else {
                    logger.d("condition false")

                }
            }
            is LoginViewModel.Event.OnSeeMoreClicked -> {
                if (NetworkUtility.isNetworkAvailable(requireContext())){
                    if (findNavController().currentDestination?.id == R.id.destination_login) {
                        getUserDetails(true)
                        val bundle = bundleOf("UserId" to 0,
                            "videoPath" to viewModel.videoUrl,
                            "title" to "Ditto application overview",
                            "from" to "LOGIN"
                        )
                        val intent = Intent(requireContext(), CustomPlayerControlActivity::class.java).putExtras(bundle)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivityForResult(intent, 200)
                        // findNavController().navigate(R.id.action_loginFragment_to_VideoFragment, bundle)
                    } else {

                    }
                } else {
                    bottomNavViewModel.showProgress.set(false)
                    viewModel.errorString.set(getString(R.string.no_internet_available))
                    showAlert()
                }

            }
            is LoginViewModel.Event.OnLoginFailed -> {
                bottomNavViewModel.showProgress.set(false)
                showAlert()
            }
            LoginViewModel.Event.OnHideProgress -> bottomNavViewModel.showProgress.set(false)
            LoginViewModel.Event.OnShowProgress -> bottomNavViewModel.showProgress.set(true)
            LoginViewModel.Event.OnLandingSuccess ->{
             setLandingImage()
            }
        }

    private fun setLandingImage() {
        viewModel.imageUrl.let {
            Glide.with( binding.ivViewpagerLogin.context)
                .load(it.get())
                .placeholder(R.drawable.ic_placeholder)
                .into( binding.ivViewpagerLogin)
        }
    }

    private fun getUserDetails(isGuest: Boolean) {
        bottomNavViewModel.isGuestBase.set(isGuest)
        bottomNavViewModel.userEmailBase.set(viewModel.userEmail)
        bottomNavViewModel.userPhoneBase.set(viewModel.userPhone)
        bottomNavViewModel.userFirstNameBase.set(viewModel.userFirstName)
        bottomNavViewModel.userLastNameBase.set(viewModel.userLastName)
        bottomNavViewModel.subscriptionEndDateBase.set(viewModel.subscriptionEndDate)
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

    private fun showSnackBar() {
        val errorMessage = viewModel.errorString.get() ?: ""
        Utility.showSnackBar(
            errorMessage,
            binding.rootLayout
        )
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
            Utility.AlertType.NETWORK
            ,
            Utility.Iconype.FAILED
        )
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
