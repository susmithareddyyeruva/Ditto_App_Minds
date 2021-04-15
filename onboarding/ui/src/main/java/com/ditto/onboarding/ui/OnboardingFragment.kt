package com.ditto.onboarding.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.onboarding.ui.adapter.OnboardingAdapter
import com.ditto.onboarding.ui.databinding.OnboardingFragmentBinding
import com.ditto.onboarding.util.ONBOARDING
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.onboarding_fragment.*
import javax.inject.Inject

class OnboardingFragment : BaseFragment(), Utility.CallbackDialogListener {

    @Inject
    lateinit var loggerFactory: LoggerFactory
    var isFromHomeScreen: Boolean = false
    var isFromOnBoardingScreen: Boolean = true
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
        arguments?.getInt("UserId")?.let { viewModel.userId = (it) }
        arguments?.getBoolean("isFromHome")?.let { isFromHomeScreen = (it) }
        viewModel.isFromHome_Observable.set(isFromHomeScreen)
        setOnBoardingAdapter()
        setUIEvents()
        setToolbar()
        checkBluetoothWifiPermission()
    }
    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 111
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.BLUETOOTH)
    }

    private fun checkBluetoothWifiPermission() {

        if (allPermissionsGranted()) {

            if (!Utility.getBluetoothstatus()) {
                isWifiAlert = false
                showBluetoothDialogue()
            } else if(!Utility.getWifistatus(requireContext())) {
                isWifiAlert = true
                showWifiDialogue()
                Log.d("wifiefi222",viewModel.isWifiLaterClicked.get().toString())
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
                Toast.makeText(requireContext(),"App will not work properly without this permission. Please turn on the permission from settings",
                    Toast.LENGTH_LONG).show()
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
                    if (viewModel.dontShowThisScreen.get()) {
                        findNavController().navigate(R.id.action_onboardingFragment_to_homefragment_checkedbox_clicked)//Navigate tp Home Screen with extra condition Don't shows this screen
                    } else {
                        findNavController().navigate(R.id.action_onboardingFragment_to_homefragment)//Navigate to Home screen skip & Continue  without any Condtion satisfied
                    }
                }
                Unit
            }

            is OnboardingViewModel.Event.OnShowBleDialogue -> {
                Log.d("dialog","Show bluetooth dialog")
            }
            is OnboardingViewModel.Event.OnItemClick -> {  //Clicked  On_boarding items
                isFromOnBoardingScreen = !isFromHomeScreen
                val bundle = bundleOf(
                    "InstructionId" to viewModel.clickedId.get(),
                    "isFromOnBoarding" to isFromOnBoardingScreen,
                    "isFromHome" to isFromHomeScreen
                )
                if (viewModel.clickedId.get() != ONBOARDING.HOWTO.id) {// clicked onBoarding item that except How to

                    if (viewModel.dontShowThisScreen.get()) {   //Clicked on Items which satisfy  Don't show this screen condition
                        if(viewModel.clickedId.get() == ONBOARDING.BEAMSETUP.id) {
                            if (findNavController().currentDestination?.id == R.id.destination_onboarding) {
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


                    } else {
                        //Clicked on Items which doesn't satisfy  Don't show this screen condition

                        try {
                            if(viewModel.clickedId.get() == ONBOARDING.BEAMSETUP.id) {
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

                }
                else{  //Clicked on How to

                    if (findNavController().currentDestination?.id == R.id.destination_onboarding) {
                        if (viewModel.dontShowThisScreen.get()) {
                            findNavController().navigate(R.id.action_onboardingFragment_to_howtofragment_checked,bundle)
                        }else{   //Navigate to Caliberation screen
                            findNavController().navigate(R.id.action_onboardingFragment_to_howtofragment_unchecked,bundle)
                        }
                    }

                }
                Unit

            }

        }

    private fun showBluetoothDialogue() {  //Displaying Dialog for Bluetooth
        if (!viewModel.isBleLaterClicked.get() && !isFromHomeScreen) {
            Utility.getAlertDialogue(
                requireContext(),
                resources.getString(R.string.connectivity),
                resources.getString(R.string.ble_connectivity_onboarding),
                resources.getString(R.string.later),
                resources.getString(R.string.turnon),
                this,
                Utility.AlertType.BLE
            )
        }
    }

    private fun showWifiDialogue() {  //Displaying Dialog for Wifi
        //for retrict to open again
        if(!viewModel.isWifiLaterClicked.get()){
            isWifiAlert=true
            Utility.getAlertDialogue(
                requireContext(),
                resources.getString(R.string.connectivity),
                resources.getString(R.string.wifi_connectivity_onboarding),
                resources.getString(R.string.later),
                resources.getString(R.string.settings),
                this,
                Utility.AlertType.WIFI
            )
        }

    }

    override fun onPositiveButtonClicked(alertType: Utility.AlertType) {// Alert Dialog Turn on button clicked
        if(isWifiAlert){
            startActivity(Intent(Settings.ACTION_SETTINGS))
        } else {
            val mBluetoothAdapter =
                BluetoothAdapter.getDefaultAdapter()
            mBluetoothAdapter.enable()
            if(!Utility.getWifistatus(requireContext())) {
                viewModel.isWifiLaterClicked.set(false)
                showWifiDialogue()
            }
        }

    }

    override fun onNegativeButtonClicked(alertType: Utility.AlertType) {// Alert Dialog Later button clicked
        if (!isWifiAlert){
            logger.d("Later clicked")
            viewModel.isBleLaterClicked.set(true)
            viewModel.onClickLater()
        } else {
            viewModel.isWifiLaterClicked.set(true)
            viewModel.onClickLater()
        }
    }

    override fun onNeutralButtonClicked() {
        TODO("Not yet implemented")
    }

    private fun setToolbar() {
        if (isFromHomeScreen) {
            viewModel.onBoardingTitle.set(getString(R.string.tutorialheader))
            toolbarViewModel.isShowTransparentActionBar.set(true)
            toolbarViewModel.isShowActionBar.set(false)
            bottomNavViewModel.visibility.set(true)
        } else {
            viewModel.onBoardingTitle.set(getString(R.string.Welcomeheader))
            toolbarViewModel.isShowTransparentActionBar.set(false)
            toolbarViewModel.isShowActionBar.set(false)
            bottomNavViewModel.visibility.set(false)
        }
    }
}
