package com.ditto.workspace.ui

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
import com.ditto.workspace.domain.GetWorkspaceData
import com.ditto.workspace.domain.model.DragData
import com.ditto.workspace.domain.model.PatternsData
import com.ditto.workspace.domain.model.WorkspaceItems
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
    private val getWorkspaceData: GetWorkspaceData
) : BaseViewModel() {

    var allPatterns: MutableLiveData<List<PatternsData>> = MutableLiveData()
    var data: MutableLiveData<PatternsData> = MutableLiveData()
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
    val clickedSize45: ObservableBoolean = ObservableBoolean(true)
    val isLastItemVisible: ObservableBoolean = ObservableBoolean(false)
    val isFirstItemVisible: ObservableBoolean = ObservableBoolean(false)
    val isScrollButtonVisible: ObservableBoolean = ObservableBoolean(false)
    val isStartedProjection: ObservableBoolean = ObservableBoolean(false)
    val isProjectionRequest: ObservableBoolean = ObservableBoolean(false)
    val isFromQuickCheck: ObservableBoolean = ObservableBoolean(false)

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
    var isSingleDelete: Boolean = false
    var cutType: core.ui.common.Utility.AlertType =core.ui.common.Utility.AlertType.CUT_BIN

    //fetch data from repo (via usecase)
    fun fetchWorkspaceData() {
        disposable += getWorkspaceData.invoke()
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }

    fun insertData(value: PatternsData) {
        disposable += getWorkspaceData.insert(value)
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleInsertDataResult(it) }
    }

    private fun handleInsertDataResult(result: Any) {
        when (result) {
            is Result.OnSuccess<*> -> {
                Log.d("handleInsertDataResult","OnSuccess")
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
                Log.d("handleError","WorkspaceViewModel")
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
        clickedSize45.set(isSize45)
        uiEvents.post(Event.OnClickInch)
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
        Log.d("TRACE","Setting progress")
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
        if (selectAllText.get().equals("Select All")){
            uiEvents.post(Event.OnClickSelectAll)
            uiEvents.post(Event.DisableMirror)
        } else {
            uiEvents.post(Event.OnClickDeSelectAll)
        }
    }

    fun cutSelectAll(workspaceItems: List<WorkspaceItems>) {
        cutCount = 0
        cutType = core.ui.common.Utility.AlertType.CUT_BIN_ALL
        for (workspaceItem in workspaceItems.distinctBy { it.parentPatternId }) {
            if(!(data.value?.patternPieces?.find { it.id == workspaceItem?.parentPatternId }?.isCompleted?:false)){
                cutCount += workspaceItem?.cutQuantity?.get(4)
                    ?.let { Character.getNumericValue(it) }
            }
        }
        if (cutCount > 1) {
            uiEvents.post(Event.ShowCutBinDialog)
        }else{
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
            cutIndividualPiecesConfirmed(workspaceItems,1)
        }
    }


    fun cutIndividualPiecesConfirmed(workspaceItems: WorkspaceItems, cutCount :Int) {
        cutType = core.ui.common.Utility.AlertType.CUT_BIN
        println("TRACE: Setting progress")
        Utility.progressCount.set( Utility.progressCount.get() + cutCount)
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
        Utility.progressCount.set( Utility.progressCount.get() + cutCount)
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
        if(data.value?.completedPieces == data.value?.totalPieces) {
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
            "toSavedProject : " +data.value?.workspaceItems
        )
        insertData(data.value!!)
    }

    fun overridePattern(
        oldPatternsData: PatternsData,
        patternsData: PatternsData,
        isCompleted: Boolean?
    ) {
        data.value?.patternName = oldPatternsData.patternName
        if(data.value?.status == "New") {
            data.value?.status = if((Utility.progressCount.get() == data.value?.totalPieces)) "Completed" else "Active"
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

    fun clickSaveAndExit() {
        uiEvents.post(Event.OnClickSaveAndExit)
    }

    fun onClickInstructions() {
        uiEvents.post(Event.OnClickPatternInstructions)
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

        object CalculateScrollButtonVisibility : Event()
        object OnDataUpdated : Event()
        object OnClickInch : Event()
        object OnClickSelectAll : Event()
        object OnClickDeSelectAll : Event()
        object EnableMirror : Event()
        object DisableMirror : Event()
        object OnClickMirrorHorizontal : Event()
        object OnClickMirrorVertical : Event()
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
    suspend fun downloadPDF(url : String, filename: String){
        performtask(url,filename)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun performtask(url: String, filename : String){

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
            if(inputStream != null)
                result = convertInputStreamToFile(inputStream,filename)
            val path = Uri.fromFile(result)
            patternpdfuri.set(path.toString())
            onFinished()
        }
    }

    private fun convertInputStreamToFile(inputStream: InputStream, filename: String): File? {
        var result : File? = null
        val outputFile : File? = null
        var dittofolder : File? = null
        dittofolder = File(
            Environment.getExternalStorageDirectory().toString() + "/" + "Ditto"
        )
        if (!dittofolder.exists()) {
            dittofolder.mkdir()
        }
        result = File(dittofolder, filename)
        if (!result.exists()) {
            result.createNewFile()
        }
        result.copyInputStreamToFile(inputStream)
        return result
    }

    private fun File.copyInputStreamToFile(inputStream: InputStream) {
        this.outputStream().use { fileOut ->
            inputStream.copyTo(fileOut)
        }
    }
}

