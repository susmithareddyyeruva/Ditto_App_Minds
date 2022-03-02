package com.ditto.mylibrary.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ditto.connectivity.ConnectivityActivity
import com.ditto.connectivity.ConnectivityUtils
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.domain.model.MannequinDataDomain
import com.ditto.mylibrary.domain.model.ProdDomain
import com.ditto.mylibrary.ui.adapter.CustomSpinnerAdapter
import com.ditto.mylibrary.ui.databinding.PatternDescriptionFragmentBinding
import com.ditto.workspace.ui.util.SvgBitmapDecoder
import com.joann.fabrictracetransform.transform.TransformErrorCode
import com.joann.fabrictracetransform.transform.performTransform
import core.ERROR_FETCH
import core.PDF_DOWNLOAD_URL
import core.appstate.AppState
import core.data.model.SoftwareUpdateResult
import core.lib.BuildConfig
import core.network.NetworkUtility
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import core.ui.rxbus.RxBus
import core.ui.rxbus.RxBusEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.pattern_description_fragment.*
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.net.Socket
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class PatternDescriptionFragment : BaseFragment(), Utility.CallbackDialogListener,
    Utility.CustomCallbackDialogListener {

    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(PatternDescriptionFragment::class.java.simpleName)
    }
    var versionDisposable: CompositeDisposable? = null
    private val viewModel: PatternDescriptionViewModel by ViewModelDelegate()
    lateinit var binding: PatternDescriptionFragmentBinding
    private lateinit var alert: AlertDialog
    private lateinit var outputDirectory: File
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    private val CONNNECTION_FAILED = "Projector Connection failed. Try again!!" // Compliant
    var versionResult: SoftwareUpdateResult? = null
    private lateinit var job: Job
    // var clickedProduct: ProdDomain? = null

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
        context?.getString(R.string.pattern_details)?.let {
            (activity as BottomNavigationActivity).setToolbarTitle(
                it
            )
        }
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbarPatterndesc)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_patterndesc.setNavigationIcon(R.drawable.ic_back_button)
        //baseViewModel.activeSocketConnection.set(false)


        if (arguments?.getString("ISFROM").equals("DEEPLINK")) {
            (activity as BottomNavigationActivity).setEmaildesc()
            logger.d("FROM DEEPLINK IN PATTERN DESCRIPTION")
            viewModel.isFromDeepLinking.set(true)
            arguments?.getString("clickedTailornovaID").toString()
                ?.let { viewModel.clickedTailornovaID.set(it) }
            arguments?.getString("clickedOrderNumber").toString()
                ?.let { viewModel.clickedOrderNumber.set(it) }
            arguments?.getString("mannequinId").toString()
                ?.let { viewModel.mannequinId.set(it) }
            bottomNavViewModel.showProgress.set(true)
            if (NetworkUtility.isNetworkAvailable(context)) {
                viewModel.fetchPattern()
            } else {
                viewModel.fetchOfflinePatternDetails()
            }
            viewModel.isFromDeeplink.set(true)
        } else {
            viewModel.isFromDeeplink.set(false)
            if (viewModel.data.value == null) {
                arguments?.getString("clickedTailornovaID").toString()
                    ?.let { viewModel.clickedTailornovaID.set(it) }
                arguments?.getString("clickedOrderNumber").toString()
                    ?.let { viewModel.clickedOrderNumber.set(it) }
                viewModel.clickedProduct = arguments?.get("product") as ProdDomain?
                logger.d("12345, received is ${viewModel.clickedProduct.toString()}")
            } else {
                setPatternImage()
            }

            if (viewModel.clickedProduct != null) {
                if (NetworkUtility.isNetworkAvailable(context)) {
                    if (AppState.getIsLogged()) {
                        binding.textMannequinName.visibility = View.GONE
                        if (viewModel.clickedProduct?.patternType.equals("Trial", true)) {
                            viewModel.fetchOfflinePatternDetails()
                        } else {  //Online Scenario
                            if (viewModel.clickedProduct!!.mannequin.isNullOrEmpty()) {
                                viewModel.mannequinId.set(viewModel.clickedProduct!!.purchasedSizeId)  //setting purchase ID as mannequin id
                                if (viewModel.clickedProduct!!.purchasedSizeId.isNullOrEmpty()) {
                                    viewModel.mannequinId.set(viewModel.clickedProduct!!.selectedMannequinId) //setting selectedMannequin Id as mannequin id
                                }
                                if (viewModel.mannequinId.get()
                                        ?.isNotEmpty() == true
                                ) {//API cal  will happen only mannequin id is not empty
                                    bottomNavViewModel.showProgress.set(true)
                                    viewModel.fetchPattern()// on sucess inserting tailornova details inside internal DB
                                }
                            } else {
                                setSpinner()// Setting Dropdown with Mannequin ID
                            }


                        }
                    } else {
                        viewModel.fetchOfflinePatternDetails()
                    }
                } else {
                    viewModel.fetchOfflinePatternDetails()
                }

            }
            setUpUiBasedOnLoggedIn()
            // fetchPatternDetails()   //Fetching Pattern Details using design id

        }
        setUIEvents()
        outputDirectory = Utility.getOutputDirectory(requireContext())



        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {


                // It returns the clicked item.
                val clickedItem: MannequinDataDomain =
                    parent.getItemAtPosition(position) as MannequinDataDomain
                val id: String = clickedItem.mannequinId
                val name = clickedItem.mannequinName
                viewModel.mannequinId.set(id)
                viewModel.mannequinName.set(name)
                bottomNavViewModel.showProgress.set(true)
                fetchPatternDetails()//Fetching pattern Details using selected mannequin ID
                logger.d("ITEM SELECTED********, MANNEQUIN ID:  + ${viewModel.mannequinId.get()}")
                logger.d("ITEM SELECTED********, MANNEQUIN NAME: + ${viewModel.mannequinName.get()}")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setSpinner() {
        viewModel.isShowSpinner.set(true)
        // we pass our item list and context to our Adapter.
        viewModel.mannequinId?.set("")
        viewModel.mannequinName?.set("")
        viewModel.mannequinList?.clear()
        viewModel.mannequinList?.add(MannequinDataDomain("", "Add Customization"))
        viewModel.clickedProduct!!.mannequin?.forEach {
            viewModel.mannequinList?.add(
                MannequinDataDomain(
                    it.mannequinId,
                    it.mannequinName
                )
            )
        }
        val adapter =
            viewModel.mannequinList?.let {
                CustomSpinnerAdapter(
                    requireContext(),
                    it
                )
            }
        binding.spinner.adapter = adapter
        binding.spinner.setSelection(0, true)
        /*     try {
                 val popup = Spinner::class.java.getDeclaredField("mPopup")
                 popup.isAccessible = true

                 // Get private mPopup member variable and try cast to ListPopupWindow
                 val popupWindow = popup[binding.spinner] as ListPopupWindow

                 // Set popupWindow height to 500px
                 popupWindow.height = 106
             } catch (e: NoClassDefFoundError) {
                 // silently fail...
             } catch (e: ClassCastException) {
             } catch (e: NoSuchFieldException) {
             } catch (e: IllegalAccessException) {
             }*/
    }

    private fun fetchPatternDetails() {
        if (NetworkUtility.isNetworkAvailable(context)) {
            if (AppState.getIsLogged()) {
                if (viewModel.clickedProduct?.patternType.equals("Trial", true)) {
                    viewModel.fetchOfflinePatternDetails()
                } else {
                    viewModel.fetchPattern()// on sucess inserting tailornova details inside internal DB
                }
            } else {
                viewModel.fetchOfflinePatternDetails()
            }
        } else {
            viewModel.fetchOfflinePatternDetails()
        }


        outputDirectory = Utility.getOutputDirectory(requireContext())
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 111
        private const val REQUEST_ACTIVITY_RESULT_CODE = 121
        private val REQUIRED_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(Manifest.permission.BLUETOOTH)
        }
        private const val REQUEST_CODE_PERMISSIONS_DOWNLOAD = 131
        private val REQUIRED_PERMISSIONS_DOWNLOAD =
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
    }

    private fun dowloadPermissonGranted() = REQUIRED_PERMISSIONS_DOWNLOAD.all {
        context?.let { it1 ->
            ContextCompat.checkSelfPermission(
                it1, it
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    private fun setUpUiBasedOnLoggedIn() {
        if (bottomNavViewModel.isGuestBase.get()) {
            setUpUiForGuestUser()
        } else {
            setUIForLoggedInUser()
        }
        viewModel.isDataReceived.set(true)
    }

    private fun setUpUiForGuestUser() {
        setData()
        setVisibilityForViews("WORKSPACE", false, false, false, false, false, true)
        setPatternImage()

    }

    private fun setVisibilityForViews(
        buttonText: String,
        isSubscriptionExpired: Boolean,
        showActiveText: Boolean,
        showPurchasedText: Boolean,
        showLine: Boolean,
        showResumeButton: Boolean,
        showWorkspaceOrRenewSubscriptionButton: Boolean
    ) {
        viewModel.resumeOrSubscription.set(buttonText)
        viewModel.isSubscriptionExpired.set(isSubscriptionExpired)
        viewModel.showActive.set(showActiveText)
        viewModel.showPurchased.set(showPurchasedText)
        viewModel.showLine.set(showLine)
        viewModel.showResumButton.set(showResumeButton)
        viewModel.showWorkspaceOrRenewSubscriptionButton.set(showWorkspaceOrRenewSubscriptionButton)
        if (showPurchasedText && !showActiveText) {
            binding.purchasedPattern.setPadding(0, 0, 0, 0)
        }
    }

    private fun setUIForLoggedInUser() {
        if (viewModel.isFromDeepLinking.get()) {
            viewModel.patternName.set(viewModel.data.value?.patternName)
            viewModel.patternDescription.set(viewModel.data.value?.description)
            viewModel.prodSize.set(viewModel.data.value?.size)
            Glide.with(requireContext())
                .load(viewModel.data.value?.patternDescriptionImageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.imagePatternDesc)
            // need to check for type
            setVisibilityForViews(
                "WORKSPACE",
                false,
                false,
                false,
                false,
                false,
                true
            )
        } else {
            setData()
            if (viewModel.clickedProduct?.status.equals("Expired", true)) {
                setVisibilityForViews(
                    "RENEW SUBSCRIPTION",
                    true,
                    false,
                    false,
                    false,
                    false,
                    true
                )
            } else if (viewModel.clickedProduct?.status.equals("New", true)) {
                setVisibilityForViews(
                    "WORKSPACE",
                    false,
                    false,
                    false,
                    false,
                    false,
                    true
                )

            } else {
                setVisibilityForViews("RESUME", false, true, false, false, true, false)
            }
            setPatternImage()
        }


    }

    private fun setData() {
        viewModel.patternName.set(viewModel.clickedProduct?.prodName)
        if (viewModel.clickedProduct?.patternType.equals("Trial")) {
            viewModel.prodSize.set(
                viewModel.data?.value?.size ?: ""
            ) //  milli second null CHANGE LOGIC
        } else {
            viewModel.prodSize.set(viewModel.clickedProduct?.prodSize ?: "")
        }

        if (viewModel.clickedProduct?.tailornovaDesignName.isNullOrEmpty()) {
            viewModel.tailornovaDesignpatternName.set(viewModel.clickedProduct?.prodName)
        } else {
            viewModel.tailornovaDesignpatternName.set(viewModel.clickedProduct?.tailornovaDesignName)
        }
        //viewModel.patternDescription.set(clickedProduct?.description)
        viewModel.patternDescription.set(
            viewModel.clickedProduct?.description ?: "Some description"
        )
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
        logger.d("TRACE_ Projection : performTransform  Start " + Calendar.getInstance().timeInMillis)
        showProgress(true)
        val bitmap =
            Utility.getBitmapFromDrawable("calibration_transformed", requireContext())

        GlobalScope.launch {
            sendSampleImage(
                bitmap,
                false
            )
        }
        /* viewModel.disposable += Observable.fromCallable {
             performTransform(
                 bitmap,
                 context?.applicationContext,
                 Utility.unityTransParmsString,
                 false
             )
         }
             .subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribeBy { handleResult(it, false) }*/
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
        logger.d("TRACE_ Projection : performTransform  finish " + Calendar.getInstance().timeInMillis)
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
        logger.d("TRACE_ Projection : sendSampleImage  Start " + Calendar.getInstance().timeInMillis)
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
                logger.d("TRACE_ Projection : sendSampleImage  Finish " + Calendar.getInstance().timeInMillis)
            }
        }
    }

    private fun setPrepareDownloadList(map: HashMap<String, String>) {
        /*  val filterd=map.filter {
              it.value != "null"&&!it.value.isNullOrEmpty()
          } as HashMap<String,String>*/
        viewModel.prepareDowloadList(viewModel.imageFilesToDownload(map))
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

                WSButtonClick()

            }
            is PatternDescriptionViewModel.Event.OnDataUpdated -> {
                bottomNavViewModel.showProgress.set(false)
                if (viewModel.clickedProduct?.patternType.toString().equals("Trial", true)) {
                    binding.textMannequinName.visibility = View.GONE
                }/*else{
                    *//*if (viewModel.mannequinName.get()?.isNotEmpty() == true) {
                        binding.textMannequinName.visibility = View.VISIBLE
                    }*//*
                }*/
                setUpUiBasedOnLoggedIn()
            }

            is PatternDescriptionViewModel.Event.OnSubscriptionClicked -> {
                logger.d("onSubscriptionClicked")
                Utility.redirectToExternalBrowser(
                    requireContext(), BuildConfig.SUBSCRIPTION_URL
                    //"https://development.dittopatterns.com/on/demandware.store/Sites-ditto-Site/default/Recurly-GetSubscriptionPlan"
                )

            }
            is PatternDescriptionViewModel.Event.OnInstructionsButtonClicked -> {
                /**
                 * Allowing user to enter into instruction if mannequinId is present
                 */
                if (viewModel.mannequinId?.get()
                        ?.isEmpty() == true && !(viewModel.clickedProduct?.patternType.toString()
                        .equals("Trial", true))
                ) {
                    /**
                     * Restricting user to enter into Instructions without selecting any customization if Network is Connected
                     */
                    showAlert(
                        getString(R.string.please_selecte_mannequinid),
                        Utility.AlertType.DEFAULT
                    )
                } else {
                    if ((findNavController().currentDestination?.id == R.id.patternDescriptionFragment)
                        || (findNavController().currentDestination?.id == R.id.patternDescriptionFragmentFromHome)
                    ) {
                        PDF_DOWNLOAD_URL = viewModel.data.value?.instructionUrl
                        var bundle = Bundle()
                        if (viewModel.clickedProduct?.tailornovaDesignName.isNullOrEmpty()) {
                            bundle =
                                bundleOf(
                                    "PatternName" to viewModel.clickedProduct?.prodName,
                                    "tailornovaDesignName" to viewModel.clickedProduct?.prodName
                                )
                        } else {
                            bundle =
                                bundleOf(
                                    "PatternName" to viewModel.clickedProduct?.prodName,
                                    "tailornovaDesignName" to viewModel.clickedProduct?.tailornovaDesignName
                                )

                        }
                        findNavController().navigate(
                            R.id.action_patternDescriptionFragment_to_pattern_instructions_Fragment,
                            bundle
                        )
                    } else
                        Unit
                }
            }
            PatternDescriptionViewModel.Event.OnDownloadComplete -> TODO()
            PatternDescriptionViewModel.Event.OnDataloadFailed -> showDataFailedAlert()
            PatternDescriptionViewModel.Event.OnImageDownloadComplete -> {

                if (viewModel.temp.size == viewModel.imagesToDownload.size) {
                    bottomNavViewModel.showProgress.set(false)
                    //Log.d("Download", "ENDED >>>>>>>>>>> ")
                    logger.d("Download123,  ENDED > > > > > > > > > > > OnImageDownloadComplete in if ")
                    bottomNavViewModel.showWSProgress.set(false)
                    if (NetworkUtility.isNetworkAvailable(context)) {
                        viewModel.deletePattern()
                    } else {
                        checkSocketConnectionBeforeWorkspace()
                    }

                } else {
                    logger.d("Download123, ENDED >>>>>>>>>>> OnImageDownloadComplete in else ")
                    bottomNavViewModel.showProgress.set(false)
                    bottomNavViewModel.showWSProgress.set(false)
                    Utility.getCommonAlertDialogue(
                        requireContext(),
                        resources.getString(com.ditto.workspace.ui.R.string.download_failed),
                        resources.getString(com.ditto.workspace.ui.R.string.download_failed_message),
                        resources.getString(com.ditto.workspace.ui.R.string.str_retry),
                        resources.getString(com.ditto.workspace.ui.R.string.ok),
                        this,
                        Utility.AlertType.DOWNLOADFAILED,
                        Utility.Iconype.NONE
                    )
                }
            }
            PatternDescriptionViewModel.Event.OnNoNetworkToDownloadImage -> {
                bottomNavViewModel.showProgress.set(false)
                bottomNavViewModel.showWSProgress.set(false)
                Utility.getCommonAlertDialogue(
                    requireContext(),
                    resources.getString(com.ditto.workspace.ui.R.string.download_failed),
                    resources.getString(com.ditto.workspace.ui.R.string.guest_user_ws_enter_message),
                    resources.getString(com.ditto.workspace.ui.R.string.empty_string),
                    resources.getString(com.ditto.workspace.ui.R.string.ok),
                    this,
                    Utility.AlertType.DEFAULT,
                    Utility.Iconype.NONE
                )
            }
            PatternDescriptionViewModel.Event.OnShowMannequinData -> {
                if (viewModel.mannequinName.get()?.isNotEmpty() == true) {
                    binding.textMannequinName.visibility = View.VISIBLE
                    viewModel.isShowSpinner.set(false)
                } else {

                }
            }

            PatternDescriptionViewModel.Event.OnDeletePatternFolder -> {
                if (AppState.getIsLogged()) {
                    deleteFolder(viewModel.patternsInDB)
                } else {
                }
            }

            PatternDescriptionViewModel.Event.OnMannequinNameEmpty -> {
                showAlert(
                    getString(R.string.please_selecte_mannequinid),
                    Utility.AlertType.DEFAULT
                )
            }

            PatternDescriptionViewModel.Event.OnGuestUSerWSClick -> {
                checkSocketConnectionBeforeWorkspace()
            }
            else -> {}
        }

    private fun WSButtonClick() {
        if (viewModel.clickedProduct?.patternType.toString().equals("Trial", true)) {
            binding.textWatchvideo2.isEnabled = false
            if ((findNavController().currentDestination?.id == R.id.patternDescriptionFragment)
                || (findNavController().currentDestination?.id == R.id.patternDescriptionFragmentFromHome)
            ) {
                //checkBluetoothWifiPermission()
                //forwardtoWorkspace()
                /* // delete folder and PDF flow starting here
                        viewModel.fetchDemoPatternList()*/
                val map = getPatternPieceListTailornova()
                //if (context?.let { core.network.NetworkUtility.isNetworkAvailable(it) }!!) {
                if (dowloadPermissonGranted()) {
                    Log.d("prepare>>>>>", "OnWorkspaceButtonClicked if")
                    bottomNavViewModel.showProgress.set(true)
                    bottomNavViewModel.showWSProgress.set(true)
                    if (!::job.isInitialized || !job.isActive) {
                        job = GlobalScope.launch {
                            setPrepareDownloadList(map)
                        }
                    } else {

                    }
                } else {
                    requestPermissions(
                        REQUIRED_PERMISSIONS_DOWNLOAD,
                        REQUEST_CODE_PERMISSIONS_DOWNLOAD
                    )
                }

            } else {
                logger.d("OnClick Workspace failed")
            }
        } else {//Pattern TYPE not  Trial and Network Connected
            /**
             * Allowing user to enter into workspace  which is not  trial Pattern if Network is Connected
             */

            if (viewModel.mannequinId?.get()?.isNotEmpty() == true) {
                binding.textWatchvideo2.isEnabled = false
                if ((findNavController().currentDestination?.id == R.id.patternDescriptionFragment)
                    || (findNavController().currentDestination?.id == R.id.patternDescriptionFragmentFromHome)
                ) {
                    //checkBluetoothWifiPermission()
                    //forwardtoWorkspace()
                    /*viewModel.fetchDemoPatternList()*/
                    val map = getPatternPieceListTailornova()
                    //if (context?.let { core.network.NetworkUtility.isNetworkAvailable(it) }!!) {
                    if (dowloadPermissonGranted()) {
                        Log.d("prepare>>>>>", "OnWorkspaceButtonClicked else")
                        bottomNavViewModel.showProgress.set(true)
                        bottomNavViewModel.showWSProgress.set(true)
                        if (!::job.isInitialized || !job.isActive) {
                            job = GlobalScope.launch {
                                setPrepareDownloadList(map)
                            }
                        } else {

                        }

                    } else {
                        requestPermissions(
                            REQUIRED_PERMISSIONS_DOWNLOAD,
                            REQUEST_CODE_PERMISSIONS_DOWNLOAD
                        )
                    }

                } else {
                    logger.d("OnClick Workspace failed")
                }
            } else {
                /**
                 * Restricting user to enter into workspace without selecting any customization if Network is Connected
                 */
                showAlert(
                    getString(R.string.please_selecte_mannequinid),
                    Utility.AlertType.DEFAULT
                )

            }

        }
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
        if (NetworkUtility.isNetworkAvailable(context)) {
            downloadImage(viewModel.clickedProduct?.image, viewModel.patternName.get())
        }
        setImageFromSvgPngDrawable(
            viewModel.patternName.get(),
            if (NetworkUtility.isNetworkAvailable(context) || viewModel.clickedProduct?.patternType?.toUpperCase()
                    .equals("TRIAL")
            ) viewModel.clickedProduct?.image else viewModel.patternName.get(),
            binding.imagePatternDesc.context,
            binding.imagePatternDesc
        )
    }

    private fun downloadImage(imageUrl: String?, patternName: String?) {
        runBlocking {
            imageUrl?.let {
                patternName?.let { fileName ->
                    viewModel.downloadEachPatternPiece(
                        imageUrl = it,
                        filename = fileName,
                        patternFolderName = patternName
                    )
                }
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?
    ) {
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
        logger.d("PatternSCreen, onResume-PatternDescription")
        if (viewModel.disposable.size() == 0) {
            setUIEvents()
        }
        listenVersionEvents()
    }

    private fun listenVersionEvents() {
        versionDisposable = CompositeDisposable()
        versionDisposable?.plusAssign(
            RxBus.listen(RxBusEvent.CheckVersion::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.isCheckVersion) {
                        !it.isCheckVersion
                        bottomNavViewModel.showProgress.set(true)
                        viewModel.versionCheck()
                    }
                })
        versionDisposable?.plusAssign(
            RxBus.listen(RxBusEvent.VersionReceived::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    bottomNavViewModel.showProgress.set(false)
                    if (it.versionReceived.response.version != null) {
                        versionResult = it.versionReceived
                        showVersionPopup()
                    } else {
                        showAlert(
                            ERROR_FETCH,
                            Utility.AlertType.DEFAULT
                        )
                    }

                })

        versionDisposable?.plusAssign(
            RxBus.listen(RxBusEvent.VersionErrorReceived::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    bottomNavViewModel.showProgress.set(false)
                    showAlert(it.versionerrorReceived, Utility.AlertType.NETWORK)
                })
    }

    override fun onPause() {
        toolbarViewModel.isShowTransparentActionBar.set(false)
        super.onPause()
        versionDisposable?.clear()
        versionDisposable?.dispose()
    }

    override fun onStop() {
        super.onStop()
        viewModel.disposable.clear()
    }

    override fun onDestroy() {
        Log.d("PatternSCreen", "onDestroy-PatternDescription")
        bottomNavViewModel.showWSProgress.set(false)
        super.onDestroy()
    }

    private fun showVersionPopup() {
        var negativeText = versionResult?.response?.cancel!!
        var positiveText = versionResult?.response?.confirm!!
        var status = Utility.Iconype.WARNING
        if (versionResult?.response?.versionUpdate == false) {
            negativeText = ""
            positiveText = "OK"
            status = Utility.Iconype.SUCCESS
        }
        Utility.getCommonAlertDialogue(
            requireContext(),
            versionResult?.response?.title!!,
            versionResult?.response?.body!!,
            negativeText,
            positiveText,
            this,
            Utility.AlertType.SOFTWARE_UPDATE,
            status
        )
    }

    private fun enterWorkspace() {
        logger.d(
            "Download123, ENDED >>>>>>>>>>> enterWorkspace in if ${viewModel.clickedProduct?.prodName}"
        )

        if (baseViewModel.activeSocketConnection.get()) {
            GlobalScope.launch {
                Utility.sendDittoImage(
                    requireContext(),
                    "solid_black"
                )
            }
        }

        var bundle = bundleOf()

        if (viewModel.clickedProduct?.tailornovaDesignName.isNullOrEmpty()) {
            bundle =
                bundleOf(
                    "PatternName" to viewModel.patternName.get(),
                    "clickedTailornovaID" to viewModel.clickedTailornovaID.get(),
                    "clickedOrderNumber" to viewModel.clickedOrderNumber.get(),
                    "mannequinId" to viewModel.mannequinId.get(),
                    "tailornovaDesignName" to viewModel.patternName.get()
                )
        } else {
            bundle =
                bundleOf(
                    "PatternName" to viewModel.clickedProduct?.prodName,
                    "clickedTailornovaID" to viewModel.clickedTailornovaID.get(),
                    "clickedOrderNumber" to viewModel.clickedOrderNumber.get(),
                    "mannequinId" to viewModel.mannequinId.get(),
                    "tailornovaDesignName" to viewModel.clickedProduct?.tailornovaDesignName
                )
        }

        if ((findNavController().currentDestination?.id == R.id.patternDescriptionFragment) || (findNavController().currentDestination?.id == R.id.patternDescriptionFragmentFromHome)) {
            findNavController().navigate(
                R.id.action_patternDescriptionFragment_to_WorkspaceFragment,
                bundle
            )
        }
    }

    //download the pattern pieces
    fun getPatternPieceListTailornova(): HashMap<String, String> {
        var hashMap: HashMap<String, String> = HashMap<String, String>()
        hashMap[if(viewModel.isFromDeepLinking.get()) viewModel.patternName.get().toString() else viewModel.data.value?.thumbnailImageName.toString()] =
            viewModel.data.value?.thumbnailImageUrl.toString()
        hashMap[viewModel.data.value?.thumbnailImageName.toString()] =
            viewModel.data.value?.thumbnailImageUrl.toString()
        for (patternItem in viewModel.data.value?.selvages ?: emptyList()) {
            hashMap[patternItem.imageName.toString()] = patternItem.imageUrl ?: ""
        }
        for (patternItem in viewModel.data.value?.patternPieces ?: emptyList()) {
            hashMap[patternItem.thumbnailImageName.toString()] =
                patternItem.thumbnailImageUrl.toString()
            hashMap[patternItem.imageName.toString()] = patternItem.imageUrl.toString()
            for (splicedImage in patternItem.splicedImages ?: emptyList()) {
                hashMap[splicedImage.imageName.toString()] =
                    splicedImage.imageUrl.toString()
                hashMap[splicedImage.mapImageName.toString()] =
                    splicedImage.mapImageUrl.toString()
            }
        }
        return hashMap
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
                logger.d("alertType,  DEFAULT ")
            }
            Utility.AlertType.PERMISSION_DENIED -> {
                Utility.navigateToAppSettings(requireContext())
            }
        }
    }

    private suspend fun projectBorderImage() {
        withContext(Dispatchers.IO) {
            val bitmap =
                Utility.getBitmapFromDrawable("setup_pattern_border", requireContext())

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
            activity?.layoutInflater?.inflate(
                R.layout.calibration_camera_alert_ws,
                null
            )

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
                    sendCalibrationPattern()
                })

        val alertCalibration = dialogBuilder.create()
        alertCalibration.setView(layout)
        alertCalibration.show()
    }

    override fun onNegativeButtonClicked(
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
                logger.d("alertType, DEFAULT")
            }
        }
    }

    override fun onNeutralButtonClicked(
        alertType: Utility.AlertType
    ) {
        // to clear out workspace projection
        if (baseViewModel.activeSocketConnection.get()) {
            GlobalScope.launch {
                Utility.sendDittoImage(
                    requireActivity(),
                    "solid_black"
                )
            }
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
                    GlobalScope.launch {
                        Utility.sendDittoImage(
                            requireActivity(),
                            "solid_black"
                        )
                    }
                }
                enterWorkspace()
            }
            Utility.AlertType.DEFAULT -> {
                logger.d("alertType, DEFAULT")
            }

            /*Utility.AlertType.DOWNLOADFAILED -> {
                bottomNavViewModel.showProgress.set(false)
                checkSocketConnectionBeforeWorkspace()
            }*/


            Utility.AlertType.SOFTWARE_UPDATE -> {
                if (versionResult?.response?.versionUpdate == true) {
                    val packageName = "com.joann.ditto"
                    try {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$packageName")
                            )
                        )
                    } catch (e: ActivityNotFoundException) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                            )
                        )
                    }
                }
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
                logger.d("alertType, DEFAULT")
            }
            alertType == Utility.AlertType.SOFTWARE_UPDATE -> {
                if (versionResult?.response?.forceUpdate == true) {
                    requireActivity().finishAffinity()
                }
            }
            alertType == Utility.AlertType.DOWNLOADFAILED -> {
                val map = getPatternPieceListTailornova()
                if (!::job.isInitialized || !job.isActive) {
                    job = GlobalScope.launch {
                        setPrepareDownloadList(map)
                    }
                }

            }
        }
    }

    private fun showDataFailedAlert() {
        var errorMsg: String? = null
        if (viewModel.tailornovaApiError?.contains("In progress", true) == true) {
            errorMsg = getString(R.string.str_processing_error)
        } else {
            errorMsg =
                "\n" + getString(R.string.str_internal_server_error) + "\n\n" + viewModel.tailornovaApiError
        }
        bottomNavViewModel.showProgress.set(false)
        bottomNavViewModel.showWSProgress.set(false)
        if (activity != null && context != null) {
            Utility.getCommonAlertDialogue(
                requireContext(),
                "",
                errorMsg ?: "",
                "",
                getString(R.string.str_ok),
                this,
                Utility.AlertType.NETWORK,
                Utility.Iconype.FAILED
            )
        }
    }

    /**
     * [Function] Call back when user allow/deny the permission
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (dowloadPermissonGranted() && requestCode == REQUEST_CODE_PERMISSIONS_DOWNLOAD) {
            logger.d("onReqPermissionsResult, permission granted")
            val map = getPatternPieceListTailornova()

            if (core.network.NetworkUtility.isNetworkAvailable(requireContext())) {
                bottomNavViewModel.showProgress.set(true)
                logger.d("prepare>>>>>, onRequestPermissionsResult")
                bottomNavViewModel.showWSProgress.set(true)
                Log.d("prepare>>>>>", "onRequestPermissionsResult")
                if (!::job.isInitialized || !job.isActive) {
                    job = GlobalScope.launch {
                        setPrepareDownloadList(viewModel.imageFilesToDownload(map))
                    }
                }


            } else {
                Utility.getCommonAlertDialogue(
                    requireContext(),
                    "",
                    getString(R.string.no_internet_available),
                    "",
                    getString(R.string.str_ok),
                    this,
                    Utility.AlertType.NETWORK,
                    Utility.Iconype.FAILED
                )
            }
        } else {
            //checkSocketConnectionBeforeWorkspace()
            // todo need dialog to ask for permission
            Utility.getAlertDialogue(
                requireContext(),
                getString(R.string.permissions_required),
                getString(R.string.storage_permissions),
                getString(R.string.cancel),
                getString(R.string.go_to_settings),
                this,
                Utility.AlertType.PERMISSION_DENIED
            )
            //Toast.makeText(requireContext(), "Denied", Toast.LENGTH_SHORT)
            logger.d("onReqPermissionsResult, permission denied")
        }

    }

    private fun showAlert(
        versionerrorReceived: String,
        alertType: Utility.AlertType
    ) {
        Utility.getCommonAlertDialogue(
            requireContext(),
            "",
            versionerrorReceived,
            "",
            getString(com.ditto.menuitems_ui.R.string.str_ok),
            this,
            alertType,
            Utility.Iconype.FAILED
        )
    }


    private fun deleteFolder(patterns: MutableList<ProdDomain>?) {
        /*val directory = File(
            Environment.getExternalStorageDirectory()
                .toString() + "/Ditto"
        )*/


        val contextWrapper = ContextWrapper(context)
        val directory = contextWrapper.getDir("Ditto", Context.MODE_PRIVATE)

        if (directory.exists()) {
            val folders = directory.listFiles()
            val listOfCommonFiles: ArrayList<File> = ArrayList(emptyList())
            if (patterns != null) {
                for (file in folders) {
                    var fileName = file.name
                    if (fileName.contains(".pdf")) {
                        fileName = getNameWithoutExtension(fileName)
                    }
                    patterns.forEach {
                        if (it.prodName.toString()
                                .replace("[^A-Za-z0-9 ]".toRegex(), "") == fileName.toString()
                                .replace("[^A-Za-z0-9 ]".toRegex(), "")
                        ) {
                            listOfCommonFiles.add(file)
                        }
                    }
                }

            }

            val filesToDelete = folders.toSet().minus(listOfCommonFiles.toSet())
            logger.d("deleteFolderFun12, File to delete  >> Name: ${filesToDelete.size}")
            for (file in filesToDelete) {
                val d = deleteDirectory(file)
                logger.d("deleteFolderFun, RESULT: ${file.name} >>> $d")
            }
        }
        deletePDF(patterns)
    }


    private fun deletePDF(patterns: MutableList<ProdDomain>?) {
        val directory = if (Build.VERSION.SDK_INT >= 30) {
            File(
                context?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                    .toString() + "/" + "Ditto"
            )
        } else {
            File(
                Environment.getExternalStorageDirectory().toString() + "/" + "Ditto"
            )
        }

        /*val contextWrapper = ContextWrapper(context)
        val directory = contextWrapper.getDir("Ditto", Context.MODE_PRIVATE)*/

        if (directory.exists()) {
            val folders = directory.listFiles()
            val listOfCommonFiles: ArrayList<File> = ArrayList(emptyList())
            if (patterns != null) {
                for (file in folders) {
                    var fileName = file.name
                    if (fileName.contains(".pdf")) {
                        fileName = getNameWithoutExtension(fileName)
                    }
                    patterns.forEach {
                        if (it.prodName.toString()
                                .replace("[^A-Za-z0-9 ]".toRegex(), "") == fileName.toString()
                                .replace("[^A-Za-z0-9 ]".toRegex(), "")
                        ) {
                            listOfCommonFiles.add(file)
                        }
                    }
                }

            }

            val filesToDelete = folders.toSet().minus(listOfCommonFiles.toSet())
            logger.d("deleteFolderFun12, File to delete  >> Name: ${filesToDelete.size}")
            for (file in filesToDelete) {
                val d = deleteDirectory(file)
                logger.d("deleteFolderFun, RESULT: ${file.name} >>> $d")
            }
        }
        checkSocketConnectionBeforeWorkspace()
    }

    fun deleteDirectory(path: File): Boolean {
        if (path.exists()) {
            if (path.name.contains(".pdf")) {
                return path.delete()
            }
            val files = path.listFiles() ?: return true
            for (i in files.indices) {
                if (files[i].isDirectory) {
                    deleteDirectory(files[i])
                } else {
                    files[i].delete()
                }
            }
        }
        return path.delete()
    }

    fun getNameWithoutExtension(fileName: String): String {
        val dotIndex = fileName.lastIndexOf('.')
        return if (dotIndex == -1) fileName else fileName.substring(0, dotIndex)
    }

    private fun setImageFromSvgPngDrawable(
        foldername: String?,
        imagePath: String?,
        context: Context,
        imageView: ImageView
    ) {

        var availableUri: Uri? = null
        availableUri = Utility.isImageFileAvailable(imagePath, "${foldername}", context)
        logger.d("imageUri123, $foldername availableUri: $availableUri")

        if (imagePath?.endsWith(".svg", true) == true) {
            Glide
                .with(context)
                .load(/*if(NetworkUtility.isNetworkAvailable(context)) imagePath else */availableUri)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(com.ditto.workspace.ui.R.drawable.ic_placeholder)
                .imageDecoder(SvgBitmapDecoder(context))
                .into(imageView)

        } else if (imagePath?.endsWith(".png", true) == true) {
            Glide
                .with(context)
                .load(/*if(NetworkUtility.isNetworkAvailable(context)) imagePath else*/ availableUri)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(com.ditto.workspace.ui.R.drawable.ic_placeholder)
                .into(imageView)
        } else {
            Glide
                .with(context)
                .load(if (NetworkUtility.isNetworkAvailable(context)) imagePath else availableUri)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(com.ditto.workspace.ui.R.drawable.ic_placeholder)
                .into(imageView)
        }
    }
}

