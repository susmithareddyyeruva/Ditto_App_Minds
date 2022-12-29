package com.ditto.mylibrary.ui

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.ui.databinding.FragmentYardageNotionBinding
import com.ditto.workspace.ui.R
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import core.YARDAGE_PDF_DOWNLOAD_URL
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.fragment_pattern_instructions.*
import kotlinx.android.synthetic.main.fragment_yardage_notion.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class YardageNotionFragment : BaseFragment(), Utility.CustomCallbackDialogListener {

    private val viewModel: PatternDescriptionViewModel by ViewModelDelegate()
    lateinit var binding: FragmentYardageNotionBinding
    var downloadFileName: String? = null
    var patternFolderName: String? = null
    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(YardageNotionFragment::class.java.simpleName)
    }
    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        binding = FragmentYardageNotionBinding.inflate(
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
        private val REQUIRED_PERMISSIONS = emptyArray<String>()
//            arrayOf(
////                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            )

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbarViewModel.isShowActionBar.set(false)
        bottomNavViewModel.visibility.set(false)
        (activity as BottomNavigationActivity).setToolbarTitle("Yardage and Notion")
        toolbarViewModel.isShowTransparentActionBar.set(false)
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbarYardage)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_yardage.setNavigationIcon(com.ditto.mylibrary.ui.R.drawable.ic_back_button)
        bottomNavViewModel.visibility.set(false)
        (activity as BottomNavigationActivity).setToolbarIcon()
        toolbarViewModel.isShowActionMenu.set(false)
        setUIEvents()
        patternFolderName = arguments?.getString("PatternName")
        arguments?.getString("notionDetails")?.let { viewModel.setNotionDetails(it) }
        arguments?.getStringArrayList("yardageDetails")?.let {  viewModel.setYardageDetails(it)}

        viewModel.setUI()
    }

    override fun onStop() {
        super.onStop()
        viewModel.disposable.clear()
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUIEvents() {
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleEvent(event: PatternDescriptionViewModel.Event) =
        when (event) {
            PatternDescriptionViewModel.Event.OnDownloadComplete -> showPdfFromUri(
                Uri.parse(
                    viewModel.patternpdfuri.get()
                )
            )
            PatternDescriptionViewModel.Event.OnYardagePdfAvailable -> {
                loadPdf()
            }
            else -> logger.d("Error, Invaid Event")
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkavailablefile() {
        if (!YARDAGE_PDF_DOWNLOAD_URL.isNullOrEmpty()) {
            try {
                downloadFileName =
                    YARDAGE_PDF_DOWNLOAD_URL?.substring(YARDAGE_PDF_DOWNLOAD_URL!!.lastIndexOf('/'), YARDAGE_PDF_DOWNLOAD_URL!!.length)
                val availableUri = downloadFileName?.let {
                    Utility.isFileAvailable(
                        it,
                        requireContext(),
                        patternFolderName + "yardage"
                    )
                }
                if (availableUri != null) {
                    showPdfFromUri(availableUri)
                } else {
                    pdfdownload()
                }
            } catch (e: Exception) {
                logger.d("EXCEPTION,${e.localizedMessage}")
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun pdfdownload() {

        if (context?.let { core.network.NetworkUtility.isNetworkAvailable(it) }!!) {
            bottomNavViewModel.showProgress.set(true)
            GlobalScope.launch {
                downloadFileName?.let {
                    viewModel.downloadPDF(
                        YARDAGE_PDF_DOWNLOAD_URL!!,
                        it,
                        patternFolderName + "yardage"
                    )
                }
            }
        } else {
            showNeworkError()
        }
    }


    private fun showPdfFromUri(pdfName: Uri) {
        viewModel.isYardagePDFAvailable.set(true)
        bottomNavViewModel.showProgress.set(false)
        if (context == null) return
        binding.pdfView.fromUri(pdfName)
            .defaultPage(0) // set the default page to open
            .scrollHandle(DefaultScrollHandle(context))
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
        if (allPermissionsGranted() && requestCode == REQUEST_CODE_PERMISSIONS) {
            checkavailablefile()
        } else {
            showRedownload()
        }
    }

    private fun showNeworkError() {
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

    private fun showRedownload() {

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
            Utility.AlertType.NETWORK, Utility.AlertType.PDF -> {
                findNavController().popBackStack(R.id.yardageNotionFragment, true)
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

