package com.ditto.mylibrary.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.ditto.mylibrary.ui.databinding.FragmentPatternInstructionsBinding
import com.ditto.workspace.ui.R
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import core.PDF_SAMPLE_URL
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.fragment_pattern_instructions.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PatternInstructionsFragment : BaseFragment(),Utility.CustomCallbackDialogListener {

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
        toolbarViewModel.isShowActionBar.set(false)
        bottomNavViewModel.visibility.set(false)
        (activity as BottomNavigationActivity).setToolbarTitle("Pattern Instructions")
        toolbarViewModel.isShowTransparentActionBar.set(false)
        (activity as BottomNavigationActivity).hidemenu()
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbarInstrctions)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_instrctions.setNavigationIcon(com.ditto.mylibrary.ui.R.drawable.ic_back_button)
     /*   toolbarViewModel.isShowActionBar.set(true)
        (activity as BottomNavigationActivity).setToolbarTitle("Pattern Instructions")
        toolbarViewModel.isShowTransparentActionBar.set(false)*/
        bottomNavViewModel.visibility.set(false)
        (activity as BottomNavigationActivity).setToolbarIcon()
        toolbarViewModel.isShowActionMenu.set(false)
        (activity as BottomNavigationActivity).hidemenu()
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
            PatternDescriptionViewModel.Event.OnDownloadComplete -> showPdfFromUri(Uri.parse(
                viewModel.patternpdfuri.get()
            ))
            else -> Log.d("Error", "Invaid Event")
        }
    @RequiresApi(Build.VERSION_CODES.O)
    private  fun checkavailablefile() {
        downloadFileName = PDF_SAMPLE_URL?.substring(PDF_SAMPLE_URL.lastIndexOf('/'), PDF_SAMPLE_URL.length)
        val availableUri = downloadFileName?.let { Utility.isFileAvailable(it) }
        if (availableUri != null){
            showPdfFromUri(availableUri)
        } else {
            pdfdownload()
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun pdfdownload(){

        if (context?.let { core.network.Utility.isNetworkAvailable(it) }!!){
            bottomNavViewModel.showProgress.set(true)
            GlobalScope.launch {
                downloadFileName?.let { viewModel.downloadPDF(PDF_SAMPLE_URL, it) }
            }
        } else {
            showNeworkError()
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
            .onError {
                showRedownload()
            }
            .onPageError { page, _ ->
                showRedownload()
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

    private fun showNeworkError(){

        Utility.getCommonAlertDialogue(
            requireContext(),
            "",
            getString(R.string.str_no_internet),
            "",
            getString(R.string.str_ok),
            this,
            Utility.AlertType.NETWORK,
            Utility.Iconype.FAILED
        )
    }

    private fun showRedownload(){

        Utility.getCommonAlertDialogue(
            requireContext(),
            "",
            getString(R.string.str_unable_to_load),
            getString(R.string.str_retry),
            getString(R.string.str_cancel),
            this,
            Utility.AlertType.PDF,
            Utility.Iconype.FAILED
        )
    } 
    
      override fun onCustomPositiveButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        when (alertType) {
            Utility.AlertType.NETWORK,  Utility.AlertType.PDF -> {
                findNavController().popBackStack(R.id.patternInstructionsFragment,true)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
       when (alertType) {
            Utility.AlertType.PDF -> {
                pdfdownload()
            }
        }
    }
 
}

