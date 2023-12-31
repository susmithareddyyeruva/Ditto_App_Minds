package com.ditto.instructions.ui

/**
 * Created by Vishnu A V on  03/08/2020.
 * Fragment class for loading  Beamsetup and Calibration Screen
 */
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.ditto.connectivity.ConnectivityActivity
import com.ditto.connectivity.ConnectivityUtils
import com.ditto.instructions.ui.adapter.InstructionAdapter
import com.ditto.instructions.ui.adapter.InstructionCalibrationAdapter
import com.ditto.instructions.ui.databinding.InstructionFragmentBinding
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.videoplayer.CustomPlayerControlActivity
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.instruction_adapter.view.*
import kotlinx.android.synthetic.main.instruction_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.net.Socket
import java.util.*
import javax.inject.Inject


class InstructionFragment constructor(
    val position: Int = 0,
    val isFromHome: Boolean = false,
) : BaseFragment(), Utility.CallbackDialogListener, Utility.CustomCallbackDialogListener {

    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(InstructionFragment::class.java.simpleName)
    }
    private val viewModel: InstructionViewModel by ViewModelDelegate()
    lateinit var binding: InstructionFragmentBinding
    private var alert: AlertDialog? = null

    /**
     * [Function] onCreateView where setting up the viewmodel and binding to the layout
     */
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (!::binding.isInitialized) {
            binding = InstructionFragmentBinding.inflate(
                inflater
            ).also {
                it.viewModel = viewModel
                it.lifecycleOwner = viewLifecycleOwner
            }.apply {
                if (arguments != null) {
                    arguments?.getInt("InstructionId")?.let { viewModel?.instructionID?.set(it) }
                    arguments?.getBoolean("isFromHome")?.let { viewModel?.isFromHome?.set(it) }
                    arguments?.getBoolean("isFromCamera")
                        ?.let { viewModel?.isFromCameraScreen?.set(it) }
                    arguments?.getBoolean("ISDNDCHECKED")?.let { viewModel?.isDSSchecked?.set(it) }
                }
            }
        }
        return binding.root
    }

    /**
     * [Function] onActivityCreated where setting up the toolbar and initial DB calls
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.isFromHome.set(isFromHome)
        viewModel.tabPosition.set(position)
        viewModel.isShowindicator.set(true)
        if (viewModel.data.value == null) {
            bottomNavViewModel.showProgress.set(true)
            viewModel.fetchInstructionData()
            viewModel.disposable += viewModel.events
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    handleEvent(it)
                }
        }
        setupToolbar()

        binding.instructionViewPager?.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                logger.d("onPageScroll, state changed")
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                logger.d("onPageScrolled, state scrolled")
            }

            override fun onPageSelected(position: Int) {
                val listsize = if (viewModel.data.value?.title.equals("Beam Setup & Takedown")) {
                    viewModel.data.value?.instructions?.get(position)?.instructions?.size
                } else {
                    viewModel.data.value?.instructions?.size
                }
                if (listsize != null) {
                    when (position) {
                        (listsize - 1) -> {
                            viewModel.isFinalPage.set(true)
                            viewModel.isStartingPage.set(false)
                        }
                        0 -> {
                            viewModel.isFinalPage.set(false)
                            viewModel.isStartingPage.set(true)
                        }
                        else -> {
                            viewModel.isFinalPage.set(false)
                            viewModel.isStartingPage.set(false)
                        }
                    }
                }
            }
        })

    }

    /**
     * [Function] Setting values in adapter class
     */
    private fun setInstructionadapter() {
        if (viewModel.instructionID.get() == 2 || viewModel.instructionID.get() == 3) {
            val adapter =
                InstructionCalibrationAdapter()
            binding.instructionViewPager.adapter = adapter
            adapter.viewModel = viewModel
            viewModel.isShowindicator.set(false)
            adapter.setListData(viewModel.data.value?.instructions!!)
            viewModel.isFinalPage.set(false)
            viewModel.isStartingPage.set(true)
            binding.bottomViewpager.visibility =
                if (viewModel.data.value?.instructions?.size ?: 0 > 1) View.VISIBLE else View.INVISIBLE
        } else {
            binding.bottomViewpager.visibility = View.INVISIBLE

            val adapter =
                InstructionAdapter(position)
            binding.instructionViewPager.adapter = adapter
            adapter.viewModel = viewModel
            viewModel.isShowindicator.set(false)
            viewModel.data.value?.instructions?.get(position)?.instructions.let {
                if (it != null) {
                    adapter.setListData(it)
                }
            }
            // disabling previous next button inside pager if count is 1
            if (viewModel.data.value?.instructions?.get(position)?.instructions?.size ?: 0 < 2) {
                viewModel.isFinalPage.set(true)
                viewModel.isStartingPage.set(true)
            }
        }
        tablay.setupWithViewPager(binding.instructionViewPager)
    }

    /**
     * [Function] Handling events from view model
     */
    private fun handleEvent(event: InstructionViewModel.Event) =
        when (event) {
            is InstructionViewModel.Event.OnInstructionDataUpdated -> {
                setInstructionadapter()
                viewModel.toolbarTitle.set(viewModel.data.value?.title)
                binding.instructionViewPager.adapter?.notifyDataSetChanged()
            }
            is InstructionViewModel.Event.OnNextButtonClicked -> {
                viewpagernextbuttonclick()
            }
            is InstructionViewModel.Event.OnPreviousButtonClicked -> {
                viewpagerpreviousbuttonclick()
            }
            is InstructionViewModel.Event.OnCalibrationStepsButtonClicked -> {
                calibrationstepsbuttonclick()
            }

            is InstructionViewModel.Event.OnHowToButtonClicked -> {
                howTobuttonclick()
            }
            is InstructionViewModel.Event.OnPlayVideoClicked -> {
                showVideoPopup()
            }
            is InstructionViewModel.Event.OnShowError -> {
                showerrorpopup()
            }
            is InstructionViewModel.Event.OnCalibrationButtonClicked -> {
                enableCalibrateButton(false)
                checkBluetoothWifiPermission()
            }
            is InstructionViewModel.Event.OnSkipTutorial -> {
                clickSkipTutorial()
            }
            is InstructionViewModel.Event.OnHideProgress -> bottomNavViewModel.showProgress.set(
                false
            )
            is InstructionViewModel.Event.OnShowProgress -> bottomNavViewModel.showProgress.set(true)

            is InstructionViewModel.Event.OnDownloadPdfClicked -> {
                onDownloadPdfClicked()
            }
        }

    private fun onDownloadPdfClicked() {
        val filePath = if (viewModel.instructionID.get() == 1) {
            viewModel.data.value?.instructions?.get(position)?.instructions?.get(
                binding.instructionViewPager.currentItem
            )?.tutorialPdfUrl
        } else {
            viewModel.data.value?.instructions?.get(binding.instructionViewPager.currentItem)?.tutorialPdfUrl
        }

        if (!filePath.isNullOrEmpty()) {
            if (findNavController().currentDestination?.id == R.id.destination_instruction) {
                val title = if (position == 0) {
                    "Beam Setup"
                } else {
                    "Beam Takedown"
                }
                var bundle = Bundle()
                bundle =
                    bundleOf(
                        "InstructionPdfUrl" to filePath,
                        "InstructionPdfTitle" to title
                    )
                findNavController().navigate(
                    R.id.action_instructionFragment_to_tutorialPdfFragment,
                    bundle
                )

            } else if (findNavController().currentDestination?.id == R.id.destination_instruction_calibration_fragment) {
                val title = viewModel.data.value?.title
                var bundle = Bundle()
                bundle =
                    bundleOf(
                        "InstructionPdfUrl" to filePath,
                        "InstructionPdfTitle" to title
                    )
                findNavController().navigate(
                    R.id.action_instructionFragment_to_tutorialPdfFragment,
                    bundle
                )
            }
        } else {
            Utility.getCommonAlertDialogue(
                requireContext(),
                "",
                getString(core.lib.R.string.no_pdf_available),
                "",
                getString(core.lib.R.string.str_ok),
                this,
                Utility.AlertType.NETWORK,
                Utility.Iconype.FAILED
            )
        }

    }

    /**
     * [Function] Skip Tutorial text clicked
     */
    private fun clickSkipTutorial() {
        if (findNavController().currentDestination?.id == R.id.destination_instruction
            || findNavController().currentDestination?.id == R.id.destination_instruction_calibration_fragment
        ) {
            findNavController().navigate(R.id.action_instructionFragment_to_homefragment)
        }
        Unit
    }

    /**
     * [Function] next chevron clicked
     */
    private fun viewpagernextbuttonclick() {
        binding.instructionViewPager.setCurrentItem(getItem(+1), true)
    }

    /**
     * [Function] Previous chevron clicked
     */
    private fun viewpagerpreviousbuttonclick() {
        binding.instructionViewPager.setCurrentItem(getprevItem(+1), true)
    }

    /**
     * [Function] Navigating to same fragment to show calibration screen
     */
    private fun calibrationstepsbuttonclick() {
        if (findNavController().currentDestination?.id == R.id.destination_instruction) {
            findNavController().navigate(
                R.id.action_destination_instruction_self,
                bundleOf(
                    "InstructionId" to 2,
                    "isFromHome" to viewModel?.isFromHome?.get()
                )
            )

        }

    }

    /**
     * [Function] How To text Clicked
     */
    private fun howTobuttonclick() {
        if (findNavController().currentDestination?.id == R.id.destination_instruction ||
            findNavController().currentDestination?.id == R.id.destination_instruction_calibration_fragment
        ) {
            val bundle = bundleOf(
                "isFromHome" to viewModel?.isFromHome?.get(),
                "InstructionId" to 3
            )
            findNavController().navigate(
                R.id.action_destination_instruction_to_howto_nav_graph,
                bundle
            )
        }
    }

    /**
     * [Function] How To text Clicked
     */
    private fun skipToHowToButtonclick() {
        if (findNavController().currentDestination?.id == R.id.destination_instruction ||
            findNavController().currentDestination?.id == R.id.destination_instruction_calibration_fragment
        ) {
            val bundle =
                bundleOf("isFromHome" to viewModel?.isFromHome?.get(), "InstructionId" to 3)
            findNavController().navigate(
                R.id.action_destination_instruction_to_howto_nav_graph,
                bundle
            )
        }
    }

    /**
     * [Function] Current view pager selected item
     */
    private fun getItem(i: Int): Int {
        return binding.instructionViewPager.currentItem + i
    }

    /**
     * [Function] Previous view pager selected item
     */
    private fun getprevItem(i: Int): Int {
        return binding.instructionViewPager.currentItem - i
    }

    /**
     * [Function] Watch video click
     */
    private fun showVideoPopup() {
//        val position = Common.currentSelectedTab.get()
        if (position < (viewModel.data.value?.instructions?.size ?: 0)) {
            val filePath = if (viewModel.instructionID.get() == 1) {
                viewModel.data.value?.instructions?.get(position)?.instructions?.get(
                    binding.instructionViewPager.currentItem
                )?.videoPath
            } else {
                viewModel.data.value?.instructions?.get(binding.instructionViewPager.currentItem)?.videoPath
            }

            if (viewModel.instructionID.get() == 1) { // beamsetup and takedown
                viewModel.data.value?.instructions?.get(position)?.instructions?.get(binding.instructionViewPager.currentItem)?.title
            } else {
                viewModel.data.value?.instructions?.get(binding.instructionViewPager.currentItem)?.title // calibration
            }
            if (filePath?.isNotEmpty() == true) {
                displayFullScreenVideo(filePath, "tutorial")
            } else {
                Utility.getCommonAlertDialogue(
                    requireContext(),
                    "",
                    getString(core.lib.R.string.no_video_available),
                    "",
                    getString(core.lib.R.string.str_ok),
                    this,
                    Utility.AlertType.NETWORK,
                    Utility.Iconype.FAILED
                )
            }
        }
    }

    private fun displayFullScreenVideo(
        filePath: String?,
        from: String,
    ) {
        if (findNavController().currentDestination?.id == R.id.destination_instruction
        ) {
            var titlen = if (position == 0) {
                "Beam Setup"
            } else {
                "Beam Takedown"
            }
            val bundle = bundleOf("videoPath" to filePath, "title" to titlen, "from" to from)
            val intent = Intent(requireContext(), CustomPlayerControlActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
            /* findNavController().navigate(
                 R.id.action_destination_instruction_to_nav_graph_id_video,
                 bundle
             )*/
        } else if (findNavController().currentDestination?.id == R.id.destination_instruction_calibration_fragment) {
            val title = viewModel.data.value?.title
            val bundle = bundleOf("videoPath" to filePath, "title" to title, "from" to from)
            val intent = Intent(requireContext(), CustomPlayerControlActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
            /* findNavController().navigate(
                 R.id.action_destination_instruction_calibration_fragment_to_nav_graph_id_video,
                 bundle
             )*/
        }
    }

    /**
     * [Function] Handling error response if DB call fails
     */
    private fun showerrorpopup() {
        viewModel.isErrorString.get()?.let {
            view?.rootView?.let { it1 ->
                Utility.showSnackBar(
                    it,
                    it1
                )
            }
        }
    }

    private fun projectBorderImage() {
        val bitmap = Utility.getBitmapFromDrawable("setup_pattern_border", requireContext())
        GlobalScope.launch { sendSampleImage(bitmap, false) }
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
            .setNegativeButton(
                getString(R.string.cancel),
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                })
            .setPositiveButton(
                getString(R.string.launch_camera),
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                    enableCalibrateButton(true)
                    if (findNavController().currentDestination?.id == R.id.destination_instruction
                        || findNavController().currentDestination?.id == R.id.destination_instruction_calibration_fragment
                    ) {
                        sendCalibrationPattern()
                    }
                })

        val alertCalibration = dialogBuilder.create()
        alertCalibration.setView(layout)
        alertCalibration.show()
    }

    /**
     * [Function] Setting up the toolbar by checking the previous screen
     */
    private fun setupToolbar() {
        arguments?.getBoolean("isFromHome")?.let { viewModel?.isFromHome?.set(it) }
        if (viewModel?.isFromHome?.get() ?: false) {
            bottomNavViewModel.visibility.set(false)
            toolbarViewModel.isShowActionBar.set(false)
            toolbarViewModel.isShowTransparentActionBar.set(false)
            if (viewModel.instructionID.get() == 1) {
                viewModel.toolbarTitle.set("Beam Setup & Takedown")
                toolbar.setNavigationIcon(R.drawable.ic_back_button)
                (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
                (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            } else {
                toolbar.setNavigationIcon(R.drawable.ic_back_button)
                (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
                (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                if (viewModel.instructionID.get() == 3)
                    viewModel.toolbarTitle.set(getString(R.string.Calibrationheader))
                else {
                    viewModel.toolbarTitle.set(getString(R.string.connectivity_header))
                }
            }
        } else {
            bottomNavViewModel.visibility.set(false)
            toolbarViewModel.isShowActionBar.set(false)
            toolbarViewModel.isShowTransparentActionBar.set(false)
            toolbar.setNavigationIcon(R.drawable.ic_back_button)
            (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    /**
     * [Function] OK button in alert dialog which navigates to Calibration fragment
     */
    override fun onPositiveButtonClicked(alertType: Utility.AlertType) {
        when (alertType) {
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
                enableCalibrateButton(true)
            }
            else -> {}
        }
    }

    /**
     * [Function] onNegativeButtonClicked
     */
    override fun onNegativeButtonClicked(alertType: Utility.AlertType) {
        when (alertType) {
            Utility.AlertType.BLE, Utility.AlertType.WIFI -> {
                enableCalibrateButton(true)
                skipToHowToButtonclick()
            }

            else -> {
                logger.d("alert type, except bluetooth and wifi")
            }
        }
    }

    override fun onNeutralButtonClicked(alertType: Utility.AlertType) {
        TODO("Not yet implemented")
    }

    override fun onResume() {
        super.onResume()
        viewModel.isWatchVideoClicked.set(false)
        toolbar.setNavigationIcon(R.drawable.ic_back_button)

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        context?.let { it1 ->
            ContextCompat.checkSelfPermission(
                it1, it
            )
        } == PackageManager.PERMISSION_GRANTED
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

    private fun enableCalibrateButton(enable: Boolean) {
        binding.instructionViewPager?.getChildAt(1)?.button_calibrate?.isClickable = enable
        binding.instructionViewPager?.getChildAt(1)?.button_calibrate?.isFocusable = enable
        binding.instructionViewPager?.getChildAt(1)?.button_calibrate?.isEnabled = enable
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
                    baseViewModel.activeSocketConnection.set(true)
                    projectBorderImage()
                } else {
                    withContext(Dispatchers.Main) { showProgress(false) }
                    showConnectivityPopup()
                }
            }
        }
    }

    private fun navigateToCalibration() {
        //alert.dismiss()
        findNavController().navigate(
            R.id.action_destination_instruction_to_calibration_nav_graph,
            bundleOf(
                "isFromHome" to viewModel?.isFromHome?.get(),
                "isFromPatternDescription" to false
            )
        )
    }

    private fun showProgress(toShow: Boolean) {
        if (toShow) {
            val layout =
                activity?.layoutInflater?.inflate(R.layout.progress_dialog, null)

            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder
                .setCancelable(false)
            alert = dialogBuilder.create()
            alert?.setView(layout)
            alert?.show()
        } else {
            alert?.dismiss()
            alert = null
        }
    }

    private fun sendCalibrationPattern() {
        showProgress(true)
        logger.d("TRACE_ Projection : sendCalibrationPattern " + Calendar.getInstance().timeInMillis)
        val bitmap =
            Utility.getBitmapFromDrawable("calibration_transformed", requireContext())
        GlobalScope.launch {
            sendSampleImage(
                bitmap, true
            )
        }

        /*   viewModel.disposable += Observable.fromCallable {
               performTransform(
                   bitmap,
                   context?.applicationContext,
                   Utility.unityTransParmsString,
                   false
               )
           }
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribeBy { handleResult(it) }*/
    }

    private suspend fun sendSampleImage(
        transformedBitmap: Bitmap,
        isNavigateToCalibration: Boolean,
    ) {
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
                    transformedBitmap.compress(Bitmap.CompressFormat.PNG, 0, workspaceStream)
                    val bitmapdata = workspaceStream.toByteArray()
                    transformedBitmap.recycle()
                    val dataOutputStream: DataOutputStream =
                        DataOutputStream(soc.getOutputStream())
                    dataOutputStream.write(bitmapdata)
                    dataOutputStream.close()
                    withContext(Dispatchers.Main) {
                        if (isNavigateToCalibration)
                            navigateToCalibration()
                        else
                            showcalibrationbuttonclicked()
                        showProgress(false)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Projector Connection failed. Try again!!",
                            Toast.LENGTH_SHORT
                        ).show()
                        showProgress(false)
                    }
                }
            } catch (e: Exception) {
                logger.d("Exception " + e.message)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Projector Connection failed. Try again!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    showProgress(false)
                }
            } finally {
                soc?.close()
                logger.d("TRACE_ Projection : send Image Finish" + Calendar.getInstance().timeInMillis)
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
                logger.d("${ConnectivityUtils.TAG}, Exception")
            } finally {
                soc?.close()
            }
        }
        return withContext(Dispatchers.Main) { isConnected }
    }

    private fun showConnectivityPopup() {
        val intent = Intent(requireContext(), ConnectivityActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivityForResult(
            intent,
            REQUEST_ACTIVITY_RESULT_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        logger.d("On Activity Result")
        if (requestCode == REQUEST_ACTIVITY_RESULT_CODE) {
            when {
                data?.data.toString().equals("success") -> {
                    baseViewModel.activeSocketConnection.set(true)
                    projectBorderImage()
                }
                data?.data.toString().equals(ConnectivityUtils.SKIP) -> {
                    enableCalibrateButton(true)
                    howTobuttonclick()
                }
            }
        } else if (requestCode == REQUEST_ENABLE_BT) {
            val mBluetoothAdapter =
                BluetoothAdapter.getDefaultAdapter()
            if (mBluetoothAdapter?.isEnabled == false) {
                enableCalibrateButton(true)
                skipToHowToButtonclick()
            } else {
                if (!Utility.getWifistatus(requireContext())) {
                    showWifiDialogue()
                } else {
                    showConnectivityPopup()
                }
            }
        }
    }

    companion object {
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

    }

    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType,
    ) {
    }

}


