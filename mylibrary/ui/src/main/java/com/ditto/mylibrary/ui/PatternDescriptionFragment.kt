package com.ditto.mylibrary.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.ditto.connectivity.ConnectivityActivity
import com.ditto.connectivity.ConnectivityUtils
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.ui.databinding.PatternDescriptionFragmentBinding
import com.joann.fabrictracetransform.transform.TransformErrorCode
import com.joann.fabrictracetransform.transform.performTransform
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

class PatternDescriptionFragment : BaseFragment(), Utility.CallbackDialogListener {

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
    private val CONNNECTION_FAILED = "Socket Connection failed. Try again!!" // Compliant


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
        toolbarViewModel.isShowActionBar.set(true)
        (activity as BottomNavigationActivity).setToolbarTitle("Pattern Description")
        toolbarViewModel.isShowTransparentActionBar.set(false)
        bottomNavViewModel.visibility.set(true)
        baseViewModel.activeSocketConnection.set(false)
        if (viewModel.data.value == null) {
            arguments?.getInt("clickedID")?.let { viewModel.clickedID.set(it) }
            viewModel.fetchPattern()
            setUIEvents()
        } else {
            setPatternImage()
        }
        outputDirectory = Utility.getOutputDirectory(requireContext())
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 111
        private const val REQUEST_ACTIVITY_RESULT_CODE = 121
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.BLUETOOTH)
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
            if (core.network.Utility.nsdSericeHostName.isEmpty() && core.network.Utility.nsdSericePortName == 0) {
                showConnectivityPopup()
            } else {
                withContext(Dispatchers.Main) { showProgress(true) }
                if (startSocketConnection(
                        core.network.Utility.nsdSericeHostName,
                        core.network.Utility.nsdSericePortName
                    )
                ) {
                    baseViewModel.activeSocketConnection.set(true)
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
        Utility.getAlertDialogue(
            requireContext(),
            resources.getString(R.string.ditto_connect),
            resources.getString(R.string.ble_connectivity),
            resources.getString(R.string.skips),
            resources.getString(R.string.turnon),
            this,
            Utility.AlertType.BLE
        )
    }

    private fun showWifiDialogue() {

        Utility.getAlertDialogue(
            requireContext(),
            resources.getString(R.string.ditto_connect),
            resources.getString(R.string.wifi_connectivity),
            resources.getString(R.string.skips),
            resources.getString(R.string.settings),
            this,
            Utility.AlertType.WIFI
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
            Utility.AlertType.CALIBRATION
        )

    }

    private fun showQuickCheckDialog() {
        Utility.getAlertDialogue(
            requireContext(),
            resources.getString(R.string.setup_quickcheck_title),
            resources.getString(R.string.setup_quickcheck_message),
            resources.getString(R.string.calibrate),
            resources.getString(R.string.yes_string),
            this,
            Utility.AlertType.QUICK_CHECK
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
        if (toShow) {
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
        }
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
                    core.network.Utility.nsdSericeHostName,
                    core.network.Utility.nsdSericePortName
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
                    checkBluetoothWifiPermission()
                } else {
                    logger.d("OnClick Workspace failed")
                }
            }
            is PatternDescriptionViewModel.Event.OnDataUpdated -> {
                setData()
            }
            is PatternDescriptionViewModel.Event.OnInstructionsButtonClicked -> {
                if ((findNavController().currentDestination?.id == R.id.patternDescriptionFragment)
                    || (findNavController().currentDestination?.id == R.id.patternDescriptionFragmentFromHome)
                ) {
                    val bundle =
                        bundleOf("PatternName" to viewModel.data.value?.patternPieces?.get(0)?.parentPattern)
                    findNavController().navigate(
                        R.id.action_patternDescriptionFragment_to_pattern_instructions_Fragment,
                        bundle
                    )
                } else
                    Unit
            }
            PatternDescriptionViewModel.Event.OnDownloadComplete -> TODO()
        }


    private fun showConnectivityPopup() {
        val intent = Intent(requireContext(), ConnectivityActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivityForResult(
            intent,
            REQUEST_ACTIVITY_RESULT_CODE
        )
    }


    private fun setData() {
        viewModel.patternName.set(viewModel.data.value?.patternName)
        viewModel.patternDescription.set(viewModel.data.value?.description)
        viewModel.patternStatus.set(viewModel.data.value?.status)
        setPatternImage()
    }

    private fun setPatternImage() {

        val res: Resources = requireActivity().resources
        println("ImagefromDB${viewModel.data.value?.descriptionImages?.get(0)?.imagePath}")
        if (!viewModel.data.value?.descriptionImages?.get(0)?.imagePath.equals("")) {
            val resID: Int = res.getIdentifier(
                viewModel.data.value?.descriptionImages?.get(0)?.imagePath,
                "drawable",
                requireContext().packageName
            )
            val drawable: Drawable = res.getDrawable(resID)
            val bitmap = (drawable as BitmapDrawable).bitmap
            image_pattern_desc.setImageBitmap(bitmap)
        }
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
                baseViewModel.activeSocketConnection.set(true)
                showCalibrationDialog()
            } else if (data?.data.toString().equals("skip")) {
                baseViewModel.activeSocketConnection.set(false)
                if ((findNavController().currentDestination?.id == R.id.patternDescriptionFragment) || (findNavController().currentDestination?.id == R.id.patternDescriptionFragmentFromHome)) {
                    val bundle = bundleOf("PatternId" to viewModel.clickedID.get())
                    findNavController().navigate(
                        R.id.action_patternDescriptionFragment_to_WorkspaceFragment,
                        bundle
                    )
                } else {
                    logger.d("")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.textWatchvideo2.isEnabled = true
    }

    private fun enterWorkspace() {
        val bundle = bundleOf("PatternId" to viewModel.clickedID.get())
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
            val bitmap = Utility.getBitmapFromDrawable("calibration_border", requireContext())

            var soc: Socket? = null
            try {
                soc = Socket(
                    core.network.Utility.nsdSericeHostName,
                    core.network.Utility.nsdSericePortName
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
            activity?.layoutInflater?.inflate(R.layout.calibration_camera_alert, null)

        val dialogBuilder =
            AlertDialog.Builder(
                ContextThemeWrapper(
                    requireContext(),
                    R.style.AlertDialogCustom
                )
            )
        dialogBuilder
            .setCancelable(false)
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
                sendCalibrationPattern()
            })

        val alertCalibration = dialogBuilder.create()
        alertCalibration.setView(layout)
        alertCalibration.show()
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.getDefaultDisplay().getMetrics(displayMetrics)
        val displayWidth: Int = displayMetrics.widthPixels
        val displayHeight: Int = displayMetrics.heightPixels
        val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alertCalibration.window?.attributes)
        val dialogWindowWidth = (displayWidth * 0.7f).toInt()
        val dialogWindowHeight = (displayHeight * 0.7f).toInt()
        layoutParams.width = dialogWindowWidth
        layoutParams.height = dialogWindowHeight
        alertCalibration.window?.attributes = layoutParams
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

    override fun onNeutralButtonClicked() {
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
                    "PatternId" to viewModel.clickedID.get(),
                    "isFromPatternDescription" to true
                )
            )
        }
    }
}