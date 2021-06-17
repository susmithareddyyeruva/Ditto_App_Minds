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
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.menuitems_ui.R
import com.ditto.menuitems_ui.databinding.FragmentManagedevicesBinding
import com.ditto.menuitems_ui.managedevices.adapter.ManageDeviceAdapter
import core.MODE_SERVICE
import core.SEARCH_COMPLETE
import core.appstate.AppState
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
    var receivedServiceList : ArrayList<Nsdservicedata>? = null
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
        showConnectivityPopup()
    }
    private fun setUIEvents() {
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }
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
    private fun bindAdapter(){
        if (receivedServiceList?.size!! > 0){
            val adapter = ManageDeviceAdapter(requireContext(), receivedServiceList!!,viewModel)
            binding.rvManageDevice.adapter = adapter
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
    private fun filterServiceList(){
        if (core.network.Utility.nsdSericeHostName != null) {
            for (item in receivedServiceList!!.indices){
                if (core.network.Utility.nsdSericeHostName == receivedServiceList!![item].nsdSericeHostAddress){
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
    private fun showConnectivityPopup() {
        GlobalScope.launch {
            delay(100)
            val intent = Intent(requireContext(), ConnectivityActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("ScreenName","MD")
            intent.putExtra("ScreenMode",viewModel.mode.get())
            startActivityForResult(
                intent,
                REQUEST_ACTIVITY_RESULT_CODE
            )
        }
    }
    private fun setuptoolbar() {
        bottomNavViewModel.visibility.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(true)
        toolbarViewModel.isShowActionMenu.set(false)
        (activity as BottomNavigationActivity).hidemenu()
        (activity as BottomNavigationActivity).setToolbarTitle(getString(R.string.manage_devices))
        (activity as BottomNavigationActivity).setToolbarIcon()

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ACTIVITY_RESULT_CODE) {
            when {
                data?.data.toString().equals(SEARCH_COMPLETE) -> {
                    receivedServiceList = Utility.searchServieList
                    bindAdapter()
                }

            }
        }
    }
    companion object {
        private const val REQUEST_ACTIVITY_RESULT_CODE = 121
        private const val REQUEST_CODE_PERMISSIONS = 111
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.BLUETOOTH)
    }

    private fun showSuccessPopup(){
        baseViewModel.activeSocketConnection.set(true)
        resetAppstate()
        resetAdapter(true)
    }

    private fun showFailedPopup(){
        baseViewModel.activeSocketConnection.set(false)
        Utility.getCommonAlertDialogue(
            requireContext(),
            "",
            "Connection failed!",
            "",
            "OK",
            this,
            Utility.AlertType.CONNECTIVITY,
            Utility.Iconype.FAILED
        )
    }

    private fun showConnectPopup(){

        var toShowPopup : Boolean = false
        for (item in receivedServiceList!!){
            if (item.isConnected){
                toShowPopup = true
                break
            }
        }
        if (toShowPopup){
            Utility.getCommonAlertDialogue(
                requireContext(),
                "",
                "Do you want to switch the projector?",
                "No",
                "Yes",
                this,
                Utility.AlertType.SOC_CONNECT,
                Utility.Iconype.FAILED
            )
        } else {
            viewModel.connectToProjector(receivedServiceList!![viewModel.clickedPosition.get()]?.nsdSericeHostAddress,
                receivedServiceList!![viewModel.clickedPosition.get()]?.nsdServicePort,
                true)
        }
    }

    override fun onCustomPositiveButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {

        when(alertType){
            Utility.AlertType.SOC_CONNECT ->
                viewModel.connectToProjector(receivedServiceList!![viewModel.clickedPosition.get()]?.nsdSericeHostAddress,
                    receivedServiceList!![viewModel.clickedPosition.get()]?.nsdServicePort,
                true)
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

            }
        }
    }

    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {

    }

    private fun resetAppstate(){
        core.network.Utility.isServiceConnected = true
        core.network.Utility.nsdSericeHostName =  Utility.searchServieList?.get(viewModel.clickedPosition.get())?.nsdSericeHostAddress!!
        core.network.Utility.nsdSericePortName = Utility.searchServieList?.get(viewModel.clickedPosition.get())?.nsdServicePort!!
        AppState.clearSavedService()
        Utility.searchServieList?.get(viewModel.clickedPosition.get())?.let {
            AppState.saveCurrentService(
                it
            )
        }
    }

    private fun resetAdapter(isConnected : Boolean){
        bottomNavViewModel.showProgress.set(false)
        receivedServiceList?.get(viewModel.clickedPosition.get())?.isConnected = isConnected
        resetlist(isConnected)
        binding.rvManageDevice.adapter?.notifyDataSetChanged()
    }

    private fun resetlist(isConnected: Boolean){
        if (isConnected){
            for (item in receivedServiceList!!.indices){
                if (item != viewModel.clickedPosition.get())
                    receivedServiceList!![item].isConnected = false
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
    private fun showBluetoothDialogue() {

        Utility.getCommonAlertDialogue(
            requireContext(),
            "Connectivity",
            "This app needs Bluetooth connectivity",
            "LATER",
            resources.getString(R.string.turnon),
            this,
            Utility.AlertType.BLE,
            Utility.Iconype.SUCCESS
        )
    }

    private fun showWifiDialogue() {

        Utility.getCommonAlertDialogue(
            requireContext(),
            "Connectivity",
            "This app needs WiFi connectivity",
            "LATER",
            "SETTINGS",
            this,
            Utility.AlertType.WIFI,
            Utility.Iconype.SUCCESS
        )

    }
}