package com.ditto.instructions.ui

import android.Manifest
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
import com.ditto.instructions.ui.databinding.FragmentTutorialPdfBinding
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.workspace.ui.R
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class TutorialPdfFragment : BaseFragment(), Utility.CustomCallbackDialogListener {
    private val viewModel: TutorialPdfInstructionViewModel by ViewModelDelegate()

    lateinit var binding: FragmentTutorialPdfBinding
    var downloadFileName: String? = null
    var pdfUrl = ""

    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(TutorialPdfFragment::class.java.simpleName)
    }


    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentTutorialPdfBinding.inflate(
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
        private val REQUIRED_PERMISSIONS = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else {
            emptyArray<String>()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbarViewModel.isShowActionBar.set(false)
        bottomNavViewModel.visibility.set(false)
        pdfUrl = arguments?.getString("InstructionPdfUrl").toString()
        viewModel.toolbarTitle.set(arguments?.getString("InstructionPdfTitle").toString())
        viewModel.toolbarTitle.get()
            ?.let { (activity as BottomNavigationActivity).setToolbarTitle(it) }
        toolbarViewModel.isShowTransparentActionBar.set(false)
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbarInstrctions)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarInstrctions.setNavigationIcon(R.drawable.ic_back_button)
        bottomNavViewModel.visibility.set(false)
        (activity as BottomNavigationActivity).setToolbarIcon()
        toolbarViewModel.isShowActionMenu.set(false)
        Log.d("TAG","onActivityCreated")
        setUIEvents()
        loadPdf()
    }

    override fun onStop() {
        super.onStop()
        Log.d("TAG","onStop")
       // viewModel.disposable.dispose()
    }

    override fun onDestroyView() {
        Log.d("TAG","onDestroyView")
        viewModel.disposable.clear()
       // viewModel.disposable.dispose()
        super.onDestroyView()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadPdf() {
        Log.d("TAG","loadPdf")
        if (allPermissionsGranted()) {
            checkavailablefile()
        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    /*override fun onResume() {
        super.onResume()
        Log.d("TAG","onResume")
        if(viewModel.disposable.size().equals(0)) {
            setUIEvents()
            loadPdf()
        } else {
            loadPdf()
        }
    }*/

    private fun setUIEvents() {
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    private fun handleEvent(event: TutorialPdfInstructionViewModel.Event) =
        when (event) {
            TutorialPdfInstructionViewModel.Event.OnDownloadComplete -> {
                Log.d("TAG","OnDownloadComplete")
                showPdfFromUri(Uri.parse(
                    viewModel.patternpdfuri.get()))
            }
            else -> logger.d("Error, Invaid Event")
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkavailablefile() {
        if (!pdfUrl.isNullOrEmpty()) {
            Log.d("TAG","checkavailablefile")
            try {
                downloadFileName =
                    pdfUrl?.substring(pdfUrl!!.lastIndexOf('/'), pdfUrl!!.length)
                val availableUri = downloadFileName?.let {
                    viewModel.isFileAvailable(it)
                }
                if (availableUri != null) {
                    Log.d("TAG","availableUri")
                    showPdfFromUri(availableUri)
                } else {
                    Log.d("TAG","Not-availableUri")
                    pdfdownload()
                }
            } catch (e: Exception) {
                logger.d("EXCEPTION,${e.localizedMessage}")
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun pdfdownload() {
        Log.d("TAG","pdfdownload")
        if (core.network.NetworkUtility.isNetworkAvailable(requireContext())) {
            bottomNavViewModel.showProgress.set(true)
            GlobalScope.launch {
                // viewModel.loadPDF(sampleUrl)
                downloadFileName?.let { viewModel.downloadPDF(pdfUrl, it) }
            }
        } else {
            showNetworkError()
        }
    }


    private fun showPdfFromUri(pdfName: Uri) {
        Log.d("TAG","showPdfFromUri")
        bottomNavViewModel.showProgress.set(false)
        if (context == null) return
        binding.pdfView.fromUri(pdfName)
            .defaultPage(0) // set the default page to open
            .scrollHandle(DefaultScrollHandle(context))
            .onError {
                Log.d("TAG","onError")
                showRedownload()
            }
            .onPageError { page, _ ->
                Log.d("TAG","onPageError")
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
        requestCode: Int, permissions: Array<String>,
        grantResults:
        IntArray,
    ) {
        if (allPermissionsGranted() && requestCode == REQUEST_CODE_PERMISSIONS) {
            checkavailablefile()
        } else {
            showRedownload()
        }
    }

    private fun showNetworkError() {
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
        Log.d("TAG","showRedownload")
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
        alertType: Utility.AlertType,
    ) {
        when (alertType) {
            Utility.AlertType.NETWORK, Utility.AlertType.PDF -> {
                requireActivity().onBackPressed()
            }
            else -> {}
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType,
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