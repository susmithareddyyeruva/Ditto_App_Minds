package com.ditto.mylibrary.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ditto.connectivity.ConnectivityActivity
import com.ditto.connectivity.ConnectivityUtils
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.ui.databinding.PatternDescriptionFragmentBinding
import com.joann.fabrictracetransform.transform.TransformErrorCode
import com.joann.fabrictracetransform.transform.performTransform
import core.PDF_DOWNLOAD_URL
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.pattern_description_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.net.Socket
import java.util.*
import javax.inject.Inject

class PatternDescriptionFragment : BaseFragment(), Utility.CallbackDialogListener,Utility.CustomCallbackDialogListener {

    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(PatternDescriptionFragment::class.java.simpleName)
    }

    private val viewModel: PatternDescriptionViewModel by ViewModelDelegate()
    lateinit var binding: PatternDescriptionFragmentBinding
    private lateinit var alert: AlertDialog
    private lateinit var outputDirectory: File
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    private val CONNNECTION_FAILED = "Projector Connection failed. Try again!!" // Compliant


    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        binding = PatternDescriptionFragmentBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbarViewModel.isShowActionBar.set(false)
        bottomNavViewModel.visibility.set(false)
        (activity as BottomNavigationActivity).setToolbarTitle("Pattern details")
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbarPatterndesc)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_patterndesc.setNavigationIcon(R.drawable.ic_back_button)
        //baseViewModel.activeSocketConnection.set(false)

       /* if (viewModel.data.value == null) {
            arguments?.getString("clickedTailornovaID").toString()?.let { viewModel.clickedTailornovaID.set(it) }
            arguments?.getString("clickedOrderNumber").toString()?.let { viewModel.clickedOrderNumber.set(it) }
            bottomNavViewModel.showProgress.set(true)
            if(NetworkUtility.isNetworkAvailable(context)) {
                viewModel.fetchPattern()
            }else{
                viewModel.fetchOfflinePatterns()
            }
            setUIEvents()
        } else {
            setPatternImage()
        }*/
        outputDirectory = Utility.getOutputDirectory(requireContext())
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 111
        private const val REQUEST_ACTIVITY_RESULT_CODE = 121
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.BLUETOOTH)
    }

    private fun setUpUiBasedOnLoggedIn() {
        if (bottomNavViewModel.isGuestBase.get()) {
            setUpUiForGuestUser()
        } else {
            setUIForLoggedInUser()
        }
        viewModel.isDataReceived.set(true)
    }

    private fun setUpUiForGuestUser(){
        setData()
        setVisibilityForViews("WORKSPACE",false,false,false,false, false,false,true)
        setPatternImage()

    }

    private fun setVisibilityForViews(buttonText:String,showStatusLayout:Boolean,isSubscriptionExpired:Boolean
    ,showActiveText:Boolean,showPurchasedText:Boolean,showLine:Boolean,showResumeButton:Boolean,showWorkspaceOrRenewSubscriptionButton: Boolean){
        viewModel.resumeOrSubscription.set(buttonText)
        viewModel.isStatusLayoutVisible.set(showStatusLayout)
        viewModel.isSubscriptionExpired.set(isSubscriptionExpired)
        viewModel.showActive.set(showActiveText)
        viewModel.showPurchased.set(showPurchasedText)
        viewModel.showLine.set(showLine)
        viewModel.showResumButton.set(showResumeButton)
        viewModel.showWorkspaceOrRenewSubscriptionButton.set(showWorkspaceOrRenewSubscriptionButton)
        if(showPurchasedText && !showActiveText){
            binding.purchasedPattern.setPadding(0,0,0,0)
        }
    }

    private fun setUIForLoggedInUser() {
        setData()
        when(viewModel.clickedTailornovaID.get()?.toInt()){
             1-> setVisibilityForViews("RESUME",true,false,true,false, false,true,false)
            4-> setVisibilityForViews("WORKSPACE",true,false,false,true, false,false,true)
            8-> setVisibilityForViews("WORKSPACE",false,false,false,false, false,false,true)
            9-> setVisibilityForViews("RESUME",true,false,true,true, true,true,false)
            10-> setVisibilityForViews("RENEW SUBSCRIPTION",false,true,false,false, false,false,true)
            else->setVisibilityForViews("WORKSPACE",false,false,false,false, false,false,true)
        }
        setPatternImage()


    }

    private fun setData(){
        viewModel.patternName.set(viewModel.data.value?.name)
        viewModel.patternDescription.set(viewModel.data.value?.description)
        //viewModel.patternStatus.set(viewModel.data.value?.status)
        viewModel.patternStatus.set("FROM SFCC") // SET THE STATUS  which needs to be passed while clicking on particular pattern
    }


    private fun setUIEvents() {
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    private fun checkBluetoothWifiPermission() {
        if (allPermissionsGranted()) {
            if (!Utility.getBluetoothstatus()) {
                showBluetoothDialogue()
            } else if (!Utility.getWifistatus(requireContext())) {
                showWifiDialogue()
            } else {
                checkSocketConnection()
            }
        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }


    private fun checkSocketConnection() {
        GlobalScope.launch {
            if (core.network.NetworkUtility.nsdSericeHostName.isEmpty() && core.network.NetworkUtility.nsdSericePortName == 0) {
                showConnectivityPopup()
            } else {
                withContext(Dispatchers.Main) { showProgress(true) }
                if (startSocketConnection(
                        core.network.NetworkUtility.nsdSericeHostName,
                        core.network.NetworkUtility.nsdSericePortName
                    )
                ) {
                    //baseViewModel.activeSocketConnection.set(true)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Connected to Ditto Projector!!",
                            Toast.LENGTH_SHORT
                        ).show()
                        showProgress(false)
                        showCalibrationDialog()
                    }
                } else {
                    withContext(Dispatchers.Main) { showProgress(false) }
                    showConnectivityPopup()
                }
            }
        }
    }

    private suspend fun startSocketConnection(ipAddress: String, nsdPort: Int): Boolean {
        var isConnected: Boolean = false
        withContext(Dispatchers.IO) {
            var soc: Socket? = null
            try {
                soc = Socket(ipAddress, nsdPort)
                isConnected = soc.isConnected
            } catch (e: Exception) {
                isConnected = false
                Log.d(ConnectivityUtils.TAG, "Exception")
            } finally {
                soc?.close()
            }
        }
        return withContext(Dispatchers.Main) { isConnected }
    }

    private fun showBluetoothDialogue() {
        Utility.getCommonAlertDialogue(
            requireContext(),
            resources.getString(R.string.ditto_connect),
            resources.getString(R.string.ble_connectivity),
            resources.getString(R.string.skips),
            resources.getString(R.string.turnon),
            this,
            Utility.AlertType.BLE,
            Utility.Iconype.NONE
        )
    }

    private fun showWifiDialogue() {

        Utility.getCommonAlertDialogue(
            requireContext(),
            resources.getString(R.string.ditto_connect),
            resources.getString(R.string.wifi_connectivity),
            resources.getString(R.string.skips),
            resources.getString(R.string.settings),
            this,
            Utility.AlertType.WIFI,
            Utility.Iconype.NONE
        )

    }

    private fun showCalibrationDialog() {
        Utility.getAlertDialogue(
            requireContext(),
            resources.getString(R.string.setup_calibration_title),
            resources.getString(R.string.setup_calibration_message),
            resources.getString(R.string.setup_calibration_quickcheck),
            resources.getString(R.string.setup_calibration_calibrate),
            resources.getString(R.string.skips),
            this,
            Utility.AlertType.CALIBRATION,
        )

    }

    private fun showQuickCheckDialog() {
        Utility.getCommonAlertDialogue(
            requireContext(),
            resources.getString(R.string.setup_quickcheck_title),
            resources.getString(R.string.setup_quickcheck_message),
            resources.getString(R.string.calibrate),
            resources.getString(R.string.yes_string),
            this,
            Utility.AlertType.QUICK_CHECK,
            Utility.Iconype.NONE
        )
    }

    private fun sendCalibrationPattern() {
        logger.d("TRACE_ Projection : performTransform  Start " + Calendar. getInstance().timeInMillis)
        showProgress(true)
        val bitmap = Utility.getBitmapFromDrawable("calibration_pattern", requireContext())
        viewModel.disposable += Observable.fromCallable {
            performTransform(
                bitmap,
                context?.applicationContext,
                Utility.unityTransParmsString,
                false
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleResult(it, false) }
    }

    private fun sendQuickCheckImage() {
        showProgress(true)
        val bitmap = Utility.getBitmapFromDrawable("quick_check_pattern", requireContext())
        viewModel.disposable += Observable.fromCallable {
            performTransform(bitmap, context?.applicationContext, null, true)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleResult(it, true) }
    }

    private fun showProgress(toShow: Boolean) {
        bottomNavViewModel.showProgress.set(toShow)
        /*if (toShow) {
            val layout =
                activity?.layoutInflater?.inflate(R.layout.progress_dialog, null)

            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder
                .setCancelable(false)
            alert = dialogBuilder.create()
            alert.setView(layout)
            alert.show()
        } else {
            alert.dismiss()
        }*/
    }

    private fun handleResult(result: Pair<TransformErrorCode, Bitmap>, isQuickCheck: Boolean) {
        logger.d("TRACE_ Projection : performTransform  finish " + Calendar. getInstance().timeInMillis)
        logger.d("quick check Transform - ${result.second.width} * ${result.second.height}")
        alert?.dismiss()
        when (result.first) {
            TransformErrorCode.Success -> GlobalScope.launch {
                sendSampleImage(
                    Utility.addBlackBackgroundToBitmap(result.second),
                    isQuickCheck
                )
            }
            TransformErrorCode.InvalidImageFormat, TransformErrorCode.RetakeImage -> {
                // show alert?
            }
            TransformErrorCode.AdditionalImageNeeded -> {
                // show alert?
            }
            TransformErrorCode.FailToSetTransformParms, TransformErrorCode.MissingTransformParmsFile -> {
                // what to do?
            }
        }
    }

    private suspend fun sendSampleImage(result: Bitmap, isQuickCheck: Boolean) {
        //saveBitmap(result)
        logger.d("TRACE_ Projection : sendSampleImage  Start " + Calendar. getInstance().timeInMillis)
        withContext(Dispatchers.IO) {
            var soc: Socket? = null
            try {
                soc = Socket(
                    core.network.NetworkUtility.nsdSericeHostName,
                    core.network.NetworkUtility.nsdSericePortName
                )
                if (soc.isConnected) {
                    val workspaceStream = ByteArrayOutputStream()
                    result.compress(Bitmap.CompressFormat.PNG, 0, workspaceStream)
                    val bitmapdata = workspaceStream.toByteArray()
                    result.recycle()
                    val dataOutputStream: DataOutputStream =
                        DataOutputStream(soc.getOutputStream())
                    dataOutputStream.write(bitmapdata)
                    dataOutputStream.close()
                    withContext(Dispatchers.Main) {
                        showProgress(false)
                        if (isQuickCheck)
                            showQuickCheckDialog()
                        else
                            navigateToCalibration()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            CONNNECTION_FAILED,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                logger.d("Exception " + e.message)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        CONNNECTION_FAILED,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } finally {
                soc?.close()
                logger.d("TRACE_ Projection : sendSampleImage  Finish " + Calendar. getInstance().timeInMillis)
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


    private fun handleEvent(event: PatternDescriptionViewModel.Event) =
        when (event) {
            is PatternDescriptionViewModel.Event.OnWorkspaceButtonClicked -> {
                binding.textWatchvideo2.isEnabled = false
                if ((findNavController().currentDestination?.id == R.id.patternDescriptionFragment)
                    || (findNavController().currentDestination?.id == R.id.patternDescriptionFragmentFromHome)
                ) {
                    //checkBluetoothWifiPermission()
                    //forwardtoWorkspace()
                    checkSocketConnectionBeforeWorkspace()
                } else {
                    logger.d("OnClick Workspace failed")
                }
            }
            is PatternDescriptionViewModel.Event.OnDataUpdated -> {
                bottomNavViewModel.showProgress.set(false)
                setUpUiBasedOnLoggedIn()
            }

            is PatternDescriptionViewModel.Event.onSubscriptionClicked ->{
                logger.d("onSubscriptionClicked")
                Utility.redirectToExternalBrowser(requireContext(),"http://www.dittopatterns.com")

            }
            is PatternDescriptionViewModel.Event.OnInstructionsButtonClicked -> {
                if ((findNavController().currentDestination?.id == R.id.patternDescriptionFragment)
                    || (findNavController().currentDestination?.id == R.id.patternDescriptionFragmentFromHome)
                ) {
                    PDF_DOWNLOAD_URL = viewModel.data.value?.instructionUrl
                    val bundle =
                        bundleOf("PatternName" to viewModel.data.value?.name)
                    findNavController().navigate(
                        R.id.action_patternDescriptionFragment_to_pattern_instructions_Fragment,
                        bundle
                    )
                } else
                    Unit
            }
            PatternDescriptionViewModel.Event.OnDownloadComplete -> TODO()
            PatternDescriptionViewModel.Event.OnDataloadFailed -> showDataFailedAlert()
        }


    private fun showConnectivityPopup() {
        val intent = Intent(requireContext(), ConnectivityActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivityForResult(
            intent,
            REQUEST_ACTIVITY_RESULT_CODE
        )
    }



    private fun setPatternImage() {
        Glide.with(requireContext())
            .load(viewModel.data.value?.patternDescriptionImageUrl)
            .placeholder(R.drawable.ic_placeholder)
            .into(binding.imagePatternDesc)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        logger.d("On Activity Result")
        if (requestCode == REQUEST_ACTIVITY_RESULT_CODE) {
            if (data?.data.toString().equals("success")) {
                Toast.makeText(
                    requireContext(),
                    "Connected to Ditto Projector!!",
                    Toast.LENGTH_SHORT
                ).show()
                //baseViewModel.activeSocketConnection.set(true)
                showCalibrationDialog()
            } else if (data?.data.toString().equals("skip")) {
               enterWorkspace()
            }
        }
    }

    private fun checkSocketConnectionBeforeWorkspace() {
        GlobalScope.launch {
            if (core.network.NetworkUtility.nsdSericeHostName.isEmpty() && core.network.NetworkUtility.nsdSericePortName == 0) {
                withContext(Dispatchers.Main) {
                    baseViewModel.activeSocketConnection.set(false)
                    enterWorkspace()
                }
            } else {
                withContext(Dispatchers.Main) { showProgress(true) }
                if (startSocketConnection(
                        core.network.NetworkUtility.nsdSericeHostName,
                        core.network.NetworkUtility.nsdSericePortName
                    )
                ) {

                    withContext(Dispatchers.Main) {
                        showProgress(false)
                        baseViewModel.activeSocketConnection.set(true)
                        enterWorkspace()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showProgress(false)
                        baseViewModel.activeSocketConnection.set(false)
                        enterWorkspace()
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        binding.textWatchvideo2.isEnabled = true
        toolbarViewModel.isShowTransparentActionBar.set(true)
    }


    override fun onPause() {
        toolbarViewModel.isShowTransparentActionBar.set(false)
        super.onPause()
    }

    private fun enterWorkspace() {
        if (baseViewModel.activeSocketConnection.get()) {
            GlobalScope.launch { Utility.sendDittoImage(requireActivity(), "solid_black") }
        }
        //val bundle = bundleOf("PatternId" to viewModel.clickedID.get())
        val bundle = bundleOf("PatternId" to 1,"clickedOrderNumber" to 2) // todo shri see the pattern pieces
        if ((findNavController().currentDestination?.id == R.id.patternDescriptionFragment) || (findNavController().currentDestination?.id == R.id.patternDescriptionFragmentFromHome)) {
            findNavController().navigate(
                R.id.action_patternDescriptionFragment_to_WorkspaceFragment,
                bundle
            )
        }
    }

    override fun onPositiveButtonClicked(alertType: Utility.AlertType) {
        when (alertType) {
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
            Utility.AlertType.CALIBRATION -> {
                showProgress(toShow = true)
                GlobalScope.launch { projectBorderImage() }
            }
            Utility.AlertType.QUICK_CHECK -> {
                // to clear out workspace projection
                if (baseViewModel.activeSocketConnection.get()) {
                    GlobalScope.launch { Utility.sendDittoImage(requireActivity(), "solid_black") }
                }
                enterWorkspace()
            }
            Utility.AlertType.DEFAULT -> {
                Log.d("alertType", "DEFAULT")
            }
        }
    }

    private suspend fun projectBorderImage() {
        withContext(Dispatchers.IO) {
            val bitmap = Utility.getBitmapFromDrawable("setup_pattern_border", requireContext())

            var soc: Socket? = null
            try {
                soc = Socket(
                    core.network.NetworkUtility.nsdSericeHostName,
                    core.network.NetworkUtility.nsdSericePortName
                )
                if (soc.isConnected) {
                    val workspaceStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, workspaceStream)
                    val bitmapdata = workspaceStream.toByteArray()
                    bitmap.recycle()
                    val dataOutputStream: DataOutputStream =
                        DataOutputStream(soc.getOutputStream())
                    dataOutputStream.write(bitmapdata)
                    dataOutputStream.close()
                    withContext(Dispatchers.Main) {
                        showProgress(toShow = false)
                        showcalibrationbuttonclicked()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            CONNNECTION_FAILED,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                logger.d("Exception " + e.message)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        CONNNECTION_FAILED,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } finally {
                soc?.close()
            }
        }
    }

    /**
     * [Function] Calibration Button Click
     */
    private fun showcalibrationbuttonclicked() {
        val layout =
            activity?.layoutInflater?.inflate(R.layout.calibration_camera_alert_ws, null)

        val dialogBuilder =
            AlertDialog.Builder(
                ContextThemeWrapper(
                    requireContext(),
                    R.style.AlertDialogCustom
                )
            )
        dialogBuilder
            .setCancelable(false)
            .setNegativeButton(getString(R.string.cancel),DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
            })
            .setPositiveButton(getString(R.string.launch_camera), DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
                sendCalibrationPattern()
            })

        val alertCalibration = dialogBuilder.create()
        alertCalibration.setView(layout)
        alertCalibration.show()
    }

    override fun onNegativeButtonClicked(alertType: Utility.AlertType) {
        when {
            alertType == Utility.AlertType.BLE -> {
                logger.d("Later clicked")
                enterWorkspace()
            }
            alertType == Utility.AlertType.WIFI -> {
                enterWorkspace()
            }
            alertType == Utility.AlertType.CALIBRATION -> {
                sendQuickCheckImage()
            }
            alertType == Utility.AlertType.QUICK_CHECK -> {
                showProgress(toShow = true)
                GlobalScope.launch { projectBorderImage() }
            }
            alertType == Utility.AlertType.DEFAULT -> {
                Log.d("alertType", "DEFAULT")
            }
        }
    }

    override fun onNeutralButtonClicked(alertType: Utility.AlertType) {
        // to clear out workspace projection
        if (baseViewModel.activeSocketConnection.get()) {
            GlobalScope.launch { Utility.sendDittoImage(requireActivity(), "solid_black") }
        }
        enterWorkspace()
    }

    private fun navigateToCalibration() {
        if ((findNavController().currentDestination?.id == R.id.patternDescriptionFragment) || (findNavController().currentDestination?.id == R.id.patternDescriptionFragmentFromHome)) {
            findNavController().navigate(
                R.id.action_pattern_description_to_calibration,
                bundleOf(
                    "PatternId" to viewModel.clickedTailornovaID.get(),
                    "isFromPatternDescription" to true
                )
            )
        }
    }

    override fun onCustomPositiveButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        when (alertType) {
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
            Utility.AlertType.NETWORK -> {
                activity?.onBackPressed()
            }
            Utility.AlertType.CALIBRATION -> {
                showProgress(toShow = true)
                GlobalScope.launch { projectBorderImage() }
            }
            Utility.AlertType.QUICK_CHECK -> {
                // to clear out workspace projection
                if (baseViewModel.activeSocketConnection.get()) {
                    GlobalScope.launch { Utility.sendDittoImage(requireActivity(), "solid_black") }
                }
                enterWorkspace()
            }
            Utility.AlertType.DEFAULT -> {
                Log.d("alertType", "DEFAULT")
            }
        }
    }

    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        when {
            alertType == Utility.AlertType.BLE -> {
                logger.d("Later clicked")
                enterWorkspace()
            }
            alertType == Utility.AlertType.WIFI -> {
                enterWorkspace()
            }
            alertType == Utility.AlertType.CALIBRATION -> {
                sendQuickCheckImage()
            }
            alertType == Utility.AlertType.QUICK_CHECK -> {
                showProgress(toShow = true)
                GlobalScope.launch { projectBorderImage() }
            }
            alertType == Utility.AlertType.DEFAULT -> {
                Log.d("alertType", "DEFAULT")
            }
        }
    }

    private fun showDataFailedAlert() {
        bottomNavViewModel.showProgress.set(false)
        Utility.getCommonAlertDialogue(
            requireContext(),
            "",
            getString(R.string.str_fetch_error),
            "",
            getString(R.string.str_ok),
            this,
            Utility.AlertType.NETWORK,
            Utility.Iconype.FAILED
        )
    }
}