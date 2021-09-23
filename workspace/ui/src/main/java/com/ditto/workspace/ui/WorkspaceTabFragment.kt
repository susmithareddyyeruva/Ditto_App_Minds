package com.ditto.workspace.ui

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.ditto.connectivity.ConnectivityActivity
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.workspace.domain.model.*
import com.ditto.workspace.ui.adapter.PatternPiecesAdapter
import com.ditto.workspace.ui.databinding.WorkspaceTabItemBinding
import com.ditto.workspace.ui.util.*
import com.joann.fabrictracetransform.transform.TransformErrorCode
import com.joann.fabrictracetransform.transform.performTransform
import core.appstate.AppState
import core.network.NetworkUtility
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import core.ui.common.DoubleClickListener
import core.ui.common.Utility
import core.ui.common.Utility.Companion.getAlertDialogue
import core.ui.common.Utility.Companion.getBitmap
import core.ui.common.Utility.Companion.getDrawableFromString
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.workspace_layout.*
import kotlinx.coroutines.*
import java.io.*
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap


@RequiresApi(Build.VERSION_CODES.KITKAT)
class WorkspaceTabFragment : BaseFragment(), View.OnDragListener, DraggableListener,
    Utility.CallbackDialogListener, com.ditto.workspace.ui.util.Utility.CallbackDialogListener,
    Utility.CustomCallbackDialogListener {

    @Inject
    lateinit var loggerFactory: LoggerFactory
    lateinit var backpressCall: OnBackPressedCallback
    private var cutCount = 0
    private var adapter: PatternPiecesAdapter? = null

    val logger: Logger by lazy {
        loggerFactory.create(WorkspaceTabFragment::class.java.simpleName)
    }

    private val viewModel: WorkspaceViewModel by ViewModelDelegate()
    private var mWorkspaceEditor: WorkspaceEditor? = null
    lateinit var binding: WorkspaceTabItemBinding
    private var matchedPattern: PatternsData? = null
    private var isCompleted: Boolean? = null
    private lateinit var alert: AlertDialog
    private lateinit var outputDirectory: File
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        if (!::binding.isInitialized) {
            binding = WorkspaceTabItemBinding.inflate(
                inflater
            )
        }
        return binding.root
    }

    @SuppressLint("FragmentBackPressedCallback")
    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //viewModel.isOnline.set(NetworkUtility.isNetworkAvailable(requireContext()))
        arguments?.getInt(PATTERN_ID)?.let { viewModel.patternId.set(it) }
        arguments?.getString(PATTERN_CATEGORY)?.let { viewModel.tabCategory = (it) }
        if (AppState.getIsLogged()) {
            viewModel.fetchWorkspaceSettingData()
        }
        if (viewModel.data.value == null) {
            //viewModel.fetchWorkspaceData()
            //viewModel.fetchWorkspaceDataFromAPI(result) // SFCC
            //viewModel.fetchTailernovaDataByID("demo-design-id-png", "WorkspaceTABFragment")
            setPatternPiecesAdapter()
            setUIEvents()
            enableMirror(false)
            if (mWorkspaceEditor?.views?.any() ?: true) {
                enableSelectAll(false)
                enableClear(false)
            }
        }
        setConnectButton()
        setupWorkspace()
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerViewPieces.setOnDragListener(this)
        //binding.imageCutBin.setOnDragListener(this)
        binding.cutbinLay?.setOnDragListener(this)
        binding.seekbarStatus.isEnabled = false
        binding.includeWorkspacearea?.layoutWorkspace?.setOnDragListener(this)
        binding.includeWorkspacearea?.layoutWorkspace?.let { mWorkspaceEditor?.selectAllDrag(it) }
        backpressCall =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    if (baseViewModel.isSaveExitButtonClicked.get()) {
                        viewModel.disposable.clear()
                        isEnabled = false
                        activity?.onBackPressed()
                        baseViewModel.isSaveExitButtonClicked.set(false)
                    } else {
                        downloadPatternPieces()
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, backpressCall)
        outputDirectory = Utility.getOutputDirectory(requireContext())
        binding.root?.let { setupKeyboardListener(it) }
        binding.imageSelvageHorizontal.setOnClickListener(object : DoubleClickListener(),
            View.OnClickListener {
            override fun onDoubleClick(v: View) {
                showPinchZoomPopup(requireContext(), viewModel.referenceImage.get(), true)
            }
        })
    }

    private fun setUIEvents() {
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    private fun setupWorkspace() {
        if (mWorkspaceEditor == null) {
            mWorkspaceEditor = context?.let {
                binding.includeWorkspacearea?.layoutWorkspace?.let { it1 ->
                    WorkspaceEditor.Builder(
                        it,
                        it1
                    ).build()
                }
            }
        }
    }

    private fun setPatternPiecesAdapter() {
        adapter = PatternPiecesAdapter()
        binding.recyclerViewPieces.adapter = adapter
        adapter?.viewModel = viewModel
    }

    private fun refreshPatternPiecesAdapter() {
        com.ditto.workspace.ui.util.Utility.progressCount.set(0)
        viewModel.completedPieces.set(0)
        viewModel.setCompletePieceCount()
        binding.recyclerViewPieces.adapter?.notifyDataSetChanged()
    }

    private fun startProjecting() {
        logger.d("Trace Entered Projection")
        //com.ditto.base.core.ui.common.Utility.showSnackBar("Projecting",binding.workspaceRoot)  // Checking projection scenario without connecting.
        if (mWorkspaceEditor?.views?.size ?: 0 < 1) {
            GlobalScope.launch {
                Utility.sendDittoImage(
                    requireContext(),
                    "solid_black"
                )
            }
        } else {
            if (baseViewModel.activeSocketConnection.get()) {
                if (baseViewModel.isProjecting.get()) {
                    showWaitingMessage(resources.getString(R.string.projection_area_progress))
                } else {
                    showWaitingMessage(resources.getString(R.string.projecting_messsage))
                    GlobalScope.launch {
                        if (viewModel.isProjectionRequest.get() && !baseViewModel.isProjecting.get()) {
                            viewModel.isFromQuickCheck.set(false)
                            transform(
                                getVirtualWorkspace(), false
                            )     // ----------plexar library integration---------------------//
                            //projectWorkspaceImage(getVirtualWorkspace(),false false)
                        }
                    }
                }
            }
        }
    }

    private fun setupKeyboardListener(view: View?) {
        view?.viewTreeObserver?.addOnGlobalLayoutListener {
            val r = Rect()
            view.getWindowVisibleDisplayFrame(r)
            if (Math.abs(view.rootView.height - (r.bottom - r.top)) > (view.rootView.height / 2)) { // if more than 100 pixels, its probably a keyboard...
                if (isTablet(requireContext())) {
                    com.ditto.workspace.ui.util.Utility.changeAlertPsoition(
                        0,
                        view.rootView.height / 2
                    )

                } else {
                    com.ditto.workspace.ui.util.Utility.changeAlertPsoition(
                        1,
                        view.rootView.height / 2
                    )
                }
            } else {
                com.ditto.workspace.ui.util.Utility.changeAlertPsoition(2, view.rootView.height / 2)
            }
        }
    }

    fun isTablet(context: Context?): Boolean {
        val xlarge = context?.resources
            ?.configuration?.screenLayout?.and(Configuration.SCREENLAYOUT_SIZE_MASK) ?: 0 == 4
        val large = context?.resources
            ?.configuration?.screenLayout?.and(Configuration.SCREENLAYOUT_SIZE_MASK) ?: 0 == Configuration.SCREENLAYOUT_SIZE_LARGE
        return xlarge || large
    }

    private fun showWaitingMessage(message: String) {
        try {
            Utility.showSnackBar(message, binding?.workspaceRoot)
        } catch (e: java.lang.Exception) {
            logger.d("Error : " + e.message)
        }
    }

    private fun disableInchTabs() {
        viewModel.clickedSplice.set(false)
        binding.txtSizeSplice.isEnabled = false
        viewModel.enableSplice.set(false)
        binding.txtSize45.isEnabled = false
        viewModel.enableSize45.set(false)
        binding.txtSize60.isEnabled = false
        viewModel.enableSize60.set(false)
    }

    private fun disableInchTabs(view: TextView) {
        view.isEnabled = false
        viewModel
        view.setBackgroundResource(R.drawable.rounded_light_bg)
        view.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.default_splice
            )
        )
    }

    private fun saveBitmap(bitmap: Bitmap) {
        val photoFile = File(
            outputDirectory, "TRACE_IMAGE_" +
                    SimpleDateFormat(
                        FILENAME_FORMAT, Locale.US
                    ).format(System.currentTimeMillis()) + ".jpg"
        )
        try {
            val stream: OutputStream = FileOutputStream(photoFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
            Utility.galleryAddPic(requireContext(), photoFile.absolutePath)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun transform(bitmap: Bitmap, isProjectingSample: Boolean) {
        logger.d("TRACE_ Projection :Transform start " + Calendar.getInstance().timeInMillis)
        viewModel.isStartedProjection.set(true)
        viewModel.isProjectionRequest.set(false)
        baseViewModel.isProjecting.set(true)
        saveBitmap(bitmap)
        viewModel.disposable += Observable.fromCallable {
            performTransform(bitmap, context?.applicationContext, null, true)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleResult(it, isProjectingSample) }

    }

    private fun showProgress(toShow: Boolean) {
        bottomNavViewModel.showProgress.set(toShow)
    }

    private fun handleResult(
        result: Pair<TransformErrorCode, Bitmap>,
        isProjectingSample: Boolean
    ) {
        logger.d("quick check Transform - ${result.second.width} * ${result.second.height}")
        //alert.dismiss()
        when (result.first) {
            TransformErrorCode.Success -> {
                logger.d("TRACE_ Projection :TransformErrorCode.Success " + Calendar.getInstance().timeInMillis)
                saveBitmap(result.second)
                logger.d("TRACE_ Transformed bitmap width " + result.second.width)
                logger.d("TRACE_ Transformed bitmap height " + result.second.height)
                GlobalScope.launch {
                    projectWorkspaceImage(
                        result.second,
                        viewModel.isFromQuickCheck.get(),
                        isProjectingSample
                    )
                }
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

    private suspend fun projectWorkspaceImage(
        bitmap: Bitmap,
        isQuickCheck: Boolean,
        isProjectingSample: Boolean
    ) {
        withContext(Dispatchers.IO) {
            logger.d("TRACE_ Projection :projectWorkspaceImage Start " + Calendar.getInstance().timeInMillis)
            var clientSocket: Socket? = null
            try {
                viewModel.isStartedProjection.set(true)
                viewModel.isProjectionRequest.set(false)
                baseViewModel.isProjecting.set(true)
                val transformedBitmap =
                    Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
                val canvas = Canvas(transformedBitmap)
                canvas.drawColor(Color.BLACK)
                canvas.drawBitmap(bitmap, 0F, 0F, null)
                clientSocket = Socket(
                    core.network.NetworkUtility.nsdSericeHostName,
                    core.network.NetworkUtility.nsdSericePortName
                )
                val workspaceStream = ByteArrayOutputStream()
                transformedBitmap.compress(Bitmap.CompressFormat.PNG, 0, workspaceStream)
                val bitmapdata = workspaceStream.toByteArray()
                transformedBitmap.recycle()
                if (clientSocket.isConnected) {
                    baseViewModel.activeSocketConnection.set(true)
                    var dataOutputStream: DataOutputStream =
                        DataOutputStream(clientSocket.getOutputStream())
                    dataOutputStream.write(bitmapdata)
                    dataOutputStream.close()
                    baseViewModel.isProjecting.set(false)
                    withContext(Dispatchers.Main) {
                        logger.d("TRACE_ Projection :projectWorkspaceImage Finish " + Calendar.getInstance().timeInMillis)
                        showProgress(false)
                        if (isProjectingSample) {
                            navigateToCalibration()
                        }
                        if (isQuickCheck) {
                            showQuickCheckDialog()
                        }
                    }
                } else {
                    baseViewModel.activeSocketConnection.set(false)
                    baseViewModel.isProjecting.set(false)
                    viewModel.isProjectionRequest.set(false)
                    logger.d("Socket Connection lost!!")
                    withContext(Dispatchers.Main) {
                        logger.d("TRACE_ Projection :projectWorkspaceImage Finish " + Calendar.getInstance().timeInMillis)
                        showProgress(false)
                        /*Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.socketfailed),
                            Toast.LENGTH_SHORT
                        ).show()*/
                        showFailurePopup()
                    }
                }
            } catch (e: Exception) {
                baseViewModel.activeSocketConnection.set(false)
                baseViewModel.isProjecting.set(false)
                viewModel.isProjectionRequest.set(false)
                logger.d("Exception " + e.message)
                withContext(Dispatchers.Main) {
                    logger.d("TRACE_ Projection :projectWorkspaceImage Finish " + Calendar.getInstance().timeInMillis)
                    showProgress(false)
                    showFailurePopup()
                }
            } finally {
                clientSocket?.close()
            }
        }
    }

    private fun setSelvageImage() {
        if (viewModel.clickedSplice.get()) {
            val splicePiece = getSplicePiece(
                viewModel.workspacedata?.currentSplicedPieceRow ?: 0,
                viewModel.workspacedata?.currentSplicedPieceColumn ?: 0,
                viewModel.workspacedata?.splicedImages
            )
            // Setting splice reference layout
            showSpliceReference(splicePiece)
            return
        }
        if (viewModel.data.value?.selvages?.filter { it.tabCategory == getString(R.string.garments) }?.size ?: 0 > 0 &&
            viewModel.tabCategory == getString(R.string.garments)
        ) {
            val garments =
                viewModel.data.value?.selvages?.filter {
                    it.tabCategory == getString(
                        R.string.garments
                    )
                }
            if (garments?.size == 2) {
                binding.txtSize45.isEnabled = true
                binding.txtSize60.isEnabled = true
                viewModel.enableSize45.set(true)
                viewModel.enableSize60.set(true)
                if (!viewModel.clickedSize45.get() && !viewModel.clickedSize60.get()) {
                    viewModel.clickedSize45.set(true)
                    viewModel.clickedSize60.set(false)
                }
                if (viewModel.clickedSize45.get()) {
                    val selvage = garments.filter { it.fabricLength == "45" }[0]

                    logger.d(">>>>>>>>>>>>>>>>>>>>>>>>> ${selvage.imagePath}")
                    selvage.imagePath.let {
                        /* binding.imageSelvageHorizontal.setImageDrawable(
                             getDrawableFromString(context, it)
                         )*/
                        getBitmapFromSvgPngDrawable(
                            selvage.imagePath,
                            binding.imageSelvageHorizontal.context,
                            binding.imageSelvageHorizontal
                        )

                    }
                    viewModel.clickedSize45.set(true)
                    viewModel.clickedSize60.set(false)
                    viewModel.referenceImage.set(selvage.imagePath)
                }
                if (viewModel.clickedSize60.get()) {
                    val selvage = garments.filter { it.fabricLength == "60" }[0]
                    selvage.imagePath.let {
                        /* binding.imageSelvageHorizontal.setImageDrawable(
                             getDrawableFromString(context, it)
                         )*/
                        getBitmapFromSvgPngDrawable(
                            selvage.imagePath,
                            binding.imageSelvageHorizontal.context,
                            binding.imageSelvageHorizontal
                        )

                    }
                    viewModel.clickedSize45.set(false)
                    viewModel.clickedSize60.set(true)
                    viewModel.referenceImage.set(selvage.imagePath)
                }
            } else {
                if (garments?.get(0)!!.fabricLength == "45") {
                    binding.txtSize45.isEnabled = true
                    viewModel.enableSize45.set(true)
                    viewModel.clickedSize45.set(true)
                } else if (garments[0].fabricLength == "60") {
                    binding.txtSize60.isEnabled = true
                    viewModel.enableSize60.set(true)
                    viewModel.clickedSize60.set(true)
                }
                garments[0].imagePath.let {
                    binding.imageSelvageHorizontal.setImageDrawable(
                        getDrawableFromString(context, it)
                    )
                    getBitmapFromSvgPngDrawable(
                        garments[0].imagePath,
                        binding.imageSelvageHorizontal.context,
                        binding.imageSelvageHorizontal
                    )
                }
                viewModel.referenceImage.set(garments[0].imagePath)
            }
        }

        if (viewModel.data.value?.selvages?.filter { it.tabCategory == getString(R.string.lining) }
                ?.isNotEmpty() == true &&
            viewModel.tabCategory == getString(R.string.lining)
        ) {
            val lining =
                viewModel.data.value?.selvages?.filter {
                    it.tabCategory == getString(
                        R.string.lining
                    )
                }
            lining?.get(0)?.imagePath?.let {
/*
                binding.imageSelvageHorizontal.setImageDrawable(
                    getDrawableFromString(context, it)
                )
*/

                getBitmapFromSvgPngDrawable(
                    lining?.get(0)?.imagePath,
                    binding.imageSelvageHorizontal.context,
                    binding.imageSelvageHorizontal
                )

            }
            binding.txtSize45.isEnabled = lining?.get(0)?.fabricLength == "45"
            binding.txtSize60.isEnabled = lining?.get(0)?.fabricLength == "60"
            viewModel.enableSize45.set(lining?.get(0)?.fabricLength == "45")
            viewModel.enableSize60.set(lining?.get(0)?.fabricLength == "60")

            if (lining?.get(0)?.fabricLength == "45") {
                viewModel.clickedSize45.set(true)
            } else {
                viewModel.clickedSize60.set(true)
            }
            viewModel.referenceImage.set(lining?.get(0)?.imagePath)
        }

        if (viewModel.data.value?.selvages?.filter { it.tabCategory == getString(R.string.interfacing) }
                ?.isNotEmpty() == true &&
            viewModel.tabCategory == getString(R.string.interfacing)
        ) {
            val interfacing =
                viewModel.data.value?.selvages?.filter {
                    it.tabCategory == getString(
                        R.string.interfacing
                    )
                }
            interfacing?.get(0)?.imagePath?.let {
                /*binding.imageSelvageHorizontal.setImageDrawable(
                    getDrawableFromString(context, it)
                )*/

                getBitmapFromSvgPngDrawable(
                    interfacing?.get(0)?.imagePath,
                    binding.imageSelvageHorizontal.context,
                    binding.imageSelvageHorizontal
                )

            }
            binding.txtSize45.isEnabled = interfacing?.get(0)?.fabricLength == "45"
            binding.txtSize60.isEnabled = interfacing?.get(0)?.fabricLength == "60"
            viewModel.enableSize45.set(interfacing?.get(0)?.fabricLength == "45")
            viewModel.enableSize60.set(interfacing?.get(0)?.fabricLength == "60")

            if (interfacing?.get(0)?.fabricLength == "45") {
                viewModel.clickedSize45.set(true)
            } else {
                viewModel.clickedSize60.set(true)
            }

            viewModel.referenceImage.set(interfacing?.get(0)?.imagePath)
        }
    }

    // todo
    fun fetchWorkspaceData(selectedTab:Int): MutableList<WorkspaceItems>? {

        if ( selectedTab == 0) {
            viewModel.data.value?.garmetWorkspaceItemOfflines =
                mWorkspaceEditor?.views?.toMutableList()
            return viewModel.data.value?.garmetWorkspaceItemOfflines
        } else if (selectedTab == 1) {
            viewModel.data.value?.liningWorkspaceItemOfflines =
                mWorkspaceEditor?.views?.toMutableList()
            return viewModel.data.value?.liningWorkspaceItemOfflines
        } else {
            viewModel.data.value?.interfaceWorkspaceItemOfflines =
                mWorkspaceEditor?.views?.toMutableList()
            return viewModel.data.value?.interfaceWorkspaceItemOfflines
        }
    }

    fun clearWorkspace() {
        disableInchTabs()
        setSelvageImage()
        viewModel.showDoubleTouchToZoom.set(false)
        binding.invalidateAll()

//        if (com.ditto.workspace.ui.util.Utility.progressCount.get() == 0) {
//            viewModel.clickReset()
//        }
//        if (viewModel.completedPieces.get() == 0) {
//            viewModel.clickReset()
//        }
//        binding.includeWorkspacearea?.layoutWorkspaceBackground?.setBackgroundResource(
//            R.drawable.ic_workspace_new
//        )
//        binding.includeWorkspacearea?.layoutSelectAllMask?.visibility = View.GONE
        viewModel.selectAllText.set(getString(R.string.select_all))
        enableMirror(false)
        enableSelectAll(false)
        enableClear(false)
        mWorkspaceEditor?.clearAllViews()
        viewModel.workspacedata = null
        viewModel.spliced_pices_visibility.set(false)
        viewModel.clicked_spliced_second_pieces.set(viewModel.spliced_pices.get() == 2) // setting true only when screen 2 else false by default
        onUpdateProgressCount()
        viewModel.isSpliceBottomVisible.set(false)
        viewModel.isSpliceTopVisible.set(false)
        viewModel.isSpliceLeftVisible.set(false)
        viewModel.isSpliceRightVisible.set(false)
    }

    private fun calculateScrollButtonVisibility() {
        val layoutManager: LinearLayoutManager =
            binding.recyclerViewPieces.layoutManager as LinearLayoutManager
        viewModel.firstItemVisible.set(layoutManager.findFirstCompletelyVisibleItemPosition())
        viewModel.lastItemVisible.set(layoutManager.findLastCompletelyVisibleItemPosition())
        binding.recyclerViewPieces.adapter?.itemCount?.minus(1)
            ?.let { viewModel.totalItemCount.set(it) }
        viewModel.isScrollButtonVisible.set(!((viewModel.firstItemVisible.get() == 0) && (viewModel.lastItemVisible.get() == viewModel.totalItemCount.get())))
        binding.recyclerViewPieces.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.isFirstItemVisible.set(false)
                viewModel.isLastItemVisible.set(false)
                if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    viewModel.isFirstItemVisible.set(true)
                    return
                }
                val shouldPullUpRefresh =
                    layoutManager.findLastCompletelyVisibleItemPosition() == viewModel.totalItemCount.get()
                if (shouldPullUpRefresh) {
                    viewModel.isLastItemVisible.set(true)
                    return
                }
            }
        })
    }

    private fun showCutBinDialog(count: Int, alertType: Utility.AlertType) {
        if (viewModel.userData.value?.cCuttingReminder ?: true) {
            Utility.getCommonAlertDialogue(
                requireContext(),
                "",
                resources.getString(R.string.complete_piece, count),
                resources.getString(R.string.no),
                resources.getString(R.string.yes),
                this,
                alertType,
                Utility.Iconype.NONE
            )
        } else {
            adapter?.updatePositionAdapter()
            viewModel.cutCheckBoxClicked(viewModel.cutCount, true)
        }
    }

    private fun showSplicingForgetDialogue(alertType: Utility.AlertType) {
        Utility.getCommonAlertDialogue(
            requireContext(),
            resources.getString(R.string.complete_cutbin),
            resources.getString(R.string.click_spliced_second_pieces),
            resources.getString(R.string.empty_string),
            resources.getString(R.string.ok),
            this,
            alertType,
            Utility.Iconype.NONE
        )
    }

    fun getPatternPieceListTailornova(): HashMap<String, String> {
        var hashMap: HashMap<String, String> = HashMap<String, String>()
        hashMap[viewModel.data.value?.thumbnailImageName.toString()] = viewModel.data.value?.thumbnailImagePath.toString()
        for (patternItem in viewModel.data.value?.selvages!!) {
            hashMap[patternItem.imageName.toString()] = patternItem.imagePath.toString()
        }
        for (patternItem in viewModel.data.value?.patternPieces!!) {
            hashMap[patternItem.thumbnailImageName.toString()] = patternItem.thumbnailImageUrl.toString()
            hashMap[patternItem.imageName.toString()] = patternItem.imagePath.toString()
            for (splicedImage in patternItem.splicedImages) {
                hashMap[splicedImage.imageName.toString()] = splicedImage.imagePath.toString()
                hashMap[splicedImage.mapImageName.toString()] = splicedImage.mapImageUrl.toString()
            }
        }
        return hashMap
    }

    private fun handleEvent(event: WorkspaceViewModel.Event) =
        when (event) {
            is WorkspaceViewModel.Event.OnClickScrollLeft -> {
                binding.recyclerViewPieces.smoothScrollBy(-200, 0)
            }
            is WorkspaceViewModel.Event.OnClickScrollRight -> {
                binding.recyclerViewPieces.smoothScrollBy(200, 0)
            }
            is WorkspaceViewModel.Event.OnClickSaveAndExit -> {
                downloadPatternPieces()
            }
            is WorkspaceViewModel.Event.OnClickSelectAll -> {
                if (mWorkspaceEditor?.views?.any() ?: false) {
                    viewModel.selectAllText.set(getString(R.string.de_select_all))
                    mWorkspaceEditor?.selectAllSelection()
                } else Utility.showSnackBar(
                    getString(R.string.no_item_in_workspace), binding.topBorder
                )
            }
            is WorkspaceViewModel.Event.OnClickDeSelectAll -> {
                viewModel.selectAllText.set(getString(R.string.select_all))
                mWorkspaceEditor?.clearAllSelection()
            }
            is WorkspaceViewModel.Event.OnClickMirrorVertical -> {
                mWorkspaceEditor?.flipVertical()
            }
            is WorkspaceViewModel.Event.OnClickMirrorHorizontal -> {
                mWorkspaceEditor?.flipHorizontal()
            }
            is WorkspaceViewModel.Event.CalculateScrollButtonVisibility -> {
                calculateScrollButtonVisibility()
            }
            is WorkspaceViewModel.Event.OnDataUpdated -> {
                Log.d("OnDataUpdated"," WSFragment OnDataUpdated")
                setSelvageImage()
                getScaleFactor()
                setInitialProgressCount()
            }
            is WorkspaceViewModel.Event.OnClickInch -> {
                setSelvageImage()
            }
            is WorkspaceViewModel.Event.ShowMirrorDialog -> {
                if (viewModel.userData.value?.cMirrorReminder ?: true) {
                    Utility.getCommonAlertDialogue(
                        requireContext(),
                        resources.getString(R.string.mirror),
                        resources.getString(R.string.mirror_message),
                        resources.getString(R.string.cancel),
                        resources.getString(R.string.ok),
                        this,
                        Utility.AlertType.MIRROR,
                        Utility.Iconype.NONE
                    )
                } else {
                    if (viewModel.isHorizontalMirror) {
                        mWorkspaceEditor?.flipHorizontal()
                    } else {
                        mWorkspaceEditor?.flipVertical()
                    }
                }
            }
            is WorkspaceViewModel.Event.DisableMirror -> {
                enableMirror(false)
            }
            is WorkspaceViewModel.Event.EnableMirror -> {
                enableMirror(true)
            }
            is WorkspaceViewModel.Event.OnClickSpliceRight -> {
                mWorkspaceEditor?.clearAllViews()
                if (isSpliceDirectionAvailable(
                        viewModel.workspacedata?.currentSplicedPieceRow ?: 0,
                        viewModel.workspacedata?.currentSplicedPieceColumn?.plus(1) ?: 0,
                        viewModel.workspacedata?.splicedImages
                    )
                ) {
                    viewModel.workspacedata?.currentSplicedPieceColumn =
                        viewModel.workspacedata?.currentSplicedPieceColumn?.plus(1) ?: 0
                    showToWorkspace(true, false,viewModel.workspacedata)
                    mWorkspaceEditor?.highlightSplicePiece()
                    enableClear(true)
                } else {
                    //TODO
                }

            }
            is WorkspaceViewModel.Event.OnClickSpliceLeft -> {
                mWorkspaceEditor?.clearAllViews()
                if (isSpliceDirectionAvailable(
                        viewModel.workspacedata?.currentSplicedPieceRow ?: 0,
                        viewModel.workspacedata?.currentSplicedPieceColumn?.minus(1) ?: 0,
                        viewModel.workspacedata?.splicedImages
                    )
                ) {
                    viewModel.workspacedata?.currentSplicedPieceColumn =
                        viewModel.workspacedata?.currentSplicedPieceColumn?.minus(1) ?: 0
                    showToWorkspace(true, false,viewModel.workspacedata);
                    mWorkspaceEditor?.highlightSplicePiece()
                    enableClear(true)
                } else {
                    //TODO
                }
            }
            is WorkspaceViewModel.Event.OnClickSpliceBottom -> {
                mWorkspaceEditor?.clearAllViews()
                if (isSpliceDirectionAvailable(
                        viewModel.workspacedata?.currentSplicedPieceRow?.minus(1) ?: 0,
                        viewModel.workspacedata?.currentSplicedPieceColumn ?: 0,
                        viewModel.workspacedata?.splicedImages
                    )
                ) {
                    viewModel.workspacedata?.currentSplicedPieceRow =
                        viewModel.workspacedata?.currentSplicedPieceRow?.minus(1) ?: 0
                    showToWorkspace(true, false,viewModel.workspacedata);
                    mWorkspaceEditor?.highlightSplicePiece()
                    enableClear(true)
                } else {
                    //TODO
                }
            }
            is WorkspaceViewModel.Event.OnClickSpliceTop -> {
                mWorkspaceEditor?.clearAllViews()
                if (isSpliceDirectionAvailable(
                        viewModel.workspacedata?.currentSplicedPieceRow?.plus(1) ?: 0,
                        viewModel.workspacedata?.currentSplicedPieceColumn ?: 0,
                        viewModel.workspacedata?.splicedImages
                    )
                ) {
                    viewModel.workspacedata?.currentSplicedPieceRow =
                        viewModel.workspacedata?.currentSplicedPieceRow?.plus(1) ?: 0
                    showToWorkspace(true, false,viewModel.workspacedata)
                    mWorkspaceEditor?.highlightSplicePiece()
                    enableClear(true)
                } else {
                    //TODO
                }
            }

            is WorkspaceViewModel.Event.OnRecalibrateClicked -> {
                if (baseViewModel.isProjecting.get()) {
                    showWaitingMessage(resources.getString(R.string.projection_progress))
                } else {
                    if (baseViewModel.activeSocketConnection.get()) {
                        sendBorderImage()
//                        if (baseViewModel.isUserNeedCalibrated.get()) {
//                            sendBorderImage()
//                        } else {
//                            showCalibrationDialog()
//                        }
                    } else {
                        checkBluetoothWifiPermission()
                    }
                }
            }
            is WorkspaceViewModel.Event.OnClickPatternInstructions -> {
                if (findNavController().currentDestination?.id == R.id.workspaceFragment) {
                    val bundle =
                        bundleOf("PatternName" to viewModel.data.value?.patternPieces?.get(0)?.parentPattern)
                    findNavController().navigate(
                        R.id.action_workspaceFragment_to_pattern_instructions_Fragment,
                        bundle
                    )
                } else {
                    Log.d("Error", "Invalid currentDestination?.id")
                }
            }
            is WorkspaceViewModel.Event.ClearWorkspace -> {
                clearWorkspace()
            }
            is WorkspaceViewModel.Event.CloseScreen -> {
                showProgress(false)
                baseViewModel.isSaveExitButtonClicked.set(true)
                findNavController().popBackStack(R.id.patternDescriptionFragment, false)
                activity?.onBackPressed()
            }
            is WorkspaceViewModel.Event.PopulateWorkspace -> {
                //Loading only the current tab while populating

                var workspaceItems: MutableList<WorkspaceItems>? = null
                if (viewModel.tabCategory.equals("Garment")) {
                    workspaceItems =
                        viewModel.getWorkspaceDimensions(viewModel.data.value?.garmetWorkspaceItemOfflines) as MutableList<WorkspaceItems>?
                } else if (viewModel.tabCategory.equals("Lining")) {
                    workspaceItems =
                        viewModel.getWorkspaceDimensions(viewModel.data.value?.liningWorkspaceItemOfflines) as MutableList<WorkspaceItems>?
                } else if (viewModel.tabCategory.equals("Interfacing")) {
                    workspaceItems =
                        viewModel.getWorkspaceDimensions(viewModel.data.value?.interfaceWorkspaceItemOfflines) as MutableList<WorkspaceItems>?
                }
                // set id of workspace item to the oldest large value
                com.ditto.workspace.ui.util.Utility.workspaceItemId.set(
                    workspaceItems?.maxBy { it.id }?.id ?: 0
                )
                var i = 0
                if (workspaceItems != null) {
                    for (workspaceItem in workspaceItems) {
                        i++
                        viewModel.workspacedata = workspaceItem
                        showToWorkspace(i == workspaceItems.size, false,workspaceItem)
                    }
                } else {
                    logger.d("workspace item is null")
                }
            }
            is WorkspaceViewModel.Event.onProject -> {
                viewModel.isProjectionRequest.set(true)
                if (baseViewModel.activeSocketConnection.get()) {
                    startProjecting()
                } else {
                    //checkBluetoothWifiPermission()
                }
            }
            is WorkspaceViewModel.Event.ShowCutBinDialog -> {
                showCutBinDialog(viewModel.cutCount, viewModel.cutType)
                /*if (!viewModel.clicked_spliced_second_pieces.get() && viewModel.spliced_pices_visibility.get()) {
                    //showSplicingForgetDialogue(Utility.AlertType.DEFAULT)
                } else {
                    showCutBinDialog(viewModel.cutCount, viewModel.cutType)
                }*/
            }
            is WorkspaceViewModel.Event.RemoveAllPatternPieces -> {
                clearWorkspace()
                if (mWorkspaceEditor?.views?.size ?: 0 > 0) {
                    viewModel.workspacedata = mWorkspaceEditor?.views?.get(0)
                } else {
                    viewModel.workspacedata = null
                    clearWorkspace()
                }
                adapter?.notifyDataSetChanged()
                onDragCompleted()
            }
            is WorkspaceViewModel.Event.updateProgressCount -> {
                onUpdateProgressCount()
            }
            is WorkspaceViewModel.Event.OnClickClear -> {
                if (mWorkspaceEditor?.views?.any() ?: false) {
                    if (viewModel.selectAllText.get() == (getString(R.string.de_select_all))) {
                        viewModel.selectAllText.set(getString(R.string.select_all))
                        clearWorkspace()
                    } else {
                        mWorkspaceEditor?.removePattern(viewModel.workspacedata, true)
                        if (mWorkspaceEditor?.views?.size!! > 0) {
                            viewModel.workspacedata = mWorkspaceEditor?.views?.get(0)
                            enableClear(false)
                            enableMirror(false)
                            enableSelectAll(true)
                        } else {
                            viewModel.workspacedata = null
                            clearWorkspace()
                        }
                    }
                    viewModel.clickPatternReference(true)
                } else Utility.showSnackBar(
                    getString(R.string.no_item_in_workspace), binding.topBorder
                )
            }
            is WorkspaceViewModel.Event.OnDownloadComplete -> {
                /**
                 * All Pattern Pieces Downloaded Successfully
                 */
                if (viewModel.temp.size == getPatternPieceListTailornova().size) {
                    bottomNavViewModel.showProgress.set(false)
                    Log.d("DOWNLOAD","ENDED >>>>>>>>>>>")
                    //moveToLibrary()
                    showSaveAndExitPopup()
                }else{

                }

            }
            is WorkspaceViewModel.Event.OnClickTutorial -> {
                navigateToTutorial()
            }
            is WorkspaceViewModel.Event.OnResetClicked -> {
                refreshPatternPiecesAdapter()
            }
            is WorkspaceViewModel.Event.OnClickPatternOrReference -> {
                onUpdateFont()
            }
            is WorkspaceViewModel.Event.DisableClear -> {
                enableClear(false)
            }
            is WorkspaceViewModel.Event.EnableClear -> {
                enableClear(true)
            }
            is WorkspaceViewModel.Event.DisableSelectAll -> {
                enableSelectAll(false)
            }
            is WorkspaceViewModel.Event.EnableSelectAll -> {
                enableSelectAll(true)
            }
            is WorkspaceViewModel.Event.ShowProgressLoader -> {
                showProgress(true)
            }
            is WorkspaceViewModel.Event.HideProgressLoader -> {
                showProgress(false)
            }
            is WorkspaceViewModel.Event.ApiFailed -> {
                showProgress(false)
                Utility.getCommonAlertDialogue(
                    requireContext(),
                    resources.getString(R.string.api_failed),
                    resources.getString(R.string.api_failed_message),
                    resources.getString(R.string.empty_string),
                    resources.getString(R.string.ok),
                    this,
                    Utility.AlertType.UPDATEAPIFAILED,
                    Utility.Iconype.NONE
                )
            }
        }

    fun downloadPatternPieces() {
        if (!baseViewModel.isProjecting.get()) {
                    binding.buttonSaveAndExit.isEnabled = false
                    val map = getPatternPieceListTailornova()
                    if (context?.let { core.network.NetworkUtility.isNetworkAvailable(it) }!!) {
                        if (dowloadPermissonGranted()) {
                            bottomNavViewModel.showProgress.set(true)
                            viewModel.prepareDowloadList(map)
                        } else {
                            requestPermissions(
                                REQUIRED_PERMISSIONS_DOWNLOAD,
                                REQUEST_CODE_PERMISSIONS_DOWNLOAD
                            )

                        }
                    } else {
                        //no internet available
                        showProgress(false)
                        Utility.getCommonAlertDialogue(
                            requireContext(),
                            resources.getString(R.string.api_failed),
                            resources.getString(R.string.api_failed_message),
                            resources.getString(R.string.empty_string),
                            resources.getString(R.string.ok),
                            this@WorkspaceTabFragment,
                            Utility.AlertType.UPDATEAPIFAILED,
                            Utility.Iconype.NONE
                        )
                    }
                } else {
                    showWaitingMessage("Projection is under process.. Please wait")
                }
    }

    fun updateTabData(patternsData: PatternsData?) {
        viewModel.data.value = patternsData
    }

    fun updateTabDataAndShowToUI(patternsData: PatternsData?){
        viewModel.data.value = patternsData
        viewModel.setWorkspaceView()
    }


    private fun onUpdateFont() {
        binding.txtPatternPieces.typeface = ResourcesCompat.getFont(
            requireContext(),
            if (viewModel.clickedPattenPieces.get()) R.font.avenir_next_lt_pro_demi else R.font.avenir_next_lt_pro_regular
        )
        binding.txtReeferanceLayout.typeface = ResourcesCompat.getFont(
            requireContext(),
            if (viewModel.clickedPattenPieces.get()) R.font.avenir_next_lt_pro_regular else R.font.avenir_next_lt_pro_demi
        )
    }

    private fun onUpdateProgressCount() {
        binding.seekbarStatus.progress = 0
        binding.seekbarStatus.max = viewModel.data?.value?.totalPieces ?: 0
        binding.seekbarStatus.progress = com.ditto.workspace.ui.util.Utility.progressCount.get()
    }

    override fun onResume() {
        super.onResume()
        calculateScrollButtonVisibility()
        requireActivity().window
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        requireActivity().window
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        requireActivity().window
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        viewModel.isWorkspaceSocketConnection.set(baseViewModel.activeSocketConnection.get())
        if (com.ditto.workspace.ui.util.Utility.isDoubleTapTextVisible.get() != true) {
            viewModel.showDoubleTouchToZoom.set(false)
            // Hide double tap to zoom text after showing
        }
    }

    override fun onDrag(view: View?, dragEvent: DragEvent?): Boolean {

        val dragData: DragData? = if (dragEvent?.localState == null) {
            com.ditto.workspace.ui.util.Utility.dragData.get() as DragData?
        } else {
            dragEvent.localState as DragData?
        }
        when (dragEvent?.action) {
            DragEvent.ACTION_DRAG_ENTERED -> {
                if ((dragData?.type == Draggable.SELECT_ALL || dragData?.type == Draggable.DRAG_OUT_WORKSPACE)
                    && (view?.id == R.id.cutbin_lay || view?.id == R.id.recycler_view_pieces)
                ) {
//                    detectTouchedView(view)
                }
            }
            DragEvent.ACTION_DRAG_EXITED, DragEvent.ACTION_DRAG_ENDED -> {
//                if (view?.id == R.id.cutbin_lay) {
//                    binding.imageCutBin.setBackgroundColor(Color.TRANSPARENT)
//                } else if (view?.id == R.id.recycler_view_pieces) {
//                    binding.recyclerViewPieces.setBackgroundColor(Color.TRANSPARENT)
//                }
            }
            DragEvent.ACTION_DROP ->
//                if (view?.id == R.id.cutbin_lay) {
//                    if (dragData?.type == Draggable.SELECT_ALL) {
//                        mWorkspaceEditor?.clearAllSelection()
//                        enableMirror(false)
//                        viewModel.selectAllText.set(getString(R.string.select_all))
//                        mWorkspaceEditor?.views?.let { viewModel.cutSelectAll(it) }
//                    } else if (dragData?.type == Draggable.DRAG_OUT_WORKSPACE) {
//                        val count = dragData?.workspaceItems?.cutQuantity?.get(4)
//                            ?.let { Character.getNumericValue(it) }
//                        cutPieces(count)
//                    }
//                } else if (view?.id == R.id.recycler_view_pieces) {
//                    mWorkspaceEditor?.clearAllSelection()
//                    enableMirror(false)
//                    if (dragData?.type == Draggable.SELECT_ALL) {
//                        viewModel.selectAllText.set(getString(R.string.select_all))
//                        clearWorkspace()
//                    } else if (dragData?.type == Draggable.DRAG_OUT_WORKSPACE) {
//                        mWorkspaceEditor?.removePattern(viewModel.workspacedata, true)
//                        if (mWorkspaceEditor?.views?.size!! > 0) {
//                            viewModel.workspacedata = mWorkspaceEditor?.views?.get(0)
//                        } else {
//                            viewModel.workspacedata = null
//                            clearWorkspace()
//                        }
//                    }
//                    onDragCompleted()
//                } else
                if (view?.id == R.id.layout_workspace) {
                    // set id of initial item to be 0
                    if (mWorkspaceEditor?.views?.size == 0) {
                        com.ditto.workspace.ui.util.Utility.workspaceItemId.set(0)
                    }
                    if (dragData?.type == Draggable.SELECT_TO_WORKSPACE) {
                        enableSelectAll(true)
//                        enableClear(true)
                        if (!(dragData?.patternPieces?.splice ?: true)) {
                            if (viewModel.workspacedata?.splice ?: false) {
                                if (viewModel.userData.value?.cSpliceMultiplePieceReminder
                                        ?: true
                                ) {
                                    Utility.getCommonAlertDialogue(
                                        requireActivity(),
                                        resources.getString(R.string.spliced_piece),
                                        resources.getString(R.string.spliced_piece_message),
                                        resources.getString(R.string.empty_string),
                                        resources.getString(R.string.ok),
                                        this,
                                        Utility.AlertType.CUT_BIN,
                                        Utility.Iconype.NONE
                                    )
                                }
                                return true
                            }
                            mWorkspaceEditor?.clearAllSelection()
                            enableMirror(false)
                            com.ditto.workspace.ui.util.Utility.workspaceItemId.set(
                                com.ditto.workspace.ui.util.Utility.workspaceItemId.get() + 1
                            )
                            viewModel.setImageModel(
                                view, dragEvent, dragData,
                                com.ditto.workspace.ui.util.Utility.workspaceItemId.get()
                            )
                            showToWorkspace(true, true,viewModel.workspacedata)
                        } else {
                            if ((mWorkspaceEditor?.isWorkspaceNotEmpty) != false) {
                                if (viewModel.userData.value?.cSpliceMultiplePieceReminder
                                        ?: true
                                ) {
                                    Utility.getCommonAlertDialogue(
                                        requireActivity(),
                                        resources.getString(R.string.splicing_required),
                                        resources.getString(R.string.splicing_required_message),
                                        resources.getString(R.string.empty_string),
                                        resources.getString(R.string.ok),
                                        this,
                                        Utility.AlertType.CUT_BIN,
                                        Utility.Iconype.NONE
                                    )
                                }
                                return true
                            } else {
                                if (viewModel.userData.value?.cSpliceReminder ?: true) {
                                    if ((dragData?.patternPieces?.splicedImages?.size)!! > 2) {
                                        Utility.getCommonAlertDialogue(
                                            requireActivity(),
                                            resources.getString(R.string.multiple_splicing_required),
                                            resources.getString(R.string.splicing_required_multiple_message),
                                            resources.getString(R.string.empty_string),
                                            resources.getString(R.string.ok),
                                            this,
                                            Utility.AlertType.CUT_BIN,
                                            Utility.Iconype.NONE
                                        )
                                    } else {

                                        Utility.getCommonAlertDialogue(
                                            requireActivity(),
                                            resources.getString(R.string.splicing_required),
                                            resources.getString(R.string.splicing_required_first_message),
                                            resources.getString(R.string.empty_string),
                                            resources.getString(R.string.ok),
                                            this,
                                            Utility.AlertType.CUT_BIN,
                                            Utility.Iconype.NONE
                                        )
                                    }
                                }
                            }
                            mWorkspaceEditor?.clearAllSelection()
                            enableMirror(false)
                            enableClear(false)
                            viewModel.clickPatternReference(false)
                            com.ditto.workspace.ui.util.Utility.workspaceItemId.set(
                                com.ditto.workspace.ui.util.Utility.workspaceItemId.get() + 1
                            )
                            viewModel.setImageModel(
                                view, dragEvent, dragData,
                                com.ditto.workspace.ui.util.Utility.workspaceItemId.get()
                            )
                            showToWorkspace(true, true,viewModel.workspacedata)
                        }
                    }
                }

            else -> {
                Log.d("dragger event", "undefined")
            }
        }
        return true
    }

    private fun cutPieces(count: Int?) {
        if (!viewModel.clicked_spliced_second_pieces.get() && viewModel.spliced_pices_visibility.get()) {
            //showSplicingForgetDialogue(Utility.AlertType.DEFAULT)
        } else if (count != null && count > 1 && !viewModel.data.value?.patternPieces?.find { it.id == viewModel.workspacedata?.parentPatternId }?.isCompleted!!) {
            mWorkspaceEditor?.clearAllSelection()
            enableMirror(false)
            showCutBinDialog(count, Utility.AlertType.CUT_BIN)
            cutCount = count
        } else {
            mWorkspaceEditor?.clearAllSelection()
            enableMirror(false)
            if (!viewModel.data.value?.patternPieces?.find { it.id == viewModel.workspacedata?.parentPatternId }?.isCompleted!!) {
                viewModel.data.value?.patternPieces?.find { it.id == viewModel.workspacedata?.parentPatternId }
                    ?.isCompleted = true
                com.ditto.workspace.ui.util.Utility.mPatternPieceList.add(viewModel.workspacedata?.parentPatternId!!)
                adapter?.notifyDataSetChanged()
                viewModel.setCompletedCount(1)
            }
            mWorkspaceEditor?.removePattern(viewModel.workspacedata, true)
            if (mWorkspaceEditor?.views?.size ?: 0 > 0) {
                viewModel.workspacedata = mWorkspaceEditor?.views?.get(0)
            } else {
                viewModel.workspacedata = null
                clearWorkspace()
            }
            onDragCompleted()
        }
    }


    private fun detectTouchedView(view: View) {
        val scale = ScaleAnimation(
            0.5F,
            1F,
            0.5F,
            1F,
            ScaleAnimation.RELATIVE_TO_SELF,
            .5f,
            ScaleAnimation.RELATIVE_TO_SELF,
            .5f
        )
        scale.duration = 1500
        scale.fillAfter = true
        scale.interpolator = OvershootInterpolator()
        view.startAnimation(scale)
    }

    override fun onTouch(view: View, workspaceItem: WorkspaceItems?) {
//        binding.includeWorkspacearea?.layoutSelectAllMask?.visibility = View.GONE
        viewModel.selectAllText.set(getString(R.string.select_all))
        enableSelectAll(true)
        enableClear(true)
        viewModel.workspacedata = workspaceItem
        viewModel.checkMirroring()
    }

    override fun onSelectAll() {
        Log.d("DraggableListener", "onSelectAll")
    }

    override fun onPositionChanged(view: View, workspaceItem: WorkspaceItems?) {
        val id = viewModel.workspacedata?.id
        viewModel.workspacedata = workspaceItem
        // to fix mirroring issue during multi touch
        if (workspaceItem?.id != id) {
            viewModel.checkMirroring()
        }
        logger.d("onpositionchange")

    }

    override fun onDragCompleted() {
        viewModel.isProjectionRequest.set(true)
    }

    override fun onOverlapped(showToast: Boolean) {
        try {
            if (showToast) {
                Utility.showSnackBar(
                    getString(R.string.overlaping_piece_alert),
                    binding?.workspaceRoot
                )
            }
        } catch (e: java.lang.Exception) {
            logger.d("Error : " + e.message)
        }
    }

    override fun onProjectWorkspace() {
        Log.d("DraggableListener", "onProjectWorkspace")
    }

    override fun onDragOut(view: View, workspaceItem: WorkspaceItems?) {
        Log.d("DraggableListener", "onDragOut")
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
                if (findNavController().currentDestination?.id == R.id.workspaceFragment) {
                    showProgress(toShow = true)
                    GlobalScope.launch { projectBorderImage() }
                }
            }
            Utility.AlertType.QUICK_CHECK -> {
                viewModel.isFromQuickCheck.set(false)
                GlobalScope.launch {
                    Utility.sendDittoImage(
                        requireContext(),
                        "solid_black"
                    )
                }
            }
            Utility.AlertType.MIRROR -> {
                if (viewModel.isHorizontalMirror) {
                    mWorkspaceEditor?.flipHorizontal()
                } else {
                    mWorkspaceEditor?.flipVertical()
                }
            }

            Utility.AlertType.CUT_BIN_ALL -> {
                viewModel.isSingleDelete = false
                viewModel.cutAllPiecesConfirmed(mWorkspaceEditor?.views)
            }
            Utility.AlertType.PATTERN_RENAME -> {
                if (baseViewModel.activeSocketConnection.get()) {
                    GlobalScope.launch {
                        Utility.sendDittoImage(
                            requireActivity(),
                            "ditto_project"
                        )
                    }
                }
                viewModel.overridePattern(matchedPattern!!, viewModel.data.value!!, isCompleted)
            }
            Utility.AlertType.CUT_COMPLETE -> {
                adapter?.updatePositionAdapter()
                viewModel.cutCheckBoxClicked(viewModel.cutCount, true)
            }
            else -> {
                Log.d("WorkspaceTabfragment", "onPositiveButtonClicked")
            }
        }

    }

    override fun onNegativeButtonClicked(alertType: Utility.AlertType) {
        when (alertType) {
            Utility.AlertType.BLE -> {
                logger.d("Later clicked")
                baseViewModel.activeSocketConnection.set(false)
                viewModel.isBleLaterClicked.set(true)
            }
            Utility.AlertType.WIFI -> {
                baseViewModel.activeSocketConnection.set(false)
                viewModel.isWifiLaterClicked.set(true)
            }
            Utility.AlertType.CALIBRATION -> {
                viewModel.isFromQuickCheck.set(true)
                sendQuickCheckImage()
            }
            Utility.AlertType.QUICK_CHECK -> {
                viewModel.isFromQuickCheck.set(false)
                GlobalScope.launch { projectBorderImage() }
            }
            Utility.AlertType.CUT_BIN -> {
                mWorkspaceEditor?.removePattern(viewModel.workspacedata, true)
                if (mWorkspaceEditor?.views?.size ?: 0 > 0) {
                    viewModel.workspacedata = mWorkspaceEditor?.views?.get(0)
                } else {
                    viewModel.workspacedata = null
                    clearWorkspace()
                }
                onDragCompleted()
            }
            Utility.AlertType.CUT_BIN_ALL -> {
                viewModel.cutType = Utility.AlertType.CUT_BIN

            }
            Utility.AlertType.PATTERN_RENAME -> {
                showSaveAndExitPopup()
            }
            else -> {
                Log.d("WorkspaceTabfragment", "onNegativeButtonClicked")
            }
        }
    }

    override fun onNeutralButtonClicked(alertType: Utility.AlertType) {
        //      TODO("Not yet implemented")
        // No navigation when SKIP button clicked
        //initiateprojection()
    }

    override fun onSaveButtonClicked(projectName: String, isCompleted: Boolean?) {
        showProgress(true)
        val a = com.ditto.workspace.ui.util.Utility.fragmentTabs.get().toString()
        if (a.equals("0")) {
            viewModel.data.value?.garmetWorkspaceItemOfflines =
                mWorkspaceEditor?.views?.toMutableList()
            logger.d(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> $a")
        } else if (a.equals("1")) {
            viewModel.data.value?.liningWorkspaceItemOfflines =
                mWorkspaceEditor?.views?.toMutableList()
            logger.d(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> $a")
        } else if (a.equals("2")) {
            viewModel.data.value?.interfaceWorkspaceItemOfflines =
                mWorkspaceEditor?.views?.toMutableList()
            logger.d(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> $a")
        }
        this.isCompleted = isCompleted
        viewModel.saveProject()
        context?.let { Utility.setSharedPref(it, viewModel.data.value?.id!!) }
    }

    override fun onExitButtonClicked() {
        moveToLibrary()
    }

    private fun moveToLibrary() {
        if (baseViewModel.activeSocketConnection.get()) {
            GlobalScope.launch {
                Utility.sendDittoImage(
                    requireActivity(),
                    "ditto_project"
                )
            }
        }
        baseViewModel.isSaveExitButtonClicked.set(true)
        findNavController().popBackStack(R.id.patternDescriptionFragment, false)
        findNavController().popBackStack(R.id.nav_graph_mylibrary, false)
        findNavController().navigate(R.id.nav_graph_id_home)

        activity?.onBackPressed()
    }

    private fun enableMirror(status: Boolean) {
        binding.includeWorkspacearea?.txtMirrorV?.alpha = if (status) 1F else 0.5F
        binding.includeWorkspacearea?.txtMirrorH?.alpha = if (status) 1F else 0.5F
        binding.includeWorkspacearea?.txtMirrorV?.isEnabled = status
        binding.includeWorkspacearea?.txtMirrorH?.isEnabled = status
    }

    private fun enableClear(status: Boolean) {
        binding.includeWorkspacearea?.txtClear?.alpha = if (status) 1F else 0.5F
        binding.includeWorkspacearea?.txtClear?.isEnabled = status
    }

    private fun enableSelectAll(status: Boolean) {
        binding.includeWorkspacearea?.txtSelectAll?.alpha = if (status) 1F else 0.5F
        binding.includeWorkspacearea?.txtSelectAll?.isEnabled = status

    }

    private fun showSaveAndExitPopup() {
        baseViewModel.isSaveExitButtonClicked.set(false)
        onSaveButtonClicked(viewModel.data.value?.patternName.toString(), false)
    }

    private fun getScaleFactor() {
        val width: Int = binding.includeWorkspacearea.layoutWorkspace.width ?: 1
        val virtualWidth: Int = 2520
        val x: Double = (virtualWidth.toDouble().div(width.toDouble()))
        viewModel.scaleFactor.set(x)
        Log.d("TAG", "scalefactor : " + viewModel.scaleFactor.get())
    }

    private fun setInitialProgressCount() {
        if (viewModel.tabCategory.equals("Garment")) {
            viewModel.totalPieces.set(viewModel.data.value?.totalNumberOfPieces?.garment ?: 0)
            viewModel.completedPieces.set(viewModel.data.value?.numberOfCompletedPiece?.garment ?: 0)
        } else if (viewModel.tabCategory.equals("Lining")) {
            viewModel.totalPieces.set(viewModel.data.value?.totalNumberOfPieces?.lining ?: 0)
            viewModel.completedPieces.set(viewModel.data.value?.numberOfCompletedPiece?.lining ?: 0)
        } else if (viewModel.tabCategory.equals("Interfacing")) {
            viewModel.totalPieces.set(viewModel.data.value?.totalNumberOfPieces?.`interface` ?: 0)
            viewModel.completedPieces.set(viewModel.data.value?.numberOfCompletedPiece?.`interface` ?: 0)
        }
    }

    /*
    Displaying pieces in Workspace
     */
    private fun showToWorkspace(showProjection: Boolean, isDraggedPiece: Boolean,workspaceItem: WorkspaceItems?) {
        viewModel.spliced_pices_visibility.set(false)
        viewModel.clicked_spliced_second_pieces.set(false)
        if (com.ditto.workspace.ui.util.Utility.isDoubleTapTextVisible.get()) {
            viewModel.showDoubleTouchToZoom.set(true)
        }
        viewModel.selectAllText.set(getString(R.string.select_all))
        enableSelectAll(true)
        mWorkspaceEditor?.clearAllSelection()
        var imagename = workspaceItem?.imagePath
        var imagenameOffline = workspaceItem?.imageName
        if (workspaceItem?.splice ?: false) {
            showSpliceArrows(
                workspaceItem?.currentSplicedPieceRow ?: 0,
                workspaceItem?.currentSplicedPieceColumn ?: 0
            )

            val splicePiece = getSplicePiece(
                workspaceItem?.currentSplicedPieceRow ?: 0,
                workspaceItem?.currentSplicedPieceColumn ?: 0,
                workspaceItem?.splicedImages
            )

            // Setting splice reference layout
            showSpliceReference(splicePiece)
            binding.txtSizeSplice.isEnabled = true
            viewModel.enableSplice.set(true)
            viewModel.clickedSplice.set(true)
            viewModel.clickedSize45.set(false)
            viewModel.clickedSize60.set(false)

            imagename = splicePiece?.imagePath
            imagenameOffline = splicePiece?.imageName
            workspaceItem?.splicedImages?.size?.let {
                viewModel.splice_pices_count.set(
                    it
                )
            }
            viewModel.spliced_pices_visibility.set(true)
        }
        //TODO To be included when using API images
        GlobalScope.launch {
            try {
                showProgress(toShow = true)
                var workSpaceImageData = WorkspaceImageData(
                    if(NetworkUtility.isNetworkAvailable(requireContext())) imagename?.let { getBitmapFromSvgPngDrawable(it) } else imagenameOffline?.let { getBitmapFromSvgPngDrawable(it) },
                    workspaceItem,
                    viewModel.scaleFactor.get(),
                    showProjection,
                    isDraggedPiece
                )


                withContext(Dispatchers.Main) {
                    if (imagename != null) {
                        mWorkspaceEditor?.addImage(
                            workSpaceImageData.bitmap,
                            workSpaceImageData.workspaceItem,
                            workSpaceImageData.scaleFactor,
                            workSpaceImageData.showProjection,
                            workSpaceImageData.isDraggedPiece,
                            this@WorkspaceTabFragment
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                showProgress(toShow = false)
            }
        }

    }


    private fun getBitmapFromSvgPngDrawable(imagePath: String): Bitmap? {
        var availableUri: Uri? = null
        if(!(NetworkUtility.isNetworkAvailable(requireContext()))){
            availableUri = Utility.isImageFileAvailable(imagePath,"${viewModel.data.value?.patternName}")
            Log.d("imageUri123", " availableUri: $availableUri")
        }
        return if (imagePath.endsWith(".svg", true)) {
            Glide
                .with(context)
                .load((if(NetworkUtility.isNetworkAvailable(requireContext())) imagePath else availableUri))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.ic_launcher_background)
                .imageDecoder(SvgBitmapDecoder(context))
                .into(SIZE_ORIGINAL, SIZE_ORIGINAL)
                .get()
        } else if (imagePath.endsWith(".png", true)) {
            Glide
                .with(context)
                .load((if(NetworkUtility.isNetworkAvailable(requireContext())) imagePath else availableUri))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.ic_launcher_background)
                .into(SIZE_ORIGINAL, SIZE_ORIGINAL)
                .get()
        } else {
            getBitmap(getDrawableFromString(context, imagePath) as VectorDrawable, false, false)
        }
    }

    private fun showSpliceReference(spliceImages: SpliceImages?) {
        Log.d("mapImageUrl123","mapImageUrl: ${spliceImages?.mapImageUrl} ")
        spliceImages?.mapImageUrl.let {
            getBitmapFromSvgPngDrawable(
                spliceImages?.mapImageUrl,
                binding.imageSelvageHorizontal.context,
                binding.imageSelvageHorizontal
            )

        }
        viewModel.referenceImage.set(spliceImages?.mapImageUrl)
    }

    private fun showSpliceArrows(row: Int?, column: Int?) {
        if (isSpliceDirectionAvailable(
                row ?: 0,
                column?.plus(1) ?: 0,
                viewModel.workspacedata?.splicedImages
            )
        ) {
            splice_right.bringToFront()
            viewModel.isSpliceRightVisible.set(true)
            viewModel.spliced_pices.set(1)
        }
        if (isSpliceDirectionAvailable(
                row ?: 0,
                column?.minus(1) ?: 0,
                viewModel.workspacedata?.splicedImages
            )
        ) {
            splice_left.bringToFront()
            viewModel.isSpliceLeftVisible.set(true)
            viewModel.spliced_pices.set(2)
            viewModel.clicked_spliced_second_pieces.set(true)
        }
        if (isSpliceDirectionAvailable(
                row?.plus(1) ?: 0,
                column ?: 0,
                viewModel.workspacedata?.splicedImages
            )
        ) {
            splice_top.bringToFront()
            viewModel.isSpliceTopVisible.set(true)
            viewModel.spliced_pices.set(1)
        }
        if (isSpliceDirectionAvailable(
                row?.minus(1) ?: 0,
                column ?: 0,
                viewModel.workspacedata?.splicedImages
            )
        ) {
            splice_bottom.bringToFront()
            viewModel.isSpliceBottomVisible.set(true)
            viewModel.spliced_pices.set(2)
            viewModel.clicked_spliced_second_pieces.set(true) // works only for saved project
        }
    }


    private fun isSpliceDirectionAvailable(
        row: Int,
        column: Int,
        spliceImageList: List<SpliceImages>?
    ): Boolean {
        val isSplicePossible = spliceImageList?.filter {
            it.row == row && it.column == column
        }
        return !isSplicePossible.isNullOrEmpty()
    }

    private fun getSplicePiece(
        row: Int,
        column: Int,
        spliceImageList: List<SpliceImages>?
    ): SpliceImages? {
        val spliceImage = spliceImageList?.filter {
            it.row == row && it.column == column
        }?.get(0)
        return spliceImage
    }

    fun getVirtualWorkspace(): Bitmap {
        val workspaceItems: List<WorkspaceItems> =
            mWorkspaceEditor?.views ?: emptyList()
        val bitmapWidth =
            Math.ceil(
                layout_workspace?.measuredWidth?.times(viewModel.scaleFactor.get())
                    ?: 0.0
            ).toInt()
        val bitmapHeight = bitmapWidth / 14 * 9
        val bigBitmap = Bitmap.createBitmap(
            bitmapWidth, bitmapHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bigBitmap)
        for (workspaceItem in workspaceItems) {
            val imagename: String?
            val imagenameOffline: String?
            if (workspaceItem.splice ?: false) {
                imagename = getSplicePiece(
                    workspaceItem.currentSplicedPieceRow,
                    workspaceItem.currentSplicedPieceColumn,
                    workspaceItem.splicedImages
                )?.imagePath
                imagenameOffline =getSplicePiece(
                    workspaceItem.currentSplicedPieceRow,
                    workspaceItem.currentSplicedPieceColumn,
                    workspaceItem.splicedImages
                )?.imageName
            } else {
                imagename = workspaceItem.imagePath
                imagenameOffline = workspaceItem.imageName
            }
            var bitmap: Bitmap? = null
            runBlocking {
                 val job : Job = GlobalScope.launch {
                    try {
                        bitmap =
                        if(NetworkUtility.isNetworkAvailable(requireContext())) imagename?.let { getBitmapFromSvgPngDrawable(it) } else imagenameOffline?.let { getBitmapFromSvgPngDrawable(it) }
                        withContext(Dispatchers.Main) {
                            if (imagename != null) {
                                val matrix = Matrix()
                                matrix.preTranslate(
                                    workspaceItem.xcoordinate.times(
                                        viewModel.scaleFactor.get().toFloat()
                                    ),
                                    workspaceItem.ycoordinate.times(
                                        viewModel.scaleFactor.get().toFloat()
                                    )
                                )
                                val pivotx =
                                    (bitmap?.width)?.toFloat()?.div(2)
                                val pivoty =
                                    bitmap?.height?.toFloat()?.div(2)

                                matrix.preRotate(
                                    workspaceItem.rotationAngle,
                                    pivotx ?: workspaceItem.pivotX.times(
                                        viewModel.scaleFactor.get().toFloat()
                                    ),
                                    pivoty ?: workspaceItem.pivotY.times(
                                        viewModel.scaleFactor.get().toFloat()
                                    )
                                )
                                bitmap?.let {
                                    canvas.drawBitmap(
                                        it,
                                        matrix,
                                        null
                                    )
                                }
                                matrix.reset()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                job.join()
            }
        }
        return bigBitmap
    }

    /**
     * Variable creations
     */
    companion object {
        private const val PROJECTING_TIME = 10
        private const val PATTERN_CATEGORY = "PatternCategory"
        private const val PATTERN_ID = "PatternId"
        private const val SPLICE_NO = "NO"
        private const val SPLICE_YES = "YES"
        private const val SPLICE_LEFT_TO_RIGHT = "Splice Left-to-Right"
        private const val SPLICE_TOP_TO_BOTTOM = "Splice Top-to-Bottom"
        private const val MULTIPLE_TO_MULTIPLE = "Splice Multiple-to-Multiple"
        private const val REQUEST_CODE_PERMISSIONS = 111
        private const val REQUEST_CODE_PERMISSIONS_DOWNLOAD = 121
        private const val REQUEST_ACTIVITY_RESULT_CODE = 131
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.BLUETOOTH)
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

    private fun showBluetoothDialogue() {
        Utility.getCommonAlertDialogue(
            requireContext(),
            "Connectivity",
            "This app needs Bluetooth connectivity",
            "LATER",
            resources.getString(R.string.turnon),
            this,
            Utility.AlertType.BLE,
            Utility.Iconype.SUCCESS
        )
    }

    private fun showWifiDialogue() {
        Utility.getCommonAlertDialogue(
            requireContext(),
            "Connectivity",
            "This app needs WiFi connectivity",
            "LATER",
            "SETTINGS",
            this,
            Utility.AlertType.WIFI,
            Utility.Iconype.SUCCESS
        )

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
                showConnectivityPopup()
            }
        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
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
            Log.d("onReqPermissionsResult","permission granted")
            val map = getPatternPieceListTailornova()

            if (core.network.NetworkUtility.isNetworkAvailable(requireContext())) {
                bottomNavViewModel.showProgress.set(true)
                viewModel.prepareDowloadList(map)

            }
        }else {
            showSaveAndExitPopup()
            Log.d("onReqPermissionsResult","permission denied")
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

    private fun showCalibrationDialog() {
        val layout =
            activity?.layoutInflater?.inflate(R.layout.alert_calibration_confirmation, null)
        val dialogBuilder =
            AlertDialog.Builder(requireContext())
        dialogBuilder.setCancelable(false)
        val alertCalibration = dialogBuilder.create()
        alertCalibration.setView(layout)
        alertCalibration.show()
        val negative = layout?.findViewById(R.id.textNegative) as TextView
        val positive = layout.findViewById(R.id.textYes) as TextView
        positive.setOnClickListener {
            alertCalibration.dismiss()
            baseViewModel.isCalibrated.set(false)
            viewModel.isWorkspaceIsCalibrated.set(baseViewModel.isCalibrated.get())
        }
        negative.setOnClickListener {
            alertCalibration.dismiss()
            baseViewModel.isCalibrated.set(true)
            viewModel.isWorkspaceIsCalibrated.set(baseViewModel.isCalibrated.get())
        }
    }

    private fun sendBorderImage() {
        if (findNavController().currentDestination?.id == R.id.workspaceFragment) {
            showProgress(toShow = true)
            GlobalScope.launch { projectBorderImage() }
        }
    }

    private fun showQuickCheckDialog() {
        getAlertDialogue(
            requireContext(),
            resources.getString(R.string.setup_quickcheck_title),
            resources.getString(R.string.setup_quickcheck_message),
            resources.getString(R.string.calibrate),
            resources.getString(R.string.yes_string),
            this,
            Utility.AlertType.QUICK_CHECK
        )
    }

    /**
     * [Function] Calibration Button Click
     */
    private fun showcalibrationbuttonclicked() {
        val layout =
            activity?.layoutInflater?.inflate(R.layout.calibration_camera_alert_ws, null)

        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setCancelable(false)
        val alertCamera = dialogBuilder.create()
        alertCamera.setView(layout)
        alertCamera.show()
        val cancel = layout?.findViewById(R.id.textCancel) as TextView
        val launch = layout.findViewById(R.id.textLaunch) as TextView
        launch.setOnClickListener {
            alertCamera.dismiss()
            sendCalibrationPattern()
        }
        cancel.setOnClickListener {
            alertCamera.dismiss()
            if (baseViewModel.activeSocketConnection.get()) {
                GlobalScope.launch { Utility.sendDittoImage(requireActivity(), "solid_black") }
            }
        }
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.getDefaultDisplay().getMetrics(displayMetrics)
        val displayWidth: Int = displayMetrics.widthPixels
        val displayHeight: Int = displayMetrics.heightPixels
        val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alertCamera.window?.attributes)
        val dialogWindowWidth = (displayWidth * 0.8f).toInt()
        val dialogWindowHeight = (displayHeight * 0.6f).toInt()
        layoutParams.width = dialogWindowWidth
        layoutParams.height = dialogWindowHeight
        alertCamera.window?.attributes = layoutParams
    }

    private fun sendCalibrationPattern() {
        logger.d("TRACE_ Projection : sendCalibrationPattern start " + Calendar.getInstance().timeInMillis)
        showProgress(true)
        val bitmap =
            Utility.getBitmapFromDrawable(
                "calibration_pattern",
                requireContext()
            )
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
            .subscribeBy { handleResult(it, true) }
    }

    private fun sendQuickCheckImage() {
        showProgress(true)
        val bitmap =
            Utility.getBitmapFromDrawable(
                "quick_check_pattern",
                requireContext()
            )
        transform(bitmap, false)
    }

    private suspend fun projectBorderImage() {
        withContext(Dispatchers.IO) {
            val bitmap =
                Utility.getBitmapFromDrawable(
                    "setup_pattern_border",
                    requireContext()
                )
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
                    baseViewModel.activeSocketConnection.set(false)
                    baseViewModel.isProjecting.set(false)
                    viewModel.isProjectionRequest.set(false)
                    withContext(Dispatchers.Main) {
                        showProgress(toShow = false)
                        showFailurePopup()
                    }
                }
            } catch (e: Exception) {
                baseViewModel.activeSocketConnection.set(false)
                baseViewModel.isProjecting.set(false)
                viewModel.isProjectionRequest.set(false)
                logger.d("Exception " + e.message)
                withContext(Dispatchers.Main) {
                    showProgress(toShow = false)
                    showFailurePopup()
                }
            } finally {
                soc?.close()
            }
        }
    }

    private fun navigateToCalibration() {
        // Moving to calibration fragment
        findNavController().navigate(
            R.id.action_workspace_to_calibration,
            bundleOf(
                "PatternId" to viewModel.patternId.get(),
                "isFromPatternDescription" to true,
                "isRecalibrate" to true
            )
        )
    }

    private fun navigateToTutorial() {
        val bundle = bundleOf("isFromHome" to true)
        findNavController().navigate(R.id.action_workspace_to_tutorial, bundle)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        logger.d("On Activity Result")
        if (requestCode == REQUEST_ACTIVITY_RESULT_CODE) {
            if (data?.data.toString().equals("success")) {
                showWaitingMessage("Connected to Ditto Projector!!")
                baseViewModel.activeSocketConnection.set(true)
                viewModel.isWorkspaceSocketConnection.set(baseViewModel.activeSocketConnection.get())
                showCalibrationDialog()
                // clear workspace
                GlobalScope.launch {
                    Utility.sendDittoImage(
                        requireContext(),
                        "solid_black"
                    )
                }

            } else {
                baseViewModel.activeSocketConnection.set(false)
                viewModel.isWorkspaceSocketConnection.set(baseViewModel.activeSocketConnection.get())
                logger.d("")
            }
        }
    }

    private fun showFailurePopup() {
        Utility.getCommonAlertDialogue(
            requireActivity(),
            "",
            "Projector Connection failed",
            "CANCEL",
            "RETRY",
            this,
            Utility.AlertType.CONNECTIVITY,
            Utility.Iconype.FAILED
        )

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
            Utility.AlertType.CALIBRATION -> {
                if (findNavController().currentDestination?.id == R.id.workspaceFragment) {
                    showProgress(toShow = true)
                    GlobalScope.launch { projectBorderImage() }
                }
            }
            Utility.AlertType.QUICK_CHECK -> {
                viewModel.isFromQuickCheck.set(false)
                GlobalScope.launch {
                    Utility.sendDittoImage(
                        requireContext(),
                        "solid_black"
                    )
                }
            }
            Utility.AlertType.MIRROR -> {
                if (viewModel.isHorizontalMirror) {
                    mWorkspaceEditor?.flipHorizontal()
                } else {
                    mWorkspaceEditor?.flipVertical()
                }
            }

            Utility.AlertType.CUT_BIN_ALL -> {
                viewModel.isSingleDelete = false
                viewModel.cutAllPiecesConfirmed(mWorkspaceEditor?.views)
            }
            Utility.AlertType.PATTERN_RENAME -> {
                if (baseViewModel.activeSocketConnection.get()) {
                    GlobalScope.launch {
                        Utility.sendDittoImage(
                            requireActivity(),
                            "ditto_project"
                        )
                    }
                }
                viewModel.overridePattern(matchedPattern!!, viewModel.data.value!!, isCompleted)
            }
            Utility.AlertType.CUT_COMPLETE -> {
                adapter?.updatePositionAdapter()
                viewModel.cutCheckBoxClicked(viewModel.cutCount, true)
            }
            Utility.AlertType.MIRROR -> {
                if (viewModel.isHorizontalMirror) {
                    mWorkspaceEditor?.flipHorizontal()
                } else {
                    mWorkspaceEditor?.flipVertical()
                }
            }
            Utility.AlertType.CONNECTIVITY -> {
                viewModel.isWorkspaceSocketConnection.set(baseViewModel.activeSocketConnection.get())
                showConnectivityPopup()
            }
            Utility.AlertType.UPDATEAPIFAILED -> {
                showProgress(false)
                baseViewModel.isSaveExitButtonClicked.set(true)
                findNavController().popBackStack(R.id.patternDescriptionFragment, false)
                activity?.onBackPressed()
            }
        }

    }

    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        when (alertType) {
            Utility.AlertType.BLE -> {
                logger.d("Later clicked")
                baseViewModel.activeSocketConnection.set(false)
                viewModel.isBleLaterClicked.set(true)
            }
            Utility.AlertType.WIFI -> {
                baseViewModel.activeSocketConnection.set(false)
                viewModel.isWifiLaterClicked.set(true)
            }
            Utility.AlertType.CONNECTIVITY -> {
                viewModel.isWorkspaceSocketConnection.set(baseViewModel.activeSocketConnection.get())
            }
            Utility.AlertType.CUT_BIN -> {
                mWorkspaceEditor?.removePattern(viewModel.workspacedata, true)
                if (mWorkspaceEditor?.views?.size ?: 0 > 0) {
                    viewModel.workspacedata = mWorkspaceEditor?.views?.get(0)
                } else {
                    viewModel.workspacedata = null
                    clearWorkspace()
                }
                onDragCompleted()
            }
            Utility.AlertType.CUT_BIN_ALL -> {
                viewModel.cutType = Utility.AlertType.CUT_BIN

            }

        }
    }

    fun resetWorkspaceUI() {
        setConnectButton()
    }

    private fun setConnectButton() {
        viewModel.isWorkspaceSocketConnection.set(baseViewModel.activeSocketConnection.get())
        viewModel.isWorkspaceIsCalibrated.set(baseViewModel.isCalibrated.get())
        if (baseViewModel.isSetUpError.get()) {
            baseViewModel.isSetUpError.set(false)
            viewModel.onClickRecalibrate()
        }
    }


    private fun getBitmapFromSvgPngDrawable(
        imagePath: String?,
        context: Context,
        imageView: ImageView
    ) {
        if (imagePath?.endsWith(".svg", true)!!) {
            Glide
                .with(context)
                .load(imagePath)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.ic_placeholder)
                .imageDecoder(SvgBitmapDecoder(context))
                .into(imageView)

        } else if (imagePath.endsWith(".png", true)) {
            Glide
                .with(context)
                .load(imagePath)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.ic_placeholder)
                .into(imageView)
        } else {
            imageView.setImageDrawable(
                Utility.getDrawableFromString(context, imagePath) as VectorDrawable,
            )
        }
    }
}