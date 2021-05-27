package com.ditto.mylibrary.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.ditto.mylibrary.ui.databinding.FragmentPatternInstructionsBinding
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import core.PDF_PASSWORD
import core.PDF_SAMPLE_URL
import core.PDF_USERNAME
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class PatternInstructionsFragment : BaseFragment() {

    private val viewModel: PatternDescriptionViewModel by ViewModelDelegate()
    lateinit var binding: FragmentPatternInstructionsBinding
     var downloadFileName : String? = null
    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPatternInstructionsBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }
    /**
     * Variable creations
     */
    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 20
        private val REQUIRED_PERMISSIONS =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bottomNavViewModel.visibility.set(false)
        (activity as BottomNavigationActivity).setToolbarTitle("Pattern Instructions")
        setUIEvents()
        loadPdf()
        //showPdfFromAssets(arguments?.getString("PatternName") + ".pdf")
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadPdf(){

        if (allPermissionsGranted()) {
            checkavailablefile()
        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }
    private fun setUIEvents() {
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }
    private fun handleEvent(event: PatternDescriptionViewModel.Event) =
        when (event) {
            is PatternDescriptionViewModel.Event.OnWorkspaceButtonClicked ->   TODO()
            is PatternDescriptionViewModel.Event.OnDataUpdated ->  TODO()
            is PatternDescriptionViewModel.Event.OnInstructionsButtonClicked ->  TODO()
            PatternDescriptionViewModel.Event.OnDownloadComplete -> showPdfFromUri(Uri.parse(
                viewModel.patternpdfuri.get()
            ))
        }
    @RequiresApi(Build.VERSION_CODES.O)
    private  fun checkavailablefile() {
        downloadFileName = PDF_SAMPLE_URL?.substring(PDF_SAMPLE_URL.lastIndexOf('/'), PDF_SAMPLE_URL.length)
        val availableUri = downloadFileName?.let { Utility.isFileAvailable(it) }
        if (availableUri != null){
            showPdfFromUri(availableUri)
        } else {

            bottomNavViewModel.showProgress.set(true)
            GlobalScope.launch {
                downloadFileName?.let { viewModel.downloadPDF(PDF_SAMPLE_URL, it) }
            }
        }

    }

    private fun showPdfFromAssets(pdfName: String) {
        binding.pdfView.fromAsset(pdfName)
            .defaultPage(0) // set the default page to open
            .scrollHandle(DefaultScrollHandle(requireContext()))
            .onPageError { page, _ ->
                Toast.makeText(
                    requireContext(),
                    "Error loading pdf", Toast.LENGTH_LONG
                ).show()
            }
            .load()
    }

    private fun showPdfFromUri(pdfName: Uri) {
        bottomNavViewModel.showProgress.set(false)
        binding.pdfView.fromUri(pdfName)
            .defaultPage(0) // set the default page to open
            .scrollHandle(DefaultScrollHandle(requireContext()))
            .onPageError { page, _ ->
                Toast.makeText(
                    requireContext(),
                    "Error loading pdf", Toast.LENGTH_LONG
                ).show()
            }
            .load()
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
     * [Function] Call back when user allow/deny the permission
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            checkavailablefile()
        }
    }



}

