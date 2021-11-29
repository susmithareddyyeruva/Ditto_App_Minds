package com.ditto.workspace.ui

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.DragEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableDouble
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.ditto.login.domain.model.LoginUser
import com.ditto.workspace.data.error.GetWorkspaceApiFetchError
import com.ditto.workspace.data.mapper.*
import com.ditto.workspace.domain.GetWorkspaceData
import com.ditto.workspace.domain.model.*
import com.ditto.workspace.ui.util.Utility
import core.PDF_PASSWORD
import core.PDF_USERNAME
import core.appstate.AppState
import core.event.UiEvents
import core.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import non_core.lib.Result
import non_core.lib.error.Error
import non_core.lib.error.NoNetworkError
import non_core.lib.whileSubscribed
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class WorkspaceViewModel @Inject constructor(
    private val context: Context,
    private val getWorkspaceData: GetWorkspaceData,
    private val utility: core.ui.common.Utility,
) : BaseViewModel() {

    var allPatterns: MutableLiveData<List<PatternsData>> = MutableLiveData()
    var data: MutableLiveData<PatternsData> = MutableLiveData()
    var userData: MutableLiveData<LoginUser> = MutableLiveData()
    private val dbLoadError: ObservableBoolean = ObservableBoolean(false)
    var patternId: ObservableField<String> = ObservableField("")
    var clickedOrderNumber: ObservableField<String> = ObservableField("")
    var patternName: ObservableField<String> = ObservableField("")
    var mannequinId: ObservableField<String> = ObservableField("")
    var totalPieces: ObservableInt = ObservableInt(0)
    var completedPieces: ObservableInt = ObservableInt(0)
    var workspacedata: WorkspaceItems? = null
    var tabCategory: String = ""
    var selectedTab: ObservableInt = ObservableInt(0)
    var scaleFactor: ObservableDouble = ObservableDouble(1.0)
    val totalItemCount: ObservableInt = ObservableInt(0)
    val lastItemVisible: ObservableInt = ObservableInt(0)
    val firstItemVisible: ObservableInt = ObservableInt(0)
    val spliced_pices: ObservableInt = ObservableInt(0)
    val splice_pices_count: ObservableInt = ObservableInt(0)
    val spliced_pices_visibility: ObservableBoolean = ObservableBoolean(false)
    val clicked_spliced_second_pieces: ObservableBoolean = ObservableBoolean(false)
    val clickedPattenPieces: ObservableBoolean = ObservableBoolean(true)
    val clickedSize45: ObservableBoolean = ObservableBoolean(true)
    val enableSize45: ObservableBoolean = ObservableBoolean(false)
    val clickedSize60: ObservableBoolean = ObservableBoolean(false)
    val enableSize60: ObservableBoolean = ObservableBoolean(false)
    val clickedSplice: ObservableBoolean = ObservableBoolean(false)
    val enableSplice: ObservableBoolean = ObservableBoolean(false)
    val isLastItemVisible: ObservableBoolean = ObservableBoolean(false)
    val isFirstItemVisible: ObservableBoolean = ObservableBoolean(false)
    val isScrollButtonVisible: ObservableBoolean = ObservableBoolean(false)
    val isStartedProjection: ObservableBoolean = ObservableBoolean(false)
    val isProjectionRequest: ObservableBoolean = ObservableBoolean(false)
    val isFromQuickCheck: ObservableBoolean = ObservableBoolean(false)

    val showDoubleTouchToZoom: ObservableBoolean = ObservableBoolean(false)
    var referenceImage: ObservableField<String> = ObservableField("")
    var calibrationText: ObservableField<String> = ObservableField("")
    var selectAllText: ObservableField<String> = ObservableField("Select All")
    val isSpliceRightVisible: ObservableBoolean = ObservableBoolean(false)
    val isSpliceLeftVisible: ObservableBoolean = ObservableBoolean(false)
    val isSpliceTopVisible: ObservableBoolean = ObservableBoolean(false)
    val isSpliceBottomVisible: ObservableBoolean = ObservableBoolean(false)
    val isWorkspaceSocketConnection: ObservableBoolean = ObservableBoolean(false)
    val isWorkspaceIsCalibrated: ObservableBoolean = ObservableBoolean(false)
    val patternpdfuri: ObservableField<String> = ObservableField("")
    val isBleLaterClicked: ObservableBoolean = ObservableBoolean(false)
    val isWifiLaterClicked: ObservableBoolean = ObservableBoolean(false)
    val isWorkspaceShownCoachMark: ObservableBoolean = ObservableBoolean(false)
    val patternUri: ObservableField<String> = ObservableField("")
    val temp = ArrayList<String>()
    val imagesToDownload = hashMapOf<String, String>()
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    var isHorizontalMirror: Boolean = false
    var cutCount: Int = 0
    var cutPiecePosition: Int = 0
    var isSingleDelete: Boolean = false
    var isCompleteButtonClickable: Boolean = true
    var cutType: core.ui.common.Utility.AlertType = core.ui.common.Utility.AlertType.CUT_BIN

    //Fetching tailornova details from offline_pattern_data table
    fun fetchTailernovaDetails(id: String) {
        disposable += getWorkspaceData.getTailernovaDataByID(id)
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleTailernovaResult(it) }
    }

    //fetch data from SFCC server  //CustomerID_OrderNumebr_PatternID
    fun fetchWorkspaceDataFromAPI(result: Result.OnSuccess<OfflinePatternData>, id: String) {
        disposable += getWorkspaceData.getWorkspaceData(id)
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResultFromAPI(it, result) }
    }


    fun updateWSAPI(workspaceDataAPI: WorkspaceDataAPI) {
        disposable += getWorkspaceData.updateWorkspaceData(
            "${AppState.getCustID()}_${clickedOrderNumber.get()}_${patternId.get()}_${mannequinId.get()}",
            workspaceDataAPI
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleWSUpdateResult(it) }
    }

    fun updateWorkspaceDB(
        tailornaovaDesignId: String?,
        selectedTab: String?,
        status: String?,
        numberOfCompletedPiece: NumberOfPieces?,
        patternPieces: List<PatternPieceSFCCAPI>?,
        garmetWorkspaceItems: MutableList<WorkspaceItemDomain>?,
        liningWorkspaceItems: MutableList<WorkspaceItemDomain>?,
        interfaceWorkspaceItems: MutableList<WorkspaceItemDomain>?,
        workspaceDataAPI: WorkspaceDataAPI
    ) {
        disposable += getWorkspaceData.updateOfflineStorageData(
            tailornaovaDesignId,
            selectedTab,
            status,
            numberOfCompletedPiece,
            patternPieces,
            garmetWorkspaceItems,
            liningWorkspaceItems,
            interfaceWorkspaceItems
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleWSPatternDataStorage(it, workspaceDataAPI) }
    }

    fun createWSAPI(workspaceDataAPI: WorkspaceDataAPI) {
        disposable += getWorkspaceData.createWorkspaceData(
            "${AppState.getCustID()}_${clickedOrderNumber.get()}_${
                patternId.get()}_${mannequinId.get()}", workspaceDataAPI
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleWSCreateResult(it) }
    }

    fun insertData(value: PatternsData, closeScreen: Boolean) {
        disposable += getWorkspaceData.insert(value)
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleInsertDataResult(it, closeScreen) }
    }


    private fun handleWSUpdateResult(result: Result<WSUpdateResultDomain>) {
        Log.d("handleUpdateFromAPI", "is:\t ${result.toString()}")
        when (result) {
            is Result.OnSuccess -> {
                Log.d("handleWSUpdateResult", "update api Success>>>>>>>>>>>>>>>>>>> $result")
                uiEvents.post(Event.CloseScreen)
            }

            is Result.OnError -> {
                uiEvents.post(Event.ApiFailed)
                Log.d("WorkspaceViewModel", "update api Failed")
            }
        }
    }

    private fun handleWSCreateResult(result: Result<WSUpdateResultDomain>) {
        Log.d("handleUpdateFromAPI", "is:\t ${result.toString()}")
        when (result) {
            is Result.OnSuccess -> {
                Log.d("WorkspaceViewModel456", "Success>>>>>>>>>>>>>>>>>>> $result")
            }

            is Result.OnError -> {
                Log.d("WorkspaceViewModel", "Failed")
            }
        }
    }

    private fun handleWSPatternDataStorage(
        result: Any?,
        workspaceDataAPI: WorkspaceDataAPI
    ) {
        Log.d("handlWSPattenDtaStorage", "${result.toString()}")
        if (result == 1) {
            Log.d("handlWSPattenDtaStorage", "Success update storage")
            updateWSAPI(workspaceDataAPI)
        } else {
            uiEvents.post(Event.ApiFailed)
            Log.d("handlWSPattenDtaStorage", "failed update storage")
        }
    }


    fun insertWSAPIDataToDB(value: WorkspaceDataAPI) {
        disposable += getWorkspaceData.insertWorkspaceData(value)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleWSInsertResultDataToDB(it) }
    }

    private fun handleWSInsertResultDataToDB(result: Any?) {
        when (result) {
            is Result.OnSuccess<*> -> {
                Log.d("handleUpdateDataResult", "OnSuccess")
                uiEvents.post(Event.OnClickSaveAndExit)

            }
            is Result.OnError<*> -> handleError(result.error)
        }
    }

    fun fetchWorkspaceSettingData() {
        disposable += getWorkspaceData.getUserDetails()
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleWorkspacesSettingResult(it) }
    }

    private fun handleWorkspacesSettingResult(result: Result<LoginUser>?) {
        when (result) {
            is Result.OnSuccess -> {
                userData.value = result.data
            }

            is Result.OnError -> {
                Log.d("WSProSettingViewModel", "Failed")
            }
        }
    }

    private fun handleInsertDataResult(result: Any, closeScreen: Boolean) {
        when (result) {
            is Result.OnSuccess<*> -> {
                Log.d("handleInsertDataResult", "OnSuccess")
            }
            is Result.OnError<*> -> handleError(result.error)
        }
        if (closeScreen) {
            uiEvents.post(Event.CloseScreen)
        }
    }

    private fun handleFetchResult(result: Result<List<PatternsData>>) {
        when (result) {
            is Result.OnSuccess -> {
                allPatterns.value = result.data
                data.value = result.data.find { it.id == patternId.get().toString() }
                Log.d("WorkspaceViewModel098", "Combine patternsData: data.value >>${data.value} ")
                activeInternetConnection.set(true)
                uiEvents.post(Event.CalculateScrollButtonVisibility)
                uiEvents.post(Event.OnDataUpdated)
                setWorkspaceView()
            }
            is Result.OnError -> handleError(result.error)
        }
    }


    private fun handleFetchResultFromAPI(
        fetchWorkspaceResult: Result<WorkspaceDataAPI>,
        tailornovaResult: Result.OnSuccess<OfflinePatternData>
    ) {
        when (fetchWorkspaceResult) {
            is Result.OnSuccess -> {
                data.value = combineTailornovaAndSFCCDetails(tailornovaResult, fetchWorkspaceResult)
                activeInternetConnection.set(true)
                uiEvents.post(Event.HideProgressLoader)
                setWorkspaceView()
            }

            is Result.OnError -> {
                handleError(fetchWorkspaceResult.error)
                Log.d("handleFetchResultAPI", "Failed")
                data.value = combineTailornovaAndSFCCDetails(tailornovaResult)
                setWorkspaceView()
                uiEvents.post(Event.HideProgressLoader)
            }
        }
    }

    private fun handleTailernovaResult(result: Result<OfflinePatternData>?) {
        when (result) {
            is Result.OnSuccess -> {
                if (AppState.getIsLogged()) {
                    // Fetching workspace data from SFCC server
                    fetchWorkspaceDataFromAPI(
                        result,
                        "${AppState.getCustID()}_${clickedOrderNumber.get()}_${patternId.get()}_${mannequinId.get()}"
                    )
                } else {
                    data.value = combineTailornovaAndSFCCDetails(result)
                    setWorkspaceView()
                    uiEvents.post(Event.HideProgressLoader)
                }
                Log.d("WorkspaceViewModel", "Tailernova Success $result")
            }
            is Result.OnError -> handleError(result.error)
        }
    }

    //error handler for data fetch related flow
    fun handleError(error: Error) {
        uiEvents.post(Event.HideProgressLoader)
        when (error) {
            is NoNetworkError -> activeInternetConnection.set(false)
            is GetWorkspaceApiFetchError -> {
                if (error.message.contains("key", true)) {
                    Log.d("handleError", "WorkspaceViewModel >>>>>>>>>>>>>>>>>>>createWSAPI ")
                    createWSAPI(getWorkspaceInputDataToAPI(setWorkspaceDimensions(data.value)))
                }
            }
            else -> {
                Log.d("handleError", "WorkspaceViewModel else")
            }
        }
    }

    fun setWorkspaceView() {
        uiEvents.post(Event.CalculateScrollButtonVisibility)
        uiEvents.post(Event.OnDataUpdated)
        if (data.value?.garmetWorkspaceItemOfflines?.size ?: 0 > 0 || data.value?.liningWorkspaceItemOfflines?.size ?: 0 > 0 || data.value?.interfaceWorkspaceItemOfflines?.size ?: 0 > 0) {
            uiEvents.post(Event.PopulateWorkspace)
        }
    }

    fun projectWorkspace() {
        uiEvents.post(Event.onProject)
    }

    fun clickSize(isSize45: Boolean) {
        clickedSplice.set(false)
        clickedSize45.set(isSize45)
        clickedSize60.set(!isSize45)
        uiEvents.post(Event.OnClickInch)
    }

    fun clickSplice() {
        if (enableSplice.get()) {
            clickedSplice.set(true)
            clickedSize45.set(false)
            clickedSize60.set(false)
            uiEvents.post(Event.OnClickInch)
        }
    }

    fun clickPatternReference(isPattern: Boolean) {
        clickedPattenPieces.set(isPattern)
        uiEvents.post(Event.OnClickPatternOrReference)
    }

    fun setImageModel(view: View, dragEvent: DragEvent, dragData: DragData, id: Int) {
        workspacedata = WorkspaceItems(
            id,
            dragData.patternPieces?.parentPattern ?: "",
            dragData.patternPieces?.imagePath ?: "",
            dragData.patternPieces?.imageName ?: "",
            dragData.patternPieces?.size ?: "",
            dragData.patternPieces?.view ?: "",
            dragData.patternPieces?.pieceNumber ?: "",
            dragData.patternPieces?.pieceDescription ?: "",
            dragData.patternPieces?.positionInTab ?: "",
            dragData.patternPieces?.tabCategory ?: "",
            dragData.patternPieces?.cutQuantity ?: "",
            dragData.patternPieces?.splice ?: false,
            //dragData.patternPieces?.spliceDirection ?: 0,
            dragData.patternPieces?.spliceScreenQuantity ?: "",
            dragData.patternPieces?.splicedImages ?: emptyList(),
            dragData.patternPieces?.cutOnFold ?: "",
            dragData.patternPieces?.isMirrorOption ?: false,
            dragEvent.x,
            dragEvent.y,
            view.pivotX,
            view.pivotY,
            view.rotation,
            view.rotationX == 180F,
            view.rotationY == 180F,
            true,
            dragData.patternPieces?.id ?: 0
        )
    }

    fun setCompletedCount(progress: Int) {
        completedPieces.set(completedPieces.get().plus(progress))
        setCompletePieceCount()
        uiEvents.post(Event.updateProgressCount)
    }

    fun setCompletePieceCount() {
        if (tabCategory.equals("Garment")) {
            data.value?.numberOfCompletedPiece?.garment = completedPieces.get()
        } else if (tabCategory.equals("Lining")) {
            data.value?.numberOfCompletedPiece?.lining = completedPieces.get()
        } else if (tabCategory.equals("Interfacing")) {
            data.value?.numberOfCompletedPiece?.`interface` = completedPieces.get()
        }
        isCompleteButtonClickable = true
    }

    fun clickScrollLeft() {
        uiEvents.post(Event.CalculateScrollButtonVisibility)
        uiEvents.post(Event.OnClickScrollLeft)
    }

    fun onClickRecalibrate() {
        uiEvents.post(Event.OnRecalibrateClicked)
    }

    fun clickScrollRight() {
        uiEvents.post(Event.CalculateScrollButtonVisibility)
        uiEvents.post(Event.OnClickScrollRight)
    }

    fun clickSelectAll() {
        if (selectAllText.get().equals("Select All")) {
            uiEvents.post(Event.OnClickSelectAll)
            uiEvents.post(Event.DisableMirror)
            uiEvents.post(Event.EnableClear)
        } else {
            uiEvents.post(Event.OnClickDeSelectAll)
            uiEvents.post(Event.DisableClear)
        }
    }

    fun cutIndividualPieces(workspaceItems: WorkspaceItems) {
        cutCount = 0
        cutType = core.ui.common.Utility.AlertType.CUT_BIN
        cutCount = workspaceItems?.cutQuantity?.get(4)
            ?.let { Character.getNumericValue(it) }
        if (cutCount > 1 && data.value?.patternPieces?.find { it.id == workspacedata?.parentPatternId }?.isCompleted!!) {
            uiEvents.post(Event.ShowCutBinDialog)
        } else {
            cutIndividualPiecesConfirmed(workspaceItems, 1)
        }
    }

    fun onPaternItemCheckboxClicked() {
        cutType = core.ui.common.Utility.AlertType.CUT_COMPLETE
        uiEvents.post(Event.ShowCutBinDialog)
    }


    fun cutIndividualPiecesConfirmed(workspaceItems: WorkspaceItems, cutCount: Int) {
        cutType = core.ui.common.Utility.AlertType.CUT_BIN
        println("TRACE: Setting progress")
        completedPieces.set(completedPieces.get() + cutCount)
        setCompletePieceCount()
        if (!data.value?.patternPieces?.find { it.id == workspacedata?.parentPatternId }?.isCompleted!!) {
            data.value?.patternPieces?.find { it.id == workspacedata?.parentPatternId }
                ?.isCompleted = true
        }
        uiEvents.post(Event.RemoveAllPatternPieces)
        uiEvents.post(Event.updateProgressCount)
    }

    fun cutAllPiecesConfirmed(workspaceItems: List<WorkspaceItems>?) {
        cutType = core.ui.common.Utility.AlertType.CUT_BIN
        println("TRACE: Setting progress")
        completedPieces.set(completedPieces.get() + cutCount)
        setCompletePieceCount()
        workspaceItems?.forEach { workspaceItem ->
            if (!workspaceItem?.isCompleted) {
                data.value?.patternPieces?.find { it.id == workspaceItem.parentPatternId }
                    ?.isCompleted = true
                workspaceItem.parentPatternId?.let { Utility.mPatternPieceList.add(it) }
            }
            workspacedata = workspaceItem
        }
        uiEvents.post(Event.RemoveAllPatternPieces)
        uiEvents.post(Event.updateProgressCount)
    }


    fun cutCheckBoxClicked(count: Int?, isChecked: Boolean) {
        if (isChecked) {
            completedPieces.set(completedPieces.get() + count!!)
            setCompletePieceCount()
        } else {
            completedPieces.set(completedPieces.get() - count!!)
            setCompletePieceCount()
        }
    }

    fun clearPatternsSelected() {
        data?.value?.patternPieces?.filter { it.tabCategory == tabCategory }?.toMutableList()
            ?.forEach { workspaceItem ->
                if (workspaceItem?.isCompleted) {
                    workspaceItem?.isCompleted = false
                }
            }
    }

    fun clickReset() {
        clearPatternsSelected()
        uiEvents.post(Event.OnResetClicked)
    }

    fun saveProject() {
        data.value?.completedPieces = Utility.progressCount.get()
        data.value?.selectedTab = Utility.fragmentTabs.get().toString()

        if (data.value?.completedPieces == data.value?.totalPieces) {
            data.value?.status = "Completed"
        }
        loop1@ for (patternPiecesId in data.value?.patternPieces!!) {
            loop2@ for (mPatternPieceListID in Utility.mPatternPieceList) {
                if (patternPiecesId.id == mPatternPieceListID) {
                    patternPiecesId.isCompleted = true
                    break@loop2
                }
            }
        }

        var cTraceWorkSpacePatternInputData =
            getWorkspaceInputDataToAPI(setWorkspaceDimensions(data.value))

        updateWorkspaceDB(
//            "30644ba1e7aa41cfa9b17b857739968a",
            cTraceWorkSpacePatternInputData.tailornaovaDesignId,
            cTraceWorkSpacePatternInputData.selectedTab,
            cTraceWorkSpacePatternInputData.status,
            cTraceWorkSpacePatternInputData.numberOfCompletedPiece,
            cTraceWorkSpacePatternInputData.patternPieces,
            cTraceWorkSpacePatternInputData.garmetWorkspaceItems,
            cTraceWorkSpacePatternInputData.liningWorkspaceItems,
            cTraceWorkSpacePatternInputData.interfaceWorkspaceItems, cTraceWorkSpacePatternInputData
        )

    }

    // Set workspace Dimensions to Virtual
    fun setWorkspaceDimensions(value: PatternsData?): PatternsData? {
        val patternsData = value
        setWorkspaceVirtualDimensions(
            patternsData?.garmetWorkspaceItemOfflines
                ?: emptyList()
        )
        setWorkspaceVirtualDimensions(
            patternsData?.liningWorkspaceItemOfflines
                ?: emptyList()
        )
        setWorkspaceVirtualDimensions(
            patternsData?.interfaceWorkspaceItemOfflines
                ?: emptyList()
        )
        return patternsData
    }


    // Set workspace Dimensions to Virtual
    fun setWorkspaceVirtualDimensions(workspaceItems: List<WorkspaceItems>): List<WorkspaceItems> {
        for (workspaceItem in workspaceItems) {
            workspaceItem.xcoordinate =
                workspaceItem.xcoordinate?.times(scaleFactor.get().toFloat())
            workspaceItem.ycoordinate =
                workspaceItem.ycoordinate?.times(scaleFactor.get().toFloat())
            workspaceItem.pivotX = workspaceItem.pivotX?.times(scaleFactor.get().toFloat())
            workspaceItem.pivotY = workspaceItem.pivotY?.times(scaleFactor.get().toFloat())
        }
        return workspaceItems
    }

    // Set workspace Dimensions to Virtual
    fun getWorkspaceDimensions(workspaceItems: List<WorkspaceItems>?): List<WorkspaceItems>? {
        if (workspaceItems != null) {
            for (workspaceItem in workspaceItems) {
                workspaceItem.xcoordinate =
                    workspaceItem.xcoordinate?.div(scaleFactor.get().toFloat())
                workspaceItem.ycoordinate =
                    workspaceItem.ycoordinate?.div(scaleFactor.get().toFloat())
                workspaceItem.pivotX = workspaceItem.pivotX?.div(scaleFactor.get().toFloat())
                workspaceItem.pivotY = workspaceItem.pivotY?.div(scaleFactor.get().toFloat())
            }
        }
        return workspaceItems
    }

    fun overridePattern(
        oldPatternsData: PatternsData,
        patternsData: PatternsData,
        isCompleted: Boolean?
    ) {
        data.value?.patternName = oldPatternsData.patternName
        if (data.value?.status == "New") {
            data.value?.status =
                if ((Utility.progressCount.get() == data.value?.totalPieces)) "Completed" else "Active"
            data.value?.id = oldPatternsData.id
        }
        data.value?.completedPieces = Utility.progressCount.get()
        if (isCompleted != null && isCompleted) {
            data.value?.status = "Completed"
        }
        disposable += getWorkspaceData.deleteAndInsert(oldPatternsData.id, patternsData)
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleInsertDataResult(it, true) }
    }

    fun checkMirroring() {
        if (workspacedata?.mirrorOption == true) {
            uiEvents.post(Event.EnableMirror)
        } else {
            uiEvents.post(Event.DisableMirror)
        }
    }

    fun clickMirrorH() {
        if (workspacedata?.showMirrorDialog != false) {
            uiEvents.post(Event.ShowMirrorDialog)
            workspacedata?.showMirrorDialog = false
            isHorizontalMirror = true
        } else {
            uiEvents.post(Event.OnClickMirrorHorizontal)
        }
    }

    fun clickMirrorV() {
        if (workspacedata?.showMirrorDialog != false) {
            uiEvents.post(Event.ShowMirrorDialog)
            workspacedata?.showMirrorDialog = false
            isHorizontalMirror = false
        } else {
            uiEvents.post(Event.OnClickMirrorVertical)
        }
    }

    fun clickClear() {
        uiEvents.post(Event.OnClickClear)
    }

    fun clickSaveAndExit() {
        uiEvents.post(Event.OnClickSaveAndExit)
    }

    fun onClickInstructions() {
        uiEvents.post(Event.OnClickPatternInstructions)
    }

    fun onClickTutorial() {
        uiEvents.post(Event.OnClickTutorial)
    }

    fun onClickSpliceRight() {
        uiEvents.post(Event.OnClickSpliceRight)
        isSpliceLeftVisible.set(true)
        isSpliceRightVisible.set(false)
    }

    fun onClickSpliceLeft() {
        uiEvents.post(Event.OnClickSpliceLeft)
        isSpliceLeftVisible.set(false)
        isSpliceRightVisible.set(true)
    }

    fun onClickSpliceTop() {
        uiEvents.post(Event.OnClickSpliceTop)
        isSpliceTopVisible.set(false)
        isSpliceBottomVisible.set(true)
    }

    fun onClickSpliceBottom() {
        uiEvents.post(Event.OnClickSpliceBottom)
        isSpliceTopVisible.set(true)
        isSpliceBottomVisible.set(false)
    }

    fun coachMarkClose() {
        uiEvents.post(Event.OnCoachmarkClose)
    }

    fun coachMarkPlay() {
        uiEvents.post(Event.OnCoachmarkPlay)
    }

    fun onFinished() {
        uiEvents.post(Event.OnDownloadComplete)
    }

    sealed class Event {
        /**
         * Event emitted by [events] when the data received successfully
         */
        object OnClickScrollLeft : Event()

        /**
         * Event emitted by [events] when Right Scroll Button clicked
         */
        object OnClickScrollRight : Event()

        /**
         * Event emitted by [events] when Save And Exit clicked
         */
        object OnClickSaveAndExit : Event()

        /**
         * Event emitted by [events] when instructions clicked
         */
        object OnClickPatternInstructions : Event()
        object OnClickTutorial : Event()
        object OnResetClicked : Event()
        object OnClickPatternOrReference : Event()

        object CalculateScrollButtonVisibility : Event()
        object OnDataUpdated : Event()
        object OnClickInch : Event()
        object OnClickSelectAll : Event()
        object OnClickDeSelectAll : Event()
        object EnableMirror : Event()
        object DisableMirror : Event()
        object DisableClear : Event()
        object EnableClear : Event()
        object DisableSelectAll : Event()
        object EnableSelectAll : Event()
        object OnClickMirrorHorizontal : Event()
        object OnClickMirrorVertical : Event()
        object OnClickClear : Event()
        object OnClickSpliceRight : Event()
        object OnClickSpliceLeft : Event()
        object OnClickSpliceTop : Event()
        object OnClickSpliceBottom : Event()
        object OnRecalibrateClicked : Event()
        object ClearWorkspace : Event()
        object ShowMirrorDialog : Event()
        object CloseScreen : Event()
        object PopulateWorkspace : Event()
        object onProject : Event()
        object ShowCutBinDialog : Event()
        object RemoveAllPatternPieces : Event()
        object updateProgressCount : Event()
        object OnDownloadComplete : Event()
        object OnCoachmarkPlay : Event()
        object OnCoachmarkClose : Event()
        object HideProgressLoader : Event()
        object ShowProgressLoader : Event()
        object ApiFailed : Event()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun downloadPDF(url: String, filename: String, patternFolderName: String?) {
        performtask(url, filename, patternFolderName)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun performtask(url: String, filename: String, patternFolderName: String?) {

        withContext(Dispatchers.IO) {

            val userCredentials: String = "$PDF_USERNAME:$PDF_PASSWORD"
            val inputStream: InputStream
            var result: File? = null
            val url: URL = URL(url)
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            /*val basicAuth =
                "Basic " + String(Base64.getEncoder().encode(userCredentials.toByteArray()))
            conn.setRequestProperty("Authorization", basicAuth)*/
            conn.requestMethod = "GET"
            conn.connect()
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                patternpdfuri.set("")
                onFinished()
                return@withContext
            }
            inputStream = conn.inputStream
            if (inputStream != null)
                result = convertInputStreamToFile(inputStream, filename, patternFolderName)
            val path = Uri.fromFile(result)
            patternpdfuri.set(path.toString())
            onFinished()
        }
    }

    private fun convertInputStreamToFile(
        inputStream: InputStream,
        filename: String, patternFolderName: String?
    ): File? {
        var result: File? = null
        val outputFile: File? = null
        var dittofolder: File? = null

        val contextWrapper = ContextWrapper(context)

        dittofolder = File(
            Environment.getExternalStorageDirectory().toString() + "/" + "Ditto"
        )

        // uncomment following line to save file in internal app memory
        //dittofolder = contextWrapper.getDir("DittoPattern", Context.MODE_PRIVATE)

        /*
        code for creating folder with pattern name
        val file = File(dittofolder, "/${patternFolderName.toString().replace("[^A-Za-z0-9 ]".toRegex(), "")+".pdf"}")
        file.mkdirs()*/

        if (!dittofolder.exists()) {
            dittofolder.mkdir()
        }

//        val filename =
//            "${patternFolderName.toString().replace("[^A-Za-z0-9 ]".toRegex(), "") + ".pdf"}"
        val filename =
            "${patternFolderName.toString()}.pdf"
        result = File(dittofolder, filename)
        if (!result.exists()) {
            result.createNewFile()
        }
        result.copyInputStreamToFile(inputStream)
        return result
    }


    private fun convertInputStreamToFileForPatterns(
        inputStream: InputStream,
        filename: String,
        patternFolderName: String?
    ): File? {
        var result: File? = null
        var dittofolder: File? = null
        var subFolder: File? = null
        dittofolder = File(
            Environment.getExternalStorageDirectory().toString() + "/" + "Ditto"
        )

        subFolder = File(dittofolder, "/${patternFolderName}")

        if (!dittofolder.exists()) {
            dittofolder.mkdir()
            if (!subFolder.exists()) {
                subFolder.mkdirs()
            }
        } else {
            if (!subFolder.exists()) {
                subFolder.mkdirs()
            } else {
                Log.d("Ditto Folder", "${patternFolderName}PRESENT IN DIRECTORY")
            }
        }

        result = File(subFolder, filename)
        if (!result.exists()) {
            try {
                result.createNewFile()
            } catch (e: Exception) {
            }
        }
        result.copyInputStreamToFile(inputStream)
        return result
    }


    private fun File.copyInputStreamToFile(inputStream: InputStream) {
        try {
            this.outputStream().use { fileOut ->
                inputStream.copyTo(fileOut)
            }
        } catch (e: Exception) {
            Log.d("Error", "", e)
        }
    }

    fun prepareDowloadList(hashMap: HashMap<String, String>) {
        Log.d("DOWNLOAD", "prepareDowloadList >>>>>>>>>>>>>>>>>>>> STARTED")
        Log.d("Download", "Hashmap size: ${hashMap?.size}")
        temp.clear()
        if (!hashMap.isEmpty()) {
            GlobalScope.launch {

                runBlocking {
                    hashMap.forEach { (key, value) ->
                        Log.d("DOWNLOAD", "file not present KEY: $key \t VALUE : $value")
                        if (!(key.isNullOrEmpty()) && !(value.isNullOrEmpty())) {
                            downloadEachPatternPiece(
                                imageUrl = value,
                                filename = key,
                                patternFolderName = patternName.get() ?: "Pattern Piece"
                            )
                        }
                    }
                }
                uiEvents.post(Event.OnDownloadComplete)
            }
        } else {
            uiEvents.post(Event.OnDownloadComplete)
        }
    }

    suspend fun downloadEachPatternPiece(
        imageUrl: String,
        filename: String,
        patternFolderName: String?
    ) {
        withContext(Dispatchers.IO) {
            val inputStream: InputStream
            var result: File? = null
            val url: URL = URL(imageUrl)
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connect()
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                patternpdfuri.set("")
                return@withContext
            }
            inputStream = conn.inputStream
            if (inputStream != null)
                result =
                    convertInputStreamToFileForPatterns(inputStream, filename, patternFolderName)
            val path = Uri.fromFile(result)
            patternUri.set(path.toString())
            Log.d("PATTERN", patternUri.get() ?: "")

            temp.add(path.toString())
        }
    }

    fun imageFilesToDownload(hashMap: HashMap<String, String>): HashMap<String, String> {
        imagesToDownload.clear()
        hashMap.forEach { (key, value) ->
            val availableUri = key.let {
                core.ui.common.Utility.isImageFileAvailable(
                    it,
                    "${patternName.get()}"
                )
            }
            if (availableUri == null) {
                imagesToDownload.put(key, value)
            }
        }
        return imagesToDownload
    }
}