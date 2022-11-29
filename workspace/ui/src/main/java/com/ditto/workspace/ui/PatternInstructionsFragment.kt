package com.ditto.workspace.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.workspace.ui.databinding.FragmentWsPatternInstructionsBinding
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import core.PDF_DOWNLOAD_URL
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.fragment_ws_pattern_instructions.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class PatternInstructionsFragment : BaseFragment(),Utility.CustomCallbackDialogListener {

    private val viewModel: WorkspaceViewModel by ViewModelDelegate()
    lateinit var binding: FragmentWsPatternInstructionsBinding
    var downloadFileName: String? = null
    var patternFolderName : String? = null
    var patternDownloadFolderName: String? = null
    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(PatternInstructionsFragment::class.java.simpleName)
    }
    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWsPatternInstructionsBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbarViewModel.isShowActionBar.set(false)
        bottomNavViewModel.visibility.set(false)
        (activity as BottomNavigationActivity).setToolbarIcon()
        (activity as BottomNavigationActivity).setToolbarTitle("Pattern Instructions")
        toolbarViewModel.isShowTransparentActionBar.set(false)
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbarInstrctions)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_instrctions.setNavigationIcon(R.drawable.ic_back_button)
        setUIEvents()
        patternFolderName = arguments?.getString("PatternName")
        patternDownloadFolderName = arguments?.getString("PatternFolderName")
        loadPdf()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 20
        private val REQUIRED_PERMISSIONS = emptyArray<String>()
//            arrayOf(
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            )

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        context?.let { it1 ->
            ContextCompat.checkSelfPermission(
                it1, it
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (allPermissionsGranted() && requestCode == REQUEST_CODE_PERMISSIONS) {
            checkavailablefile()
        }else{
            showRedownload()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkavailablefile() {
        downloadFileName =
            PDF_DOWNLOAD_URL?.substring(PDF_DOWNLOAD_URL!!.lastIndexOf('/'), PDF_DOWNLOAD_URL!!.length)
        val availableUri = downloadFileName?.let { Utility.isFileAvailable(it,requireContext(),patternFolderName) }
        if (availableUri != null) {
            showPdfFromUri(availableUri)
        } else {
              pdfdownload()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun pdfdownload(){

        if (context?.let { core.network.NetworkUtility.isNetworkAvailable(it) }!!){
            bottomNavViewModel.showProgress.set(true)
            GlobalScope.launch {
                downloadFileName?.let { viewModel.downloadPDF(PDF_DOWNLOAD_URL!!, it,patternFolderName) }
            }
        } else {
            showNeworkError()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadPdf() {
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

    private fun handleEvent(event: WorkspaceViewModel.Event) =
        when (event) {
            is WorkspaceViewModel.Event.OnDownloadComplete -> {
                showPdfFromUri(Uri.parse(viewModel.patternpdfuri.get()))
            }
            else -> logger.d("Error, Invaid Event")
        }

    private fun showPdfFromUri(pdfName: Uri) {
        bottomNavViewModel.showProgress.set(false)
        if(context == null) return
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

    private fun showPdfFromAssets(pdfName: String) {
        if(context == null) return
        binding.pdfView.fromAsset(pdfName)
            .defaultPage(0)
            .scrollHandle(DefaultScrollHandle(requireContext()))// set the default page to open
            .onPageError { page, _ ->
                Toast.makeText(
                    requireContext(),
                    "Error loading pdf", Toast.LENGTH_LONG
                ).show()
            }
            .load()
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
            else -> {}
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
       when (alertType) {
            Utility.AlertType.PDF -> {
                if (allPermissionsGranted()) {
                    pdfdownload()
                }else{
                    requestPermissions(
                        REQUIRED_PERMISSIONS,
                        REQUEST_CODE_PERMISSIONS
                    )
                }
            }
           else -> {}
       }
    }

}