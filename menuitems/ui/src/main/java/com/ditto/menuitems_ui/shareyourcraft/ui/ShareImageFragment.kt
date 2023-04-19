package com.ditto.menuitems_ui.shareyourcraft.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.menuitems_ui.R
import com.ditto.menuitems_ui.databinding.FragmentShareImageBinding
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.fragment_share_image.view.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ShareImageFragment : BaseFragment() {

    private val viewModel: ShareImageViewModel by ViewModelDelegate()
    lateinit var binding: FragmentShareImageBinding

    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(ShareImageFragment::class.java.simpleName)
    }
    lateinit var imgUri: Uri
    private var imageCapture: ImageCapture? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentShareImageBinding.inflate(inflater).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setuptoolbar()
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backPressCall)
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)   //Observing UI event
            }

    }

    private val backPressCall = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewModel.isCameraVisible.get()) {
                viewModel.isCameraVisible.set(false)
                toolbarViewModel.isShowActionBar.set(true)
                if (::imgUri.isInitialized) viewModel.isShareButtonVisible.set(true)
            } else {
                isEnabled = false
                findNavController().popBackStack()
                // (activity as BottomNavigationActivity).onBackPressed()
            }
        }
    }

    private fun handleEvent(event: ShareImageViewModel.Event) {
        when (event) {
            is ShareImageViewModel.Event.OnOpenCameraClicked -> {
                if (!allPermissionsGranted()) {
                    requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
                } else {
                    startCamera()
                }
            }
            is ShareImageViewModel.Event.OnCameraButtonClicked -> {
                takePhoto()
            }
            is ShareImageViewModel.Event.OnPhotoGalleryClicked -> {
                val intent = Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
            }
            is ShareImageViewModel.Event.OnShareImageClicked -> {
                val shareText = binding.textToShare.text.toString()
                val intent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_STREAM, imgUri)
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "image/*"
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                startActivity(Intent.createChooser(intent, null))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM && resultCode == RESULT_OK) {
            imgUri = Uri.parse(data?.data.toString())
            binding.imageToShare.setImageURI(imgUri)
            viewModel.isShareButtonVisible.set(true)
        }
    }

    private fun startCamera() {
        viewModel.isCameraVisible.set(true)
        toolbarViewModel.isShowActionBar.set(false)

        val cameraProviderFuture = activity?.let { ProcessCameraProvider.getInstance(it) }
        cameraProviderFuture?.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.openCameraLayout.cameraviewFinder.createSurfaceProvider())
                }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                logger.d("Use case binding failed " + exc.message)
            }

        }, ContextCompat.getMainExecutor(activity))

    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val name = "dittoPatterns_" + SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver,
            inImage,
            name,
            null)
        return Uri.parse(path)
    }

    private fun takePhoto() {
        /* share photo without saving
        val imageCapture = imageCapture ?: return
         imageCapture.takePicture(
             ContextCompat.getMainExecutor(activity),
             object : ImageCapture.OnImageCapturedCallback() {
                 @SuppressLint("UnsafeExperimentalUsageError")
                 override fun onCaptureSuccess(imageProxy: ImageProxy) {

                     imageProxy.image?.let {
                         val capturedBitmap = it.toBitmap()
                         binding.imageToShare.setImageBitmap(capturedBitmap)
                         val capturedImgUri = activity?.let { it1 -> getImageUri(it1, capturedBitmap) }
                         if (capturedImgUri != null) {
                             imgUri = capturedImgUri
                         }
                         viewModel.isCameraVisible.set(false)
                         super.onCaptureSuccess(imageProxy)
                     }

                 }

                 override fun onError(exception: ImageCaptureException) {
                     logger.d("Image Capture Failed " + exception.message)
                 }
             })*/
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = "dittoPatterns_" + SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireContext().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    logger.e("Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    output.savedUri.let {
                        if (it != null) {
                            imgUri = it
                        }
                    }
                    binding.imageToShare.setImageURI(output.savedUri)
                    viewModel.isCameraVisible.set(false)
                    toolbarViewModel.isShowActionBar.set(true)
                    viewModel.isShareButtonVisible.set(true)
                }
            }
        )

    }

    fun Image.toBitmap(): Bitmap {
        val buffer = planes[0].buffer
        buffer.rewind()
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun setuptoolbar() {
        bottomNavViewModel.visibility.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(true)
        toolbarViewModel.isShowActionMenu.set(false)
        (activity as BottomNavigationActivity).setToolbarIcon()
        (activity as BottomNavigationActivity).setToolbarTitle(getString(R.string.share_your_craft))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        context?.let { it1 ->
            ContextCompat.checkSelfPermission(
                it1, it
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults:
        IntArray,
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Utility.getAlertDialogue(
                    requireContext(),
                    getString(R.string.permissions_required),
                    getString(R.string.camera_permissions_denied),
                    getString(R.string.cancel),
                    getString(R.string.go_to_settings),
                    object : Utility.CallbackDialogListener {
                        override fun onPositiveButtonClicked(alertType: Utility.AlertType) {
                            if (alertType.equals(Utility.AlertType.PERMISSION_DENIED)) {
                                Utility.navigateToAppSettings(requireContext())
                            }
                        }

                        override fun onNegativeButtonClicked(alertType: Utility.AlertType) {

                        }

                        override fun onNeutralButtonClicked(alertType: Utility.AlertType) {

                        }

                    },
                    Utility.AlertType.PERMISSION_DENIED
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backPressCall.isEnabled = false
        backPressCall.remove()
    }

    companion object {

        private const val REQUEST_SELECT_IMAGE_IN_ALBUM: Int = 101
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

        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    }
}