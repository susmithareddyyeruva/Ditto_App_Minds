package com.ditto.login.ui

import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
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
                if (AppState.getIsLogged()) {
                    getUserDetails(false)
                } else {
                    getUserDetails(true)
                }

                findNavController().navigate(
                    R.id.action_loginFragment_to_HomeFragment,
                    bundle
                )
            }
        }
    }

    //for hiding the navigation drawer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as BottomNavigationActivity).hideDrawerLayout()
    }

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(true)
        toolbarViewModel.isShowActionMenu.set(false)
        val pinfo: PackageInfo = requireActivity().getPackageManager()
            .getPackageInfo(requireActivity().getPackageName(), 0)
        viewModel.versionName.set("Version " + pinfo.versionName)
        bottomNavViewModel.showProgress.set(true)
        viewModel.getLandingScreenDetails()
        /**Fetch Landing screen Details.....*/
        if (savedInstanceState == null) {
            viewModel.disposable += viewModel.events
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    handleEvent(it)   //Observing UI event
                }

        }

        logger.d("list123, ${viewModel.viewPagerData.value?.size}")
        setUIEvents()
        requireActivity().window.navigationBarColor =
            resources.getColor(core.lib.R.color.nav_item_grey2);
        requireActivity().window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR; //For setting material color into black of the navigation bar

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
                    logger.d("actionMode, onDestroy")
                }
            }

        //for samsung keyboard
        binding.edittextPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                logger.d("onTextChange, After")

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                logger.d("onTextChange, Before")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                /*if (count > 1)
                    binding.edittextPassword.setText("")*/
            }

        })

    }

    private fun handleEvent(event: LoginViewModel.Event) =
        when (event) {

            is LoginViewModel.Event.OnLoginClicked -> {
                //redirectToHomeScreen()
                getUserDetails(false)
                //Re directing to Video Screen

                logger.d("LandingDetails  videoUrl : ${viewModel.videoUrl}")

                if (findNavController().currentDestination?.id == R.id.destination_login) {
                    //removing opening video & navigate to home screen directly
                    /*val bundle = bundleOf(
                        "UserId" to 0,
                           "videoPath" to viewModel.videoUrl,
                        "title" to "Ditto application overview",
                        "from" to "LOGIN"
                    )
                    val intent =
                        Intent(requireContext(), CustomPlayerControlActivity::class.java).putExtras(
                            bundle
                        )
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                    startActivityForResult(intent, 200)*/

                    getUserDetails(false)

                    val bundle = bundleOf("UserId" to 0)
                    if (findNavController().currentDestination?.id == R.id.destination_login) {
                        if (AppState.getIsLogged()) {
                            getUserDetails(false)
                        } else {
                            getUserDetails(true)
                        }

                        findNavController().navigate(
                            R.id.action_loginFragment_to_HomeFragment,
                            bundle
                        )
                    } else {
                        //
                    }

                    //  findNavController().navigate(R.id.action_loginFragment_to_OnboardingFragment, bundle)
                } else {
                    logger.d("condition false")

                }
            }
            is LoginViewModel.Event.OnGuestPreviewClicked -> {

                //redirectToHomeScreen()
                //removing opening video & navigate to home screen directly
                // if (NetworkUtility.isNetworkAvailable(requireContext())){
                     /*if (findNavController().currentDestination?.id == R.id.destination_login) {
                         getUserDetails(true)
                         val bundle = bundleOf(
                             "UserId" to 0,
                             "videoPath" to viewModel.videoUrl,
                             "title" to "Ditto application overview",
                             "from" to "LOGIN"
                         )
                         val intent = Intent(
                             requireContext(),
                             CustomPlayerControlActivity::class.java
                         ).putExtras(bundle)
                         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                         startActivityForResult(intent, 200)
                         // findNavController().navigate(R.id.action_loginFragment_to_VideoFragment, bundle)
                     } else {

                     }*/

                getUserDetails(false)

                val bundle = bundleOf("UserId" to 0)
                if (findNavController().currentDestination?.id == R.id.destination_login) {
                    if (AppState.getIsLogged()) {
                        getUserDetails(false)
                    } else {
                        getUserDetails(true)
                    }

                    findNavController().navigate(
                        R.id.action_loginFragment_to_HomeFragment,
                        bundle
                    )
                } else {
                    //do nothing
                }

                /*} else {
                    bottomNavViewModel.showProgress.set(false)
                    viewModel.errorString.set(getString(R.string.no_internet_available))
                    showAlert()
                }*/

            }
            is LoginViewModel.Event.OnLoginFailed -> {
                bottomNavViewModel.showProgress.set(false)
                showAlert()
            }
            LoginViewModel.Event.OnHideProgress -> bottomNavViewModel.showProgress.set(false)
            LoginViewModel.Event.OnShowProgress -> bottomNavViewModel.showProgress.set(true)
            LoginViewModel.Event.OnLandingSuccess -> {
                setLandingImage()
            }
        }

    private fun redirectToHomeScreen() {
        val bundle = bundleOf("UserId" to 0)
        if (findNavController().currentDestination?.id == R.id.destination_login) {
            if (AppState.getIsLogged()) {
                getUserDetails(false)
            } else {
                getUserDetails(true)
            }

            findNavController().navigate(
                R.id.action_loginFragment_to_HomeFragment,
                bundle
            )
        }
    }

    private fun setLandingImage() {
        if (requireContext()!=null) {
            viewModel.imageUrl.let {
                Glide.with(requireContext())
                    .load(it.get())
                    .placeholder(R.drawable.ic_placeholder)
                    .into(binding.ivViewpagerLogin)
            }
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
            Utility.AlertType.NETWORK,
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
