package com.ditto.calibration.ui

/**
 * Created by Vishnu A V on  10/08/2020.
 * Fragment for Calibration Screen
 */

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.display.DisplayManager
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.ditto.calibration.R
import com.ditto.calibration.databinding.CalibrationFragmentBinding
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.joann.fabrictracetransform.calibrate.performCalibration
import com.joann.fabrictracetransform.transform.TransformErrorCode
import com.joann.fabrictracetransform.transform.performTransform
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.calibration_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.net.Socket
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CalibrationFragment : BaseFragment(), Utility.CallbackDialogListener, Util.Callback {

    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(CalibrationFragment::class.java.simpleName)
    }
    private val viewModel: CalibrationViewModel by ViewModelDelegate()
    lateinit var binding: CalibrationFragmentBinding
    private lateinit var viewFinder: PreviewView
    private var imageCapture: ImageCapture? = null
    private var preview: Preview? = null
    private var initialCameraRotation: Int? = 0
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var outputDirectory: File
    private var count = 0
    private var imageArray: MutableList<Bitmap> = ArrayList()
    private lateinit var alert: AlertDialog
    lateinit var finalbitmap: Bitmap
    private var imageAnalyzer: ImageAnalysis? = null
    var isFromHome: Boolean = false
    var isSaveCalibrationPhotos: Boolean = false
    lateinit var backpressCall: OnBackPressedCallback
    private var isBackPressed: Boolean = true
    private var alertImageConfirmation: AlertDialog? = null

    /**
     * [Function] onCreateView where setting up the viewmodel and binding to the layout
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = CalibrationFragmentBinding.inflate(
            inflater
        )
            .also {
                it.viewModel = viewModel
                it.lifecycleOwner = viewLifecycleOwner
            }
        return binding.root
    }


    /**
     * [Function] onActivityCreated where setting up the toolbar and initial DB calls
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setToolbar()
        arguments?.getBoolean("isFromHome")?.let { isFromHome = (it) }
        arguments?.getBoolean("isSaveCalibrationPhotos")?.let { isSaveCalibrationPhotos = (it) }
        outputDirectory = Utility.getOutputDirectory(requireContext())
        cameraExecutor = Executors.newSingleThreadExecutor()
        if (allPermissionsGranted()) {
            startCamera()
        } else {

            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
        backpressCall =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    if (isBackPressed) {
                        viewModel.disposable.clear()
                        isEnabled = false
                        isBackPressed = false
                        if (arguments?.getBoolean("isRecalibrate") != null && arguments?.getBoolean(
                                "isRecalibrate"
                            )!!
                        ) {
                            GlobalScope.launch {
                                Utility.sendDittoImage(
                                    requireContext(),
                                    "ditto_project"
                                )
                            }
                        } else {
                            GlobalScope.launch {
                                Utility.sendDittoImage(
                                    requireContext(),
                                    "ditto_project"
                                )
                            }
                        }
                        activity?.onBackPressed()
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backpressCall)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewFinder = binding.cameraviewFinder
    }

    /**
     * [Function] Handling Events from View Model
     */
    private fun handleEvent(event: CalibrationViewModel.Event) =
        when (event) {
            is CalibrationViewModel.Event.OnTakePhotoClicked -> {
                takePhoto()
                count = 1
            }
            is CalibrationViewModel.Event.OnInstructionClicked -> {
                onClickInstruction()
            }

        }

    /**
     * [Function] Navigating to calibration instruction screen
     */
    private fun onClickInstruction() {
        viewModel.disposable.clear()
        val bundle = bundleOf(
            "InstructionId" to 2,
            "isFromOnBoarding" to false,
            "isFromCamera" to true
        ) // For fetching calibration data
        if (findNavController().currentDestination?.id == R.id.destination_calibrationFragment) {
            findNavController().navigate(
                R.id.action_destination_calibrationFragment_to_instruction_navigation,
                bundle
            )
        }
        Unit
    }

    /**
     * Variable creations
     */
    companion object {

        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
            )
        }
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

    /**
     * [Function] Function to check permissions for opening camera
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        context?.let { it1 ->
            ContextCompat.checkSelfPermission(
                it1, it
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    /**
     * [Function] Camera button clicked
     */
    private fun takePhoto() {
        viewModel.isShowCameraButton.set(false)
        val imageCapture = imageCapture ?: return
        imageCapture!!.targetRotation = binding.cameraviewFinder.display.rotation
        /*val photoFile = File(
            outputDirectory, "TRACE_IMAGE_" +
                    SimpleDateFormat(
                        FILENAME_FORMAT, Locale.US
                    ).format(System.currentTimeMillis()) + ".jpg"
        )
        //Utility.setSharedPref(requireContext(), photoFile.absolutePath)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()*/

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(activity),
            object : ImageCapture.OnImageCapturedCallback() {
                @SuppressLint("UnsafeExperimentalUsageError")
                override fun onCaptureSuccess(imageProxy: ImageProxy) {

                    imageProxy.image?.let {
                        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                        finalbitmap = it.toBitmap(rotationDegrees)
                        captured_image.setImageBitmap(finalbitmap)

                        viewModel.isShowCameraView.set(false)
                        viewModel.isShowFinalImage.set(true)
                        viewModel.isShowDialog.set(false) //Lottie dismissed
                        hidetoolbar()
                        // Utility.galleryAddPic(requireContext(), photoFile.absolutePath)
                        if (count == 1) {
                            imageArray.add(finalbitmap)
                            showImageConfirmationclicked(finalbitmap)
                        }
                        super.onCaptureSuccess(imageProxy)
                    }

                }

                override fun onError(exception: ImageCaptureException) {
                    logger.d("Image Capture Failed " + exception.message)
                }
            })

        //commented below code to Remove photo saving capability into personal device.
        /*imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(activity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    logger.d("Image Capture Failed " + exc.message)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val bitmap: Bitmap =
                        MediaStore.Images.Media.getBitmap(activity?.contentResolver, savedUri)
                    viewModel.isShowCameraView.set(false)
                    viewModel.isShowFinalImage.set(true)
                    viewModel.isShowDialog.set(false) //Lottie dismissed
                    hidetoolbar()
                    Utility.galleryAddPic(requireContext(), photoFile.absolutePath)
                    if (count == 1) {
                        imageArray.add(bitmap)// can we pass finalbitmap insted of bitmap
                        showImageConfirmationclicked(finalbitmap)
                    }
                }
            })*/

    }

    private fun Image.toBitmap(rotationDegrees: Int): Bitmap {
        val buffer = planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
        val matrix = Matrix()
        matrix.postRotate(rotationDegrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * [Function] Hiding toolbar after taking the photo to show full screen image
     */
    private fun hidetoolbar() {
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowHomeEnabled(false)
    }

    private fun calibrateImage() {
        // for showing the calibration animation again
        baseViewModel.isCalibrated.set(false)

        logger.d("TRACE_ Projection : performCalibration  Start" + Calendar.getInstance().timeInMillis)
        showProgress(true)
        viewModel.disposable += Observable.fromCallable {
            performCalibration(imageArray.toTypedArray(), context?.applicationContext,isSaveCalibrationPhotos)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { Util.handleResult(it, this) }
    }

    private fun transform() {
        logger.d("TRACE_ Projection : performTransform  Start" + Calendar.getInstance().timeInMillis)
        showProgress(true)
        val bitmap = Utility.getBitmapFromDrawable("calibration_check_pattern", requireContext())

        viewModel.disposable += Observable.fromCallable {
            performTransform(bitmap, context?.applicationContext, null, false)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            //.whileSubscribed { showProgress(it) }
            .subscribeBy { handleResult(it, false) }
    }

    private fun showProgress(toShow: Boolean) {
        viewModel.isProgressLoading.set(toShow)
//        if (toShow) {
//            val layout =
//                activity?.layoutInflater?.inflate(R.layout.progress_dialog, null)
//
//            val dialogBuilder = AlertDialog.Builder(requireContext())
//            dialogBuilder
//                .setCancelable(false)
//            alert = dialogBuilder.create()
//            alert.setView(layout)
//            alert.show()
//        } else {
//            alert.dismiss()
//        }
    }

    private fun handleResult(result: Pair<TransformErrorCode, Bitmap>, isRecalibration: Boolean) {
        logger.d("quick check Transform - ${result.second.width} * ${result.second.height}")
        logger.d("TRACE_ Projection : transformation " + Calendar.getInstance().timeInMillis)
        when (result.first) {
            TransformErrorCode.Success -> GlobalScope.launch {
                sendTransformedImage(
                    Utility.addBlackBackgroundToBitmap(
                        result.second
                    ), isRecalibration
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

    private suspend fun sendTransformedImage(result: Bitmap, isRecalibration: Boolean) {
        logger.d("TRACE_ Projection : send Image Start" + Calendar.getInstance().timeInMillis)
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
                        if (isRecalibration) {
                            restartCamera()
                        } else {
                            showTransformSuccessPopup()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        baseViewModel.activeSocketConnection.set(false)
                        restartCamera()
                        Toast.makeText(
                            requireContext(),
                            "Projector Connection failed. Try again!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                logger.d("Exception " + e.message)
                withContext(Dispatchers.Main) {
                    baseViewModel.activeSocketConnection.set(false)
                    restartCamera()
                    Toast.makeText(
                        requireContext(),
                        "Projector Connection failed. Try again!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } finally {
                soc?.close()
                logger.d("TRACE_ Projection : send Image Finish" + Calendar.getInstance().timeInMillis)
            }
        }
    }

    private fun showTransformSuccessPopup() {
        baseViewModel.isCalibrated.set(true)
        showProgress(false)
        viewModel.isShowDialog.set(true)
        Utility.showAlertDialogue(
            requireContext(),
            R.drawable.ic_calibration_success,
            getString(R.string.calibration_success),
            "",
            "OK",
            this,
            Utility.AlertType.CALIBRATION
        )

//        Utility.getAlertDialogue(
//            requireContext(),
//            "Calibration Completed",
//            "Does projected image line up with the features on  calibration pattern?",
//            "NO",
//            "YES",
//            this,
//            Utility.AlertType.CALIBRATION
//        )
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)

        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    /**
     * [Function] Starting camera API
     */
    private fun startCamera() {
        viewModel.isShowCameraButton.set(true) //Lottie will Dismiss
        cameraviewFinder?.rotation = 0F
        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)

        val cameraProviderFuture = activity?.let { ProcessCameraProvider.getInstance(it) }
        cameraProviderFuture?.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            preview = Preview.Builder().setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(viewFinder.display.rotation)
                .build()
                .also {
                    it.setSurfaceProvider(cameraviewFinder.createSurfaceProvider())
                }

            imageCapture = ImageCapture.Builder().setTargetAspectRatio(screenAspectRatio)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            imageAnalyzer = ImageAnalysis.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageCapture, imageAnalyzer
                )
            } catch (exc: Exception) {
                logger.d("Use case binding failed " + exc.message)
            }

        }, ContextCompat.getMainExecutor(activity))
        logger.d(
            "onDisplayChanged" + viewFinder.display.displayId + " rotation "
                    + viewFinder.display.rotation + "  cameraviewFinder?.rotation  "
                    + cameraviewFinder?.rotation + " viewFinder?.deviceRotationForRemoteDisplayMode     "
                    + viewFinder?.deviceRotationForRemoteDisplayMode
        )
        initialCameraRotation = viewFinder.display.rotation
    }

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayChanged(displayId: Int) {
            logger.d("onDisplayChanged" + displayId + "   " + viewFinder.display.displayId)
            if (viewFinder.display.displayId == displayId) {
                val rotation = viewFinder.display.rotation
                logger.d(
                    "onDisplayChanged" + displayId + "   "
                            + viewFinder.display.displayId + " rotation " + rotation
                            + " viewFinder?.deviceRotationForRemoteDisplayMode     "
                            + viewFinder?.deviceRotationForRemoteDisplayMode
                )
                cameraviewFinder?.rotation = if (rotation == initialCameraRotation) 0F else 180F
                imageAnalyzer?.targetRotation = rotation
                imageCapture?.targetRotation = rotation
            }
        }

        override fun onDisplayAdded(displayId: Int) {
            logger.d("displayListener, onDisplayAdded")
        }

        override fun onDisplayRemoved(displayId: Int) {
            logger.d("displayListener, onDisplayRemoved")
        }
    }

    override fun onStart() {
        super.onStart()
        val displayManager =
            requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displayManager.registerDisplayListener(displayListener, null)
    }

    override fun onStop() {
        super.onStop()
        val displayManager =
            requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displayManager.unregisterDisplayListener(displayListener)
    }

    /**
     * [Function] Call back when user allow/deny the permission
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults:
        IntArray,
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                logger.d("Permission Denied by the user")
                Utility.getAlertDialogue(
                    requireContext(),
                    getString(R.string.permissions_required),
                    getString(R.string.camera_permissions_denied),
                    getString(R.string.cancel),
                    getString(R.string.go_to_settings),
                    this,
                    Utility.AlertType.PERMISSION_DENIED
                )
            }
        }
    }

    /**
     * [Function] for setting the toolbar
     */
    private fun setToolbar() {
        bottomNavViewModel.visibility.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(false)
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbarCalibration)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowHomeEnabled(true)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowTitleEnabled(false)
        (activity as AppCompatActivity?)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding.toolbarCalibration.setNavigationOnClickListener {
            activity?.onBackPressed()
            /*if(baseViewModel.activeSocketConnection.get()) {
                Utility.sendDittoImage(requireContext(), "ditto_project")
            }*/
        }
        binding.headerViewTitle.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun onPositiveButtonClicked(alertType: Utility.AlertType) {
        if (alertType.equals(Utility.AlertType.PERMISSION_DENIED)) {
            Utility.navigateToAppSettings(requireContext())
        }
        baseViewModel.isSetUpError.set(false)
        if (!baseViewModel.isCalibrated.get()) {
            baseViewModel.isCalibrated.set(true)
//            baseViewModel.isUserNeedCalibrated.set(false)
        }
        if (findNavController().currentDestination?.id == R.id.destination_calibrationFragment) {
            if (arguments?.getBoolean("isFromPatternDescription")!!) {
                if (arguments?.getBoolean("isRecalibrate") != null && arguments?.getBoolean("isRecalibrate")!!) {
                    activity?.onBackPressed()
                } else {
                    // to clear out workspace projection
                    GlobalScope.launch { Utility.sendDittoImage(requireContext(), "ditto_project") }
                    findNavController().navigate(
                        R.id.action_destination_calibrationFragment_to_workspace_navigation,
                        bundleOf("PatternId" to arguments?.getInt("PatternId"))
                    )
                }
            } else {
                // to clear out workspace projection
                GlobalScope.launch { Utility.sendDittoImage(requireContext(), "ditto_project") }
                val bundle = bundleOf(
                    "isFromHome" to isFromHome,
                    "isFromOnBoarding" to arguments?.getBoolean("isFromOnBoarding"),
                    "InstructionId" to 3
                )
                findNavController().navigate(
                    R.id.action_destination_calibrationFragment_to_howto_navigation, bundle
                )
            }
        }
    }

    override fun onNegativeButtonClicked(alertType: Utility.AlertType) {
        when (alertType) {
            Utility.AlertType.CALIBRATION -> {
                if (baseViewModel.isSetUpError.get()) {
                    activity?.onBackPressed()
                } else {
                    sendCalibrationPattern() //Sent Pattern Image
                }
            }
            Utility.AlertType.DEFAULT -> restartCamera()
            else -> {
                logger.d("event, undefined")
            }
        }
    }

    override fun onNeutralButtonClicked(alertType: Utility.AlertType) {
        if (alertType == Utility.AlertType.CALIBRATION) {
            // Added black image while navigating to tutorial
            GlobalScope.launch {
                Utility.sendDittoImage(
                    requireContext(),
                    "ditto_project"
                )
            }
            baseViewModel.isSetUpError.set(false)
            if (findNavController().currentDestination?.id == R.id.destination_calibrationFragment) {
                val bundle = bundleOf("isFromHome" to true)
                findNavController().navigate(R.id.action_workspace_to_tutorial, bundle)
            }
        }
    }

    private fun restartCamera() {
        viewModel.isShowDialog.set(true) //Lottie Dismissed
        viewModel.isShowCameraButton.set(true)
        viewModel.isShowCameraView.set(true)
        viewModel.isShowFinalImage.set(false)
        setToolbar()
        startCamera()
        imageArray.clear()
    }

    private fun sendCalibrationPattern() {
        viewModel.isShowDialog.set(false)    //Lottie Displayed.....
        val bitmap =
            Utility.getBitmapFromDrawable("calibration_transformed", requireContext())
        GlobalScope.launch {
            sendTransformedImage(
                Utility.addBlackBackgroundToBitmap(
                    bitmap
                ), isRecalibration = true
            )
        }
/*
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
            .subscribeBy { handleResult(it, true) }*/
    }


    override fun onCalibrationReponse(calibrationResponse: Util.CalibrationType) {
        baseViewModel.isSetUpError.set(false)
        logger.d("TRACE_ Projection : OnCalibrationReponse  Finish" + Calendar.getInstance().timeInMillis)
        showProgress(false)
        when (calibrationResponse) {
            Util.CalibrationType.Success -> {
                transform()  //On calibration response
            }

            Util.CalibrationType.PatternImageIsCropped -> {
                baseViewModel.isSetUpError.set(true)
                showAlert(resources.getString(R.string.calibrationerror_pattern_image_is_cropped))
            }

            Util.CalibrationType.CameraDistanceTooFarBack -> {
                baseViewModel.isSetUpError.set(true)
                showAlert(resources.getString(R.string.calibrationerror_camera_distance_far_back))
            }

            Util.CalibrationType.CameraHeightTooLow -> {
                baseViewModel.isSetUpError.set(true)
                showAlert(resources.getString(R.string.calibrationerror_camera_height_too_low))
            }

            Util.CalibrationType.CameraTooFarLeftOrRight -> {
                baseViewModel.isSetUpError.set(true)
                showAlert(resources.getString(R.string.calibrationerror_camera_too_far_left_right))
            }

            Util.CalibrationType.OrientationNotLandscape -> {
                baseViewModel.isSetUpError.set(true)
                showAlert(resources.getString(R.string.calibrationerror_orientation_not_landscape))
            }

            Util.CalibrationType.CameraResolutionTooLow -> {
                baseViewModel.isSetUpError.set(true)
                showAlert(resources.getString(R.string.calibrationerror_camera_resolution_too_low))
            }

            Util.CalibrationType.MatIsRotated180Degrees -> {
                baseViewModel.isSetUpError.set(true)
                showAlert(resources.getString(R.string.calibrationerror_mat_rotated_180_deg))
            }

            Util.CalibrationType.ImageTooBlurr -> {
                baseViewModel.isSetUpError.set(true)
                showAlert(resources.getString(R.string.calibrationerror_image_too_blur))
            }
            Util.CalibrationType.ImageTooBright -> {
                baseViewModel.isSetUpError.set(true)
                showAlert(resources.getString(R.string.calibrationerror_image_too_bright))
            }
            Util.CalibrationType.FailCalibration -> {
                baseViewModel.isSetUpError.set(true)
                showAlert(resources.getString(R.string.calibrationerror_failure))
            }

            Util.CalibrationType.Else -> {
                baseViewModel.isSetUpError.set(true)
                showAlert(resources.getString(R.string.calibrationerror_else))
            }

        }
    }

    private fun showAlert(message: String) {
        viewModel.isShowDialog.set(true)
        Utility.showAlertDialogue(
            requireContext(),
            R.drawable.ic_calibration_failure,
            message,
//            String.format(getString(R.string.calibration_failure), message),
            "TUTORIAL",
            "RETRY",
            "SKIP CALIBRATION",
            this,
            Utility.AlertType.CALIBRATION
        )
    }

    private fun showImageConfirmationclicked(bitmap: Bitmap) {
        val layout =
            activity?.layoutInflater?.inflate(R.layout.calibration_image_confirmation_ws, null)

        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setCancelable(false)
        //if(alertImageConfirmation == null){
        alertImageConfirmation = dialogBuilder.create()
        //}
        alertImageConfirmation?.setView(layout)
        alertImageConfirmation?.show()
        val retake = layout?.findViewById(R.id.textRetake) as TextView
        val submit = layout.findViewById(R.id.textSubmit) as TextView
        val confirmedImage = layout.findViewById(R.id.alertImageconfirmation) as ImageView
        confirmedImage.setImageBitmap(bitmap)
        submit.setOnClickListener {
            alertImageConfirmation?.dismiss()
            /*viewModel.isShowCameraView.set(false)
            viewModel.isShowFinalImage.set(true)*/
            calibrateImage()
        }
        retake.setOnClickListener {
            alertImageConfirmation?.dismiss()
            restartCamera()
            // todo where to redirect
            /*  if (baseViewModel.activeSocketConnection.get()) {
                  GlobalScope.launch { Utility.sendDittoImage(requireActivity(), "ditto_project") }
              }*/
        }
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.getDefaultDisplay().getMetrics(displayMetrics)
        val displayWidth: Int = displayMetrics.widthPixels
        val displayHeight: Int = displayMetrics.heightPixels
        val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alertImageConfirmation?.window?.attributes)
        val dialogWindowWidth = (displayWidth * 0.8f).toInt()
        val dialogWindowHeight = (displayHeight * 0.6f).toInt()
        layoutParams.width = dialogWindowWidth
        layoutParams.height = dialogWindowHeight
        alertImageConfirmation?.window?.attributes = layoutParams
    }
}


