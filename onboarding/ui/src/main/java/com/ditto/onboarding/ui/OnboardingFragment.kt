package com.ditto.onboarding.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.onboarding.ui.adapter.OnboardingAdapter
import com.ditto.onboarding.ui.databinding.OnboardingFragmentBinding
import com.ditto.onboarding.util.ONBOARDING
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.onboarding_fragment.*
import javax.inject.Inject

class OnboardingFragment : BaseFragment(), Utility.CustomCallbackDialogListener {

    @Inject
    lateinit var loggerFactory: LoggerFactory
    var isFromHomeScreen: Boolean = false
    var isWifiAlert: Boolean = false
    val logger: Logger by lazy {
        loggerFactory.create(OnboardingFragment::class.java.simpleName)
    }

    private val viewModel: OnboardingViewModel by ViewModelDelegate()
    lateinit var binding: OnboardingFragmentBinding

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        binding = OnboardingFragmentBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.container
    }

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.getInt(USERID)?.let { viewModel.userId = (it) }
        arguments?.getBoolean(ISFROMHOME)?.let { isFromHomeScreen = (it) }
//        arguments?.getBoolean("isFromWorkspace")?.let { isFromWorkspaceScreen = (it) }
        viewModel.isFromHome_Observable.set(isFromHomeScreen)
        (activity as BottomNavigationActivity).hidemenu()
         if (core.network.Utility.isNetworkAvailable(requireContext())) {
            bottomNavViewModel.showProgress.set(true)
            viewModel.fetchOnBoardingDataFromApi()

        } else {
            viewModel.fetchOnBoardingData()
        }
        //viewModel.fetchOnBoardingData()
        setOnBoardingAdapter()
        setUIEvents()
        setToolbar()
        setHeadingTitle()
        checkBluetoothWifiPermission()
        logger.d(bottomNavViewModel.userFirstNameBase.get().toString())
    }


    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 111
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.BLUETOOTH)
        private const val ISFROMHOME = "isFromHome"
        private const val USERID = "UserId"
        private const val INSTRUCTIONID = "InstructionId"
        private const val ISDNDCHECKED = "ISDNDCHECKED"
    }

    private fun checkBluetoothWifiPermission() {

        if (allPermissionsGranted()) {

            if (!Utility.getBluetoothstatus()) {
                isWifiAlert = false
                showBluetoothDialogue()
            } else if (!Utility.getWifistatus(requireContext())) {
                isWifiAlert = true
                showWifiDialogue()
                logger.d("wifiefi222" + viewModel.isWifiLaterClicked.get().toString())
            }
        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                checkBluetoothWifiPermission()
            } else {
                logger.d("Permission Denied by the user")
                Toast.makeText(
                    requireContext(),
                    getString(R.string.turn_on_permission),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        context?.let { it1 ->
            ContextCompat.checkSelfPermission(
                it1, it
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    private fun setUIEvents() {
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)

            }
    }

    private fun setOnBoardingAdapter() {
        val adapter = OnboardingAdapter()
        recycler_view.adapter = adapter
        adapter.viewModel = viewModel
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun handleEvent(event: OnboardingViewModel.Event) =
        when (event) {
            is OnboardingViewModel.Event.OnClickSkipAndContinue -> { //Click on Skip & Continue
                if (findNavController().currentDestination?.id == R.id.destination_onboarding) {
                    navigateHomeScreen()
                }
                Unit
            }

            is OnboardingViewModel.Event.OnShowBleDialogue -> {
                logger.d("dialog Show bluetooth dialog")
            }
            is OnboardingViewModel.Event.OnItemClick -> {  //Clicked  On_boarding items
                val bundle = bundleOf(
                    INSTRUCTIONID to viewModel.clickedId.get(),
                    ISFROMHOME to isFromHomeScreen,
                    ISDNDCHECKED to viewModel.dontShowThisScreen.get()
                )
                if (viewModel.clickedId.get() != ONBOARDING.HOWTO.id) {// clicked onBoarding item that except How to

                    navigateInstructionOrCaliberation(bundle)

                } else {  //Clicked on How to

                    if (findNavController().currentDestination?.id == R.id.destination_onboarding) {
                        findNavController().navigate(
                            R.id.action_onboardingFragment_to_howtofragment_unchecked,
                            bundle
                        )

                        /*if (viewModel.dontShowThisScreen.get()) {
                            findNavController().navigate(
                                R.id.action_onboardingFragment_to_howtofragment_checked,
                                bundle
                            )
                        } else {   //Navigate to Caliberation screen
                            findNavController().navigate(
                                R.id.action_onboardingFragment_to_howtofragment_unchecked,
                                bundle
                            )
                        }*/
                    }

                }
                Unit

            }
            is OnboardingViewModel.Event.OnHideProgress -> bottomNavViewModel.showProgress.set(false)
            is OnboardingViewModel.Event.NoNetworkError -> {
                showSnackBar()
            }
            is OnboardingViewModel.Event.DatFetchError -> showSnackBar()
            is OnboardingViewModel.Event.OnShowProgress -> bottomNavViewModel.showProgress.set(true)
        }

    private fun showSnackBar() {
        val errorMessage = viewModel.errorString.get() ?: ""
        Utility.showSnackBar(
            errorMessage,
            binding.container
        )
    }

    private fun navigateInstructionOrCaliberation(bundle: Bundle) {
        navigateWithoutDontShows(bundle)

        /*if (viewModel.dontShowThisScreen.get()) {   //Clicked on Items which satisfy  Don't show this screen condition
            navigateWithDontShows(bundle)


        } else { //Clicked on Items which doesn't satisfy  Don't show this screen condition
            navigateWithoutDontShows(bundle)

        }*/
    }

    private fun navigateHomeScreen() {
        if (viewModel.dontShowThisScreen.get()) {//Navigate tp Home Screen with extra condition Don't shows this screen
            findNavController().navigate(R.id.action_onboardingFragment_to_homefragment_checkedbox_clicked)
        } else {//Navigate to Home screen skip & Continue  without any Condtion satisfied
            findNavController().navigate(R.id.action_onboardingFragment_to_homefragment)
        }
    }

    private fun navigateWithoutDontShows(bundle: Bundle) {

        try {
            if (viewModel.clickedId.get() == ONBOARDING.BEAMSETUP.id) {
                if (findNavController().currentDestination?.id == R.id.destination_onboarding) {
                    findNavController().navigate(
                        R.id.action_onboardingFragment_to_instructionsfragment,
                        bundle
                    )
                }
            } else { //Navigate to Caliberation screen
                if (findNavController().currentDestination?.id == R.id.destination_onboarding) {
                    findNavController().navigate(
                        R.id.action_onboardingFragment_to_calibrationfragment,
                        bundle
                    )
                }
            }

        } catch (e: IllegalArgumentException) {
            logger.d(e.message.toString())
        }

    }

    private fun navigateWithDontShows(bundle: Bundle) {
        if (viewModel.clickedId.get() == ONBOARDING.BEAMSETUP.id) {
            if (findNavController().currentDestination?.id == R.id.destination_onboarding) {
                //findNavController().popBackStack(R.id.destination_onboarding, false)
                findNavController().navigate(
                    R.id.action_onboardingFragment_to_instructionsfragment_checkedbox_clicked,
                    bundle
                )
            }
        } else {  //Navigate to Caliberation screen
            if (findNavController().currentDestination?.id == R.id.destination_onboarding) {
                findNavController().navigate(
                    R.id.action_onboardingFragment_to_calibrationfragment_checkedbox_clicked,
                    bundle
                )
            }
        }
    }

    private fun showBluetoothDialogue() {  //Displaying Dialog for Bluetooth
        if (!viewModel.isBleLaterClicked.get() && !isFromHomeScreen) {
            Utility.getCommonAlertDialogue(
                requireContext(),
                resources.getString(R.string.connectivity),
                resources.getString(R.string.ble_connectivity_onboarding),
                resources.getString(R.string.later),
                resources.getString(R.string.turnon),
                this,
                Utility.AlertType.BLE,
                Utility.Iconype.NONE
            )
        }
    }

    private fun showWifiDialogue() {  //Displaying Dialog for Wifi
        //for retrict to open again
        if (!viewModel.isWifiLaterClicked.get()) {
            isWifiAlert = true
            Utility.getCommonAlertDialogue(
                requireContext(),
                resources.getString(R.string.connectivity),
                resources.getString(R.string.wifi_connectivity_onboarding),
                resources.getString(R.string.later),
                resources.getString(R.string.settings),
                this,
                Utility.AlertType.WIFI,
                Utility.Iconype.NONE
            )
        }

    }


    private fun setToolbar() {
//        if (isFromHomeScreen) {
//            toolbarViewModel.isShowTransparentActionBar.set(true)
//            toolbarViewModel.isShowActionBar.set(false)
//            bottomNavViewModel.visibility.set(true)
//        } else {
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(false)
        bottomNavViewModel.visibility.set(false)
        if (viewModel.isFromHome_Observable.get()) {
            (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
//        }
    }

    private fun setHeadingTitle() {
        if (bottomNavViewModel.isGuestBase.get()) {
            viewModel.onBoardingTitle.set("")
            viewModel.onBoardingUserName.set(getString(R.string.Welcomeheader))
            viewModel.onBoardingSubTitle.set(getString(R.string.tutorial_sub_header_for_guest))
        } else {
            viewModel.onBoardingTitle.set("")
            viewModel.onBoardingSubTitle.set(getString(R.string.tutorial_sub_header_for_guest))
            viewModel.onBoardingUserName.set(
                getString(
                    R.string.hi_text,
                    bottomNavViewModel.userFirstNameBase.get()
                )
            )

        }

    }

    override fun onCustomPositiveButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        if (isWifiAlert) {
            startActivity(Intent(Settings.ACTION_SETTINGS))
        } else {
            val mBluetoothAdapter =
                BluetoothAdapter.getDefaultAdapter()
            mBluetoothAdapter.enable()
            if (!Utility.getWifistatus(requireContext())) {
                viewModel.isWifiLaterClicked.set(false)
                showWifiDialogue()
            }
        }    }

    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        if (!isWifiAlert) {
            logger.d("Later clicked")
            viewModel.isBleLaterClicked.set(true)
            viewModel.onClickLater()
        } else {
            viewModel.isWifiLaterClicked.set(true)
            viewModel.onClickLater()
        }    }


}
