package com.ditto.menuitems_ui.managedevices.fragment

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.ditto.connectivity.ConnectivityActivity
import com.ditto.connectivity.ConnectivityUtils
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.menuitems_ui.R
import com.ditto.menuitems_ui.databinding.FragmentManagedevicesBinding
import com.ditto.menuitems_ui.managedevices.adapter.ManageDeviceAdapter
import core.MODE_SERVICE
import core.SCREEN_MANAGE_DEVICE
import core.SEARCH_COMPLETE
import core.SEARCH_COMPLETE_AFTER_WIFI
import core.models.Nsdservicedata
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class ManageDeviceFragment : BaseFragment(), Utility.CustomCallbackDialogListener {

    @Inject
    lateinit var loggerFactory: LoggerFactory
    private val viewModel: ManageDeviceViewModel by ViewModelDelegate()
    lateinit var binding: FragmentManagedevicesBinding
    var receivedServiceList: ArrayList<Nsdservicedata>? = null
    val logger: Logger by lazy {
        loggerFactory.create(ManageDeviceFragment::class.java.simpleName)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManagedevicesBinding.inflate(inflater).also {
            it.viewmodel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setuptoolbar()
        setUIEvents()
        viewModel.mode.set(MODE_SERVICE)
        checkBluetoothWifiPermission()
    }

    private fun setAdapter() {
        val adapter = ManageDeviceAdapter(requireContext(), receivedServiceList!!, viewModel)
        binding.rvManageDevice.adapter = adapter
    }

    /**
     * [Function] Setting UI events which triggers viewmodel events
     */
    private fun setUIEvents() {
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    /**
     * [Function] Handles events of viewmodel
     */
    private fun handleEvent(event: ManageDeviceViewModel.Event) =
        when (event) {
            ManageDeviceViewModel.Event.OnConnectionSuccess -> showSuccessPopup()
            ManageDeviceViewModel.Event.OnConnectionFailed -> showFailedPopup()
            ManageDeviceViewModel.Event.OnShowProgress -> bottomNavViewModel.showProgress.set(true)
            ManageDeviceViewModel.Event.OnHideprogress -> bottomNavViewModel.showProgress.set(false)
            ManageDeviceViewModel.Event.OnSocketDisconnect -> resetAdapter(false)
            ManageDeviceViewModel.Event.OnConnectClick -> showConnectPopup()
            ManageDeviceViewModel.Event.OnBleConnectClick -> showConnectivityPopup()
        }

    /**
     * [Function] Setting the adapter to listview by checking the list size
     */
    private fun bindAdapter() {
        if (receivedServiceList?.size!! > 0) {
            setAdapter()
            filterServiceList()
        } else {
            viewModel.numberOfProjectors.set(
                getString(
                    R.string.str_projectorsfound,
                    receivedServiceList!!.size.toString()
                )
            )
            viewModel.isServiceNotFound.set(true)
            viewModel.isShowServiceList.set(false)
        }
    }

    /**
     * [Function] Binding adapter after passing wifi credentials
     */
    private fun bindAdapterAfterWifi() {
        if (receivedServiceList?.size!! > 0) {
            setAdapter()
            filterServiceListAfterWifi()
        } else {
            viewModel.numberOfProjectors.set(
                getString(
                    R.string.str_projectorsfound,
                    receivedServiceList!!.size.toString()
                )
            )
            viewModel.isServiceNotFound.set(true)
            viewModel.isShowServiceList.set(false)
        }
    }

    /**
     * [Function] Filtering after passing wifi credentials and autoconnect to selected projector
     */
    private fun filterServiceListAfterWifi() {
        if (ConnectivityUtils.nsdSericeNameAfterWifi != null) {
            for (item in receivedServiceList!!.indices) {
                if (ConnectivityUtils.nsdSericeNameAfterWifi == receivedServiceList!![item].nsdServiceName) {
                    viewModel.clickedPosition.set(item)
                    viewModel.connectToProjector(
                        receivedServiceList!![item].nsdSericeHostAddress,
                        receivedServiceList!![item].nsdServicePort,
                        true
                    )
                    break
                }
            }

        }
        viewModel.numberOfProjectors.set(
            getString(
                R.string.str_projectorsfound,
                receivedServiceList!!.size.toString()
            )
        )
        viewModel.isServiceNotFound.set(false)
        viewModel.isShowServiceList.set(true)
    }

    /**
     * [Function] Filtering list to change to connection status of alredy connected wifi if any
     */
    private fun filterServiceList() {
        if (core.network.Utility.nsdSericeHostName != null) {
            for (item in receivedServiceList!!.indices) {
                if (core.network.Utility.nsdSericeHostName == receivedServiceList!![item].nsdSericeHostAddress) {
                    viewModel.clickedPosition.set(item)
                    resetAdapter(true)
                    break
                }
            }
        }
        viewModel.numberOfProjectors.set(
            getString(
                R.string.str_projectorsfound,
                receivedServiceList!!.size.toString()
            )
        )
        viewModel.isServiceNotFound.set(false)
        viewModel.isShowServiceList.set(true)
    }

    /**
     * [Function] Starting connectivity module
     */
    private fun showConnectivityPopup() {
        GlobalScope.launch {
            delay(100)
            val intent = Intent(requireContext(), ConnectivityActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("ScreenName", SCREEN_MANAGE_DEVICE)
            intent.putExtra("ScreenMode", viewModel.mode.get())
            startActivityForResult(
                intent,
                REQUEST_ACTIVITY_RESULT_CODE
            )
        }
    }

    /**
     * [Function] onActivityResult from connectivity module
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        receivedServiceList = Utility.searchServieList
        if (requestCode == REQUEST_ACTIVITY_RESULT_CODE) {
            when {
                data?.data.toString().equals(SEARCH_COMPLETE) -> {
                    bindAdapter()
                }
                data?.data.toString().equals(SEARCH_COMPLETE_AFTER_WIFI) -> {
                    bindAdapterAfterWifi()
                }
            }
        }
    }


    /**
     * [Function] Setting up the toolbar
     */
    private fun setuptoolbar() {
        bottomNavViewModel.visibility.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(true)
        toolbarViewModel.isShowActionMenu.set(false)
        (activity as BottomNavigationActivity).hidemenu()
        (activity as BottomNavigationActivity).setToolbarTitle(getString(R.string.manage_devices))
        (activity as BottomNavigationActivity).setToolbarIcon()

    }
    companion object {
        private const val REQUEST_ACTIVITY_RESULT_CODE = 121
        private const val REQUEST_CODE_PERMISSIONS = 111
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.BLUETOOTH)
    }

    /**
     * [Function] After successfull connection
     */
    private fun showSuccessPopup() {
        baseViewModel.activeSocketConnection.set(true)
        viewModel.resetAppstate()
        resetAdapter(true)
    }

    /**
     * [Function] After Connection Failed
     */
    private fun showFailedPopup() {
        baseViewModel.activeSocketConnection.set(false)
        Utility.getCommonAlertDialogue(
            requireContext(),
            "",
            getString(R.string.str_connection_failed),
            "",
            getString(R.string.str_ok),
            this,
            Utility.AlertType.CONNECTIVITY,
            Utility.Iconype.FAILED
        )
    }

    /**
     * [Function] Connection switch popup
     */
    private fun showConnectPopup() {

        var toShowPopup: Boolean = false
        for (item in receivedServiceList!!) {
            if (item.isConnected) {
                toShowPopup = true
                break
            }
        }
        if (toShowPopup) {
            Utility.getCommonAlertDialogue(
                requireContext(),
                "",
                getString(R.string.str_switch_projector),
                getString(R.string.str_no),
                getString(R.string.str_yes),
                this,
                Utility.AlertType.SOC_CONNECT,
                Utility.Iconype.FAILED
            )
        } else {
            viewModel.connectToProjector(
                receivedServiceList!![viewModel.clickedPosition.get()].nsdSericeHostAddress,
                receivedServiceList!![viewModel.clickedPosition.get()].nsdServicePort,
                true
            )
        }
    }

    /**
     * [Function] Resetting adapter after changing the connection status
     */
    private fun resetAdapter(isConnected: Boolean) {
        bottomNavViewModel.showProgress.set(false)
        receivedServiceList?.get(viewModel.clickedPosition.get())?.isConnected = isConnected
        resetlist(isConnected)
        binding.rvManageDevice.adapter?.notifyDataSetChanged()
    }

    /**
     * [Function] Resetting the service list
     */
    private fun resetlist(isConnected: Boolean) {
        if (isConnected) {
            for (item in receivedServiceList!!.indices) {
                if (item != viewModel.clickedPosition.get())
                    receivedServiceList!![item].isConnected = false
            }
        }
    }

    /**
     * [Function] Checking permissions
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        context?.let { it1 ->
            ContextCompat.checkSelfPermission(
                it1, it
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    /**
     * [Function] Checking whether the bluetooth and Wifi is connected
     */
    private fun checkBluetoothWifiPermission() {
        if (allPermissionsGranted()) {
            if (!Utility.getBluetoothstatus()) {
                showBluetoothDialogue()
            } else if (!Utility.getWifistatus(requireContext())) {
                showWifiDialogue()
            } else {
                showConnectivityPopup()
            }
        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    /**
     * [Function] Popup to show Bluetooth Connection
     */
    private fun showBluetoothDialogue() {

        Utility.getCommonAlertDialogue(
            requireContext(),
            getString(R.string.str_connecitivity_title),
            getString(R.string.str_ble_message),
            getString(R.string.str_later),
            resources.getString(R.string.turnon),
            this,
            Utility.AlertType.BLE,
            Utility.Iconype.SUCCESS
        )
    }

    /**
     * [Function] Popup to show WIFI Connection
     */
    private fun showWifiDialogue() {

        Utility.getCommonAlertDialogue(
            requireContext(),
            getString(R.string.str_connecitivity_title),
            getString(R.string.str_wifi_message),
            getString(R.string.str_later),
            getString(R.string.str_settings),
            this,
            Utility.AlertType.WIFI,
            Utility.Iconype.SUCCESS
        )

    }

    /**
     * [Function] Positive buttons click listner
     */
    override fun onCustomPositiveButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {

        when (alertType) {
            Utility.AlertType.SOC_CONNECT ->
                viewModel.connectToProjector(
                    receivedServiceList!![viewModel.clickedPosition.get()].nsdSericeHostAddress,
                    receivedServiceList!![viewModel.clickedPosition.get()].nsdServicePort,
                    true
                )
            Utility.AlertType.BLE -> {
                val mBluetoothAdapter =
                    BluetoothAdapter.getDefaultAdapter()
                mBluetoothAdapter.enable()
                if (!Utility.getWifistatus(requireContext())) {
                    showWifiDialogue()
                } else {
                    showConnectivityPopup()
                }
            }
            Utility.AlertType.WIFI -> {
                startActivity(Intent(Settings.ACTION_SETTINGS))
                activity?.onBackPressed()
            }
        }
    }

    /**
     * [Function] Negative buttons click listner
     */
    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {

        when (alertType) {

            Utility.AlertType.BLE -> {

                activity?.onBackPressed()
            }
            Utility.AlertType.WIFI -> {
                activity?.onBackPressed()

            }
        }
    }

}