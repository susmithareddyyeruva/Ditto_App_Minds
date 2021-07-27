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
import com.ditto.workspace.domain.GetWorkspaceData
import com.ditto.workspace.domain.model.*
import com.ditto.workspace.ui.util.Utility
import core.PDF_PASSWORD
import core.PDF_USERNAME
import core.event.UiEvents
import core.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

class WorkspaceViewModel @Inject constructor(
    private val context: Context,
    private val getWorkspaceData: GetWorkspaceData
) : BaseViewModel() {

    var allPatterns: MutableLiveData<List<PatternsData>> = MutableLiveData()
    var data: MutableLiveData<PatternsData> = MutableLiveData()
    var userData: MutableLiveData<LoginUser> = MutableLiveData()
    private val dbLoadError: ObservableBoolean = ObservableBoolean(false)
    var patternId: ObservableInt = ObservableInt(1)
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
    val patternpdfuri: ObservableField<String> = ObservableField("")
    val isBleLaterClicked: ObservableBoolean = ObservableBoolean(false)
    val isWifiLaterClicked: ObservableBoolean = ObservableBoolean(false)

    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    var isHorizontalMirror: Boolean = false
    var cutCount: Int = 0
    var cutPiecePosition: Int = 0
    var isSingleDelete: Boolean = false
    var cutType: core.ui.common.Utility.AlertType = core.ui.common.Utility.AlertType.CUT_BIN

    //fetch data from repo (via usecase)
    fun fetchWorkspaceData() {
        disposable += getWorkspaceData.invoke()
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }

    //fetch data from API repo (via usecase)
    fun fetchWorkspaceDataFromAPI() {
        disposable += getWorkspaceData.getWorkspaceData()
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResultFromAPI(it) }
    }


    fun updateWSAPI(cTraceWorkSpacePatternInputData: CTraceWorkSpacePatternInputData){
        disposable += getWorkspaceData.updateWorkspaceData(cTraceWorkSpacePatternInputData)//calling update api
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy{handleWSUpdateResult(it)}
    }

    fun createWSAPI(cTraceWorkSpacePatternInputData: CTraceWorkSpacePatternInputData){
        disposable += getWorkspaceData.createWorkspaceData(cTraceWorkSpacePatternInputData)//calling update api
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy{handleWSInsertResult(it)}
    }

    //todo
    fun insertData(value: PatternsData) {
        disposable += getWorkspaceData.insert(value)
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleInsertDataResult(it) }
    }

    private fun handleFetchResultFromAPI(result: Result<WorkspaceResultDomain>) {
        Log.d("handleFethFromAPI", "is:\t ${result.toString()}")
        when(result) {
            is Result.OnSuccess -> {
                Log.d("WorkspaceViewModel", "Success")

            }

            is Result.OnError -> {
                // create workspace
                Log.d("WorkspaceViewModel", "Failed")
            }
        }
    }


    private fun handleWSUpdateResult(result: Result<WSUpdateResultDomain>) {
        Log.d("handleUpdateFromAPI", "is:\t ${result.toString()}")
        when(result) {
            is Result.OnSuccess -> {
                Log.d("WorkspaceViewModel456", "Success>>>>>>>>>>>>>>>>>>> $result")

                val c:CTraceWorkSpacePatternInputData=getWorkspaceInputDataToAPI()

                val wsData = WorkspaceDataAPI(c.tailornaovaDesignId,c.selectedTab,
                    c.status,c.numberOfCompletedPiece,
                    c.patternPieces,c.garmetWorkspaceItems,
                    c.liningWorkspaceItems,c.interfaceWorkspaceItem)

                updateWSPatternDataStorage(3,"UpdateTAB","DoneUpdate123",c.numberOfCompletedPiece,
                    c.patternPieces,c.garmetWorkspaceItems,
                    c.liningWorkspaceItems,c.interfaceWorkspaceItem)//todo need to add tailernova details
            }

            is Result.OnError -> {
                Log.d("WorkspaceViewModel", "Failed")
            }
        }
    }

 private fun handleWSInsertResult(result: Result<WSUpdateResultDomain>) {
        Log.d("handleUpdateFromAPI", "is:\t ${result.toString()}")
        when(result) {
            is Result.OnSuccess -> {
                Log.d("WorkspaceViewModel456", "Success>>>>>>>>>>>>>>>>>>> $result")

                val c: CTraceWorkSpacePatternInputData = getWorkspaceInputDataToAPI()

                val wsData = WorkspaceDataAPI(
                    c.tailornaovaDesignId, c.selectedTab,
                    c.status, c.numberOfCompletedPiece,
                    c.patternPieces, c.garmetWorkspaceItems,
                    c.liningWorkspaceItems, c.interfaceWorkspaceItem
                )

                insertWSAPIDataToDB(wsData)//todo need to add tailernova details
            }

            is Result.OnError -> {
                Log.d("WorkspaceViewModel", "Failed")
            }
        }
    }

    fun updateWSPatternDataStorage(
        tailornaovaDesignId: Int,
        selectedTab: String,
        status: String,
        numberOfCompletedPiece: NumberOfPieces,
        patternPieces: List<PatternPieceDomain>,
        garmetWorkspaceItems: List<WorkspaceItemDomain>,
        liningWorkspaceItems: List<WorkspaceItemDomain>,
        interfaceWorkspaceItem: List<WorkspaceItemDomain>
    ){
        disposable += getWorkspaceData.updateOfflineStorageData(tailornaovaDesignId, selectedTab,status,
            numberOfCompletedPiece,patternPieces,garmetWorkspaceItems,liningWorkspaceItems,interfaceWorkspaceItem)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy{handleWSPatternDataStorage(it)}
    }

    private fun handleWSPatternDataStorage(result: Any?) {
        when (result) {
            is Result.OnSuccess<*> -> {
                Log.d("handleUpdateDataResult", "OnSuccess")
                uiEvents.post(Event.OnClickSaveAndExit)

            }
        }
        uiEvents.post(Event.OnClickSaveAndExit)//todo check
    }


    fun insertWSAPIDataToDB(value:WorkspaceDataAPI){
        disposable += getWorkspaceData.insertWorkspaceData(value)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy{handleWSInsertResultDataToDB(it)}
    }

    private fun handleWSInsertResultDataToDB(result: Any?) {
        when (result) {
            is Result.OnSuccess<*> -> {
                Log.d("handleUpdateDataResult", "OnSuccess")
                uiEvents.post(Event.OnClickSaveAndExit)

            }
        }
    }

    fun fetchWorkspaceSettingData(){
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

    private fun handleInsertDataResult(result: Any) {
        when (result) {
            is Result.OnSuccess<*> -> {
                Log.d("handleInsertDataResult", "OnSuccess")
            }
        }
        uiEvents.post(Event.CloseScreen)
    }

    private fun handleFetchResult(result: Result<List<PatternsData>>) {
        when (result) {
            is Result.OnSuccess -> {
                allPatterns.value = result.data
                data.value = result.data.find { it.id == patternId.get() }
//                workspacedata.value = data.value?.workspaceItems?.get(0)
                activeInternetConnection.set(true)
                uiEvents.post(Event.CalculateScrollButtonVisibility)
                uiEvents.post(Event.OnDataUpdated)
                setWorkspaceView()
            }
            is Result.OnError -> handleError(result.error)
        }
    }

    //error handler for data fetch related flow
    private fun handleError(error: Error) {
        when (error) {
            is NoNetworkError -> activeInternetConnection.set(false)
            else -> {
                Log.d("handleError", "WorkspaceViewModel")
            }
        }
    }

    fun setWorkspaceView() {
        if (data.value?.workspaceItems?.size ?: 0 > 0) {
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
        clickedSplice.set(true)
        clickedSize45.set(false)
        clickedSize60.set(false)
        uiEvents.post(Event.OnClickInch)
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
            dragData.patternPieces?.size ?: "",
            dragData.patternPieces?.view ?: "",
            dragData.patternPieces?.pieceNumber ?: "",
            dragData.patternPieces?.pieceDescription ?: "",
            dragData.patternPieces?.positionInTab ?: "",
            dragData.patternPieces?.tabCategory ?: "",
            dragData.patternPieces?.cutQuantity ?: "",
            dragData.patternPieces?.splice ?: "",
            dragData.patternPieces?.spliceDirection ?: "",
            dragData.patternPieces?.spliceScreenQuantity ?: "",
            dragData.patternPieces?.splicedImages ?: emptyList(),
            dragData.patternPieces?.cutOnFold ?: "",
            dragData.patternPieces?.mirrorOption ?: "",
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
        val totalCount = Utility.progressCount.get() + progress
        data.value?.completedPieces = totalCount
        Log.d("TRACE", "Setting progress")
        Utility.progressCount.set(totalCount)
        uiEvents.post(Event.updateProgressCount)
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

    fun cutSelectAll(workspaceItems: List<WorkspaceItems>) {
        cutCount = 0
        cutType = core.ui.common.Utility.AlertType.CUT_BIN_ALL
        for (workspaceItem in workspaceItems.distinctBy { it.parentPatternId }) {
            if (!(data.value?.patternPieces?.find { it.id == workspaceItem?.parentPatternId }?.isCompleted
                    ?: false)
            ) {
                cutCount += workspaceItem?.cutQuantity?.get(4)
                    ?.let { Character.getNumericValue(it) }
            }
        }
        if (cutCount > 1) {
            uiEvents.post(Event.ShowCutBinDialog)
        } else {
            cutAllPiecesConfirmed(workspaceItems)
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

    fun onPaternItemCheckboxClicked(){
        cutType = core.ui.common.Utility.AlertType.CUT_COMPLETE
        uiEvents.post(Event.ShowCutBinDialog)
    }


    fun cutIndividualPiecesConfirmed(workspaceItems: WorkspaceItems, cutCount: Int) {
        cutType = core.ui.common.Utility.AlertType.CUT_BIN
        println("TRACE: Setting progress")
        Utility.progressCount.set(Utility.progressCount.get() + cutCount)
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
        Utility.progressCount.set(Utility.progressCount.get() + cutCount)
        workspaceItems?.forEach { workspaceItem ->
            if (!workspaceItem?.isCompleted) {
                data.value?.patternPieces?.find { it.id == workspaceItem.parentPatternId }
                    ?.isCompleted = true
                Utility.mPatternPieceList.add(workspaceItem.parentPatternId)
            }
            workspacedata = workspaceItem
        }
        uiEvents.post(Event.RemoveAllPatternPieces)
        uiEvents.post(Event.updateProgressCount)
    }


    fun cutCheckBoxClicked(count: Int?, isChecked : Boolean) {
        if (isChecked){
            Utility.progressCount.set(Utility.progressCount.get() + count!!)
        } else {
            Utility.progressCount.set(Utility.progressCount.get() - count!!)
        }
    }

    fun clearPatternsSelected() {
        data?.value?.patternPieces?.forEach { workspaceItem ->
            if (workspaceItem?.isCompleted) {
                workspaceItem?.isCompleted = false
            }
        }
    }

    fun clickReset() {
        clearPatternsSelected()
        uiEvents.post(Event.OnResetClicked)
    }

    fun saveProject(projectName: String, isCompleted: Boolean?) {
        if (data.value?.status.equals("New")) {
            data.value?.status = "Active"
            data.value?.id = System.currentTimeMillis().toInt()
            data.value?.patternName = projectName
            data.value?.completedPieces = Utility.progressCount.get()
            data.value?.selectedTab = Utility.fragmentTabs.get().toString()
        } else {
            data.value?.patternName = projectName
            data.value?.completedPieces = Utility.progressCount.get()
            data.value?.selectedTab = Utility.fragmentTabs.get().toString()
        }
        if (isCompleted != null && isCompleted) {
            data.value?.status = "Completed"
        }
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
        Log.d(
            "Coordinates",
            "toSavedProject : " + data.value?.workspaceItems
        )
        insertData(data.value!!) //todoshri

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
            .subscribeBy { handleInsertDataResult(it) }
    }

    fun checkMirroring() {
        if (workspacedata?.mirrorOption.equals("YES")) {
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
        updateWSAPI(getWorkspaceInputDataToAPI())
        //createWSAPI(getWorkspaceInputDataToAPI())
       // uiEvents.post(Event.OnClickSaveAndExit)
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun downloadPDF(url: String, filename: String, patternFolderName: String?) {
        performtask(url, filename,patternFolderName)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun performtask(url: String, filename: String, patternFolderName: String?) {

        withContext(Dispatchers.IO) {

            val userCredentials: String = "$PDF_USERNAME:$PDF_PASSWORD"
            val inputStream: InputStream
            var result: File? = null
            val url: URL = URL(url)
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            val basicAuth =
                "Basic " + String(Base64.getEncoder().encode(userCredentials.toByteArray()))
            conn.setRequestProperty("Authorization", basicAuth)
            conn.requestMethod = "GET"
            conn.connect()
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                patternpdfuri.set("")
                onFinished()
                return@withContext
            }
            inputStream = conn.inputStream
            if (inputStream != null)
                result = convertInputStreamToFile(inputStream, filename,patternFolderName)
            val path = Uri.fromFile(result)
            patternpdfuri.set(path.toString())
            onFinished()
        }
    }

    private fun convertInputStreamToFile(inputStream: InputStream, filename: String, patternFolderName: String?): File? {
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
       code to create foler with pattern name
       val file = File(dittofolder, "/${patternFolderName.toString().replace("[^A-Za-z0-9 ]".toRegex(), "")}/Pattern Instruction")
        file.mkdirs()*/

        if (!dittofolder.exists()) {
            dittofolder.mkdir()
        }

        val filename = "${patternFolderName.toString().replace("[^A-Za-z0-9 ]".toRegex(), "")+".pdf"}"

        result = File(dittofolder, filename)
        if (!result.exists()) {
            result.createNewFile()
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
            Log.d("Error","",e)
        }
    }

    private fun getWorkspaceInputDataToAPI(): CTraceWorkSpacePatternInputData {
        val numberOfCompletedPiece =
            NumberOfPieces(garment = 400, lining = 13, interfacee = 13)

        var patternPieces=mutableListOf<PatternPieceDomain>()
        val patternInputData = PatternPieceDomain(id = 11, isCompleted = "true")
        patternPieces.add(patternInputData)
        val patternInputData2 = PatternPieceDomain(id = 21, isCompleted = "true")
        patternPieces.add(patternInputData2)


        val garmetWorkspaceItems: ArrayList<WorkspaceItemDomain> = ArrayList()

        val garmentWorkspaceItemInputData = WorkspaceItemDomain(id=11,patternPiecesId = 11,
            isCompleted = "true",xcoordinate = "0.10",ycoordinate = "0.10",pivotX = "1",pivotY = "2",
            transformA = "1",transformD = "1",rotationAngle = "10",isMirrorH ="true",isMirrorV = "10",
            showMirrorDialog = "true",currentSplicedPieceNo = "2")

        val garmentWorkspaceItemInputData1 = WorkspaceItemDomain(id=1,patternPiecesId = 12,
            isCompleted = "true",xcoordinate = "0.10",ycoordinate = "0.10",pivotX = "1",pivotY = "2",
            transformA = "1",transformD = "1",rotationAngle = "10",isMirrorH ="true",isMirrorV = "10",
            showMirrorDialog = "true",currentSplicedPieceNo = "22")
        garmetWorkspaceItems.add(garmentWorkspaceItemInputData)
        garmetWorkspaceItems.add(garmentWorkspaceItemInputData1)

        val liningWorkspaceItems:ArrayList<WorkspaceItemDomain> = ArrayList()

        val liningWorkspaceItemInputData = WorkspaceItemDomain(id=12,patternPiecesId = 12,
            isCompleted = "true",xcoordinate = "0.10",ycoordinate = "0.10",pivotX = "1",pivotY = "2",
            transformA = "1",transformD = "1",rotationAngle = "10",isMirrorH ="true",isMirrorV = "10",
            showMirrorDialog = "true",currentSplicedPieceNo = "2")
        liningWorkspaceItems.add(liningWorkspaceItemInputData)

        val interfaceWorkspaceItem: ArrayList<WorkspaceItemDomain> = ArrayList()

        val interfaceWorkspaceItemInputData = WorkspaceItemDomain(id=1,patternPiecesId = 1,
            isCompleted = "true",xcoordinate = "0.10",ycoordinate = "0.100",pivotX = "122",pivotY = "112",
            transformA = "1",transformD = "1",rotationAngle = "10",isMirrorH ="true",isMirrorV = "10",
            showMirrorDialog = "true",currentSplicedPieceNo = "2")
        interfaceWorkspaceItem.add(interfaceWorkspaceItemInputData)

        val cTraceWorkSpacePatternInputData = CTraceWorkSpacePatternInputData(tailornaovaDesignId =3 ,selectedTab ="ABC" ,status = "DONE",
            numberOfCompletedPiece = numberOfCompletedPiece,patternPieces,garmetWorkspaceItems = garmetWorkspaceItems,
            liningWorkspaceItems = liningWorkspaceItems,interfaceWorkspaceItem =interfaceWorkspaceItem)

        return cTraceWorkSpacePatternInputData
    }
}

