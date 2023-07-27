package com.ditto.menuitems_ui.updateprojector

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ditto.connectivity.ConnectivityActivity
import com.ditto.connectivity.ConnectivityUtils
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.menuitems_ui.R
import com.ditto.menuitems_ui.databinding.FragmentUpdateProjectorBinding
import com.ditto.menuitems_ui.updateprojector.adapter.UpdateProjectorAdapter
import com.ditto.menuitems_ui.updateprojector.fragment.UpdateProjectorViewModel
import core.MODE_SERVICE
import core.SCREEN_MANAGE_DEVICE
import core.SEARCH_COMPLETE
import core.SEARCH_COMPLETE_AFTER_WIFI
import core.models.Nsdservicedata
import core.network.NetworkUtility
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

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AboutAppFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

//Added by vineetha for update projector popup
class UpdateProjectorFragment : BaseFragment(),Utility.CustomCallbackDialogListener,Utility.CallbackDialogListener  {
    @Inject
    lateinit var loggerFactory: LoggerFactory
    var receivedServiceList: ArrayList<Nsdservicedata>? = null
    val logger: Logger by lazy {
        loggerFactory.create(UpdateProjectorFragment::class.java.simpleName)
    }
    private val viewModel: UpdateProjectorViewModel by ViewModelDelegate()
    lateinit var binding: FragmentUpdateProjectorBinding

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentUpdateProjectorBinding.inflate(inflater).also {
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

        showAlertDialogue(
            requireContext(),
            getString(R.string.updateprojector_msg),
            getString(R.string.cancel),
            getString(R.string.yes),
            this,
            Utility.AlertType.UPDATE_PROJECTOR
        )
        getAndroidVersion()
    }

    fun getAndroidVersion(): String? {
        var versionName = ""
        try {
            versionName = Build.VERSION.RELEASE.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return versionName
        Log.d("ffff", "vvvv" + versionName)

    }
    @SuppressLint("ResourceType")
    fun showAlertDialogue(
        context: Context?,
        alertmessage: String,
        negativeButton: String,
        positiveButton: String,
        callbackDialogListener: Utility.CallbackDialogListener,
        alertType: Utility.AlertType,
    ) {
        if (context != null) {
            val mDialogView =
                LayoutInflater.from(context).inflate(core.lib.R.layout.alert_updateprojector, null)
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setView(mDialogView)
            val alert = dialogBuilder.create()
            alert.setCancelable(false)
            alert.show()
            alert.window?.setBackgroundDrawable(null)
            val message = mDialogView.findViewById(core.lib.R.id.alert_message) as TextView
            message.text = alertmessage
            val negative = mDialogView.findViewById(core.lib.R.id.neg_text) as TextView
            negative.text = negativeButton
            val positive = mDialogView.findViewById(core.lib.R.id.pos_txt) as TextView
            positive.text = positiveButton
            negative.setOnClickListener {
                alert.dismiss()
                callbackDialogListener.onNegativeButtonClicked(alertType)
            }
            positive.setOnClickListener {
                alert.dismiss()
                checkBluetoothWifiPermission()
                callbackDialogListener.onPositiveButtonClicked(alertType)
            }
        }
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
    private fun showConnectivityPopup() {
        viewModel.numberOfProjectors.set("")
        viewModel.isServiceNotFound.set(false)
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.isFromBackground = false
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
        } else if(requestCode == REQUEST_ENABLE_BT){
            val mBluetoothAdapter =
                BluetoothAdapter.getDefaultAdapter()
            if (mBluetoothAdapter?.isEnabled == false) {
                activity?.onBackPressed()
            }else{
                if (!Utility.getWifistatus(requireContext())) {
                    showWifiDialogue()
                } else {
                    showConnectivityPopup()
                }
            }
        }
    }

    private fun bindAdapter() {
        if (receivedServiceList?.size!! > 0) {
            setAdapter()
            filterServiceList()
        } else {
            viewModel.numberOfProjectors.set(
                getString(
                    R.string.str_projectorsfound,
                    (receivedServiceList?.size ?: 0).toString()
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
                    (receivedServiceList?.size ?: 0).toString()
                )
            )
            viewModel.isServiceNotFound.set(true)
            viewModel.isShowServiceList.set(false)
        }
    }
    /**
    }
     * [Function] Filtering list to change to connection status of alredy connected wifi if any
     */
    private fun filterServiceList() {
        if (NetworkUtility.nsdSericeHostName != null) {
            for (item in receivedServiceList!!.indices) {
                if (NetworkUtility.nsdSericeHostName == receivedServiceList!![item].nsdSericeHostAddress &&
                    NetworkUtility.nsdSericePortName == receivedServiceList!![item].nsdServicePort
                ) {
                    viewModel.clickedPosition.set(item)
                    resetAdapter(true)
                    break
                }
            }
        }
        viewModel.numberOfProjectors.set(
            getString(
                R.string.str_projectorsfound,
                (receivedServiceList?.size ?: 0).toString()
            )
        )
        viewModel.isServiceNotFound.set(false)
        viewModel.isShowServiceList.set(true)
    }
    /**
     * [Function] Filtering after passing wifi credentials and autoconnect to selected projector
     */
    private fun filterServiceListAfterWifi() {
        if (ConnectivityUtils.nsdSericeNameAfterWifi != null) {
            var hostAddress : String = ""
            var port : Int = 0
            for (item in receivedServiceList!!.indices) {
                if (ConnectivityUtils.nsdSericeNameAfterWifi == receivedServiceList!![item].nsdServiceName) {
                    viewModel.clickedPosition.set(item)
                    hostAddress= receivedServiceList!![item].nsdSericeHostAddress
                    port = receivedServiceList!![item].nsdServicePort
                    break
                }
            }
            viewModel.connectToProjector(hostAddress, port, true)
        }
        viewModel.numberOfProjectors.set(
            getString(
                R.string.str_projectorsfound,
                (receivedServiceList?.size ?: 0).toString()
            )
        )
        viewModel.isServiceNotFound.set(false)
        viewModel.isShowServiceList.set(true)
    }


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
    private fun setAdapter() {
        receivedServiceList?.sortBy { list -> list.nsdServiceName}
        val adapter = UpdateProjectorAdapter(requireContext(), receivedServiceList!!, viewModel)
        binding.rvManageDevice.adapter = adapter
    }


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

    private fun handleEvent(event: UpdateProjectorViewModel.Event) =
        when(event){
            UpdateProjectorViewModel.Event.OnConnectionSuccess -> showSuccessPopup()
            UpdateProjectorViewModel.Event.OnConnectionFailed -> showFailedPopup()
            UpdateProjectorViewModel.Event.OnShowProgress -> bottomNavViewModel.showProgress.set(true)
            UpdateProjectorViewModel.Event.OnHideprogress -> bottomNavViewModel.showProgress.set(false)
            UpdateProjectorViewModel.Event.OnSocketDisconnect -> {
                viewModel.clearAppstate()
//                baseViewModel.isUserNeedCalibrated.set(false)
                baseViewModel.isCalibrated.set(false)
                resetAdapter(false)
            }
            UpdateProjectorViewModel.Event.OnConnectClick -> showConnectPopup()
            UpdateProjectorViewModel.Event.OnBleConnectClick -> {
                //showConnectivityPopup()
                checkBluetoothWifiPermission()
            }
            UpdateProjectorViewModel.Event.OnConnectedImageSent -> sendConnectImage()
            UpdateProjectorViewModel.Event.OnWaitingImageSent -> sendWaitingImage()


            else -> {}

    }
    /**
     * [Function] Filtering list to change to connection status of alredy connected wifi if any
     */
    private fun sendWaitingImage() {
        GlobalScope.launch {
            Utility.sendDittoImage(context, "setup_pattern_waiting")
        }
    }
    /**
     * [Function] Filtering list to change to connection status of alredy connected wifi if any
     */
    private fun sendConnectImage() {
        GlobalScope.launch {
            Utility.sendDittoImage(context, "setup_pattern_connected")
        }
    }

    /**
     * [Function] After successfull connection
     */
    private fun showSuccessPopup() {
        if (isAdded() && activity != null) {
            GlobalScope.launch {
                delay(200)
                Utility.sendDittoImage(requireContext(), "setup_pattern_connected")
            }
            baseViewModel.activeSocketConnection.set(true)
            viewModel.resetAppstate()
            resetAdapter(true)
        }
    }

    /**
     * [Function] After Connection Failed
     */
    private fun showFailedPopup() {
        if (isAdded() && activity != null) {
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
            baseViewModel.isCalibrated.set(false)
            viewModel.connectToProjector(
                receivedServiceList!![viewModel.clickedPosition.get()].nsdSericeHostAddress,
                receivedServiceList!![viewModel.clickedPosition.get()].nsdServicePort,
                true
            )
        }
    }


    private fun setuptoolbar(){
        bottomNavViewModel.visibility.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(true)
        toolbarViewModel.isShowActionMenu.set(false)
        (activity as BottomNavigationActivity).setToolbarIcon()
        (activity as BottomNavigationActivity).setToolbarTitle(getString(R.string.str_menu_updateprojector))


    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults:
        IntArray,
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                if (!Utility.getBluetoothstatus()) {
                    showBluetoothDialogue()
                } else if (!Utility.getWifistatus(requireContext())) {
                    showWifiDialogue()
                } else {
                    showConnectivityPopup()
                }
            } else {
                logger.d("Permission Denied by the user")
                //Bluetooth permission dialog is shown only in api 31 and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Utility.getAlertDialogue(
                        requireContext(),
                        getString(R.string.permissions_required),
                        getString(R.string.bluetooth_pemissions_denied),
                        getString(R.string.cancel),
                        getString(R.string.go_to_settings),
                        this,
                        Utility.AlertType.PERMISSION_DENIED
                    )
                } else {
                    Utility.getAlertDialogue(
                        requireContext(),
                        getString(R.string.permissions_required),
                        getString(R.string.permissions_denied),
                        getString(R.string.cancel),
                        getString(R.string.go_to_settings),
                        this,
                        Utility.AlertType.PERMISSION_DENIED
                    )
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AboutAppFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UpdateProjectorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

            private const val REQUEST_CODE_PERMISSIONS = 111
            private const val REQUEST_ACTIVITY_RESULT_CODE = 121
            private const val REQUEST_ENABLE_BT = 151
            private val REQUIRED_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            } else {
                arrayOf(Manifest.permission.BLUETOOTH)
            }
        }


    override fun onCustomPositiveButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType,
    ) {
        when (alertType) {
            Utility.AlertType.SOC_CONNECT -> {
                //viewModel.disConnectToProjector(NetworkUtility.nsdSericeHostName,NetworkUtility.nsdSericePortName,false)
                viewModel.isCalibrated.set(false)
                baseViewModel.isCalibrated.set(false)
                //
                viewModel.sendWaitingImage()
                viewModel.connectToProjector(
                    receivedServiceList!![viewModel.clickedPosition.get()].nsdSericeHostAddress,
                    receivedServiceList!![viewModel.clickedPosition.get()].nsdServicePort,
                    true
                )
            }
            Utility.AlertType.BLE -> {
                val mBluetoothAdapter =
                    BluetoothAdapter.getDefaultAdapter()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && mBluetoothAdapter?.isEnabled == false) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                } else {
                    mBluetoothAdapter.enable()
                    if (!Utility.getWifistatus(requireContext())) {
                        showWifiDialogue()
                    } else {
                        showConnectivityPopup()
                    }
                }
            }
            Utility.AlertType.WIFI -> {
                startActivity(Intent(Settings.ACTION_SETTINGS))
                activity?.onBackPressed()
            }
            else -> {}
        }
    }

    /**
     * [Function] Negative buttons click listner
     */
    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType,
    ) {

        when (alertType) {

            Utility.AlertType.BLE -> {

                activity?.onBackPressed()
            }
            Utility.AlertType.WIFI -> {
                activity?.onBackPressed()

            }
            else -> {}
        }
    }

    override fun onPositiveButtonClicked(alertType: Utility.AlertType) {
        if (alertType.equals(Utility.AlertType.PERMISSION_DENIED)) {
            Utility.navigateToAppSettings(requireContext())
        }
    }

    override fun onNegativeButtonClicked(alertType: Utility.AlertType) {}

    override fun onNeutralButtonClicked(alertType: Utility.AlertType) {}
}