package com.ditto.mylibrary.ui

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.ditto.mylibrary.domain.MyLibraryUseCase
import com.ditto.mylibrary.domain.model.PatternIdData
import com.ditto.mylibrary.domain.model.ProdDomain
import core.PDF_PASSWORD
import core.PDF_USERNAME
import core.event.UiEvents
import core.network.NetworkUtility
import core.ui.BaseViewModel
import core.ui.common.Utility
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PatternDescriptionViewModel @Inject constructor(
    private val context: Context,
    val utility: Utility,
    private val getPattern: MyLibraryUseCase
) :
    BaseViewModel() {
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    val isShowindicator: ObservableBoolean = ObservableBoolean(true)

    //    val clickedTailornovaID: ObservableField<String> = ObservableField("30644ba1e7aa41cfa9b17b857739968a")
    val clickedTailornovaID: ObservableField<String> = ObservableField("")
    var clickedOrderNumber: ObservableField<String> = ObservableField("")//todo
    var data: MutableLiveData<PatternIdData> = MutableLiveData()
    val patternName: ObservableField<String> = ObservableField("")
    val isFromDeepLinking: ObservableBoolean = ObservableBoolean(false)
    val patternpdfuri: ObservableField<String> = ObservableField("")
    val patternDescription: ObservableField<String> = ObservableField("")
    val patternStatus: ObservableField<String> = ObservableField("")

    val isFinalPage: ObservableBoolean = ObservableBoolean(false)
    val isStartingPage: ObservableBoolean = ObservableBoolean(true)
    val resumeOrSubscription: ObservableField<String> = ObservableField("RESUME")
    val isSubscriptionExpired: ObservableBoolean = ObservableBoolean(false)
    val isStatusLayoutVisible: ObservableBoolean = ObservableBoolean(false)
    val showActive: ObservableBoolean = ObservableBoolean(false)
    val showPurchased: ObservableBoolean = ObservableBoolean(false)
    val showLine: ObservableBoolean = ObservableBoolean(false)
    val showResumButton: ObservableBoolean = ObservableBoolean(false)
    val showWorkspaceOrRenewSubscriptionButton: ObservableBoolean = ObservableBoolean(false)
    val isDataReceived: ObservableBoolean = ObservableBoolean(false)
    val patternUri: ObservableField<String> = ObservableField("")
    val imagesToDownload = hashMapOf<String, String>()
    val temp = ArrayList<String>()

    //error handler for data fetch related flow
    private fun handleError(error: Error) {
        when (error) {
            is NoNetworkError -> activeInternetConnection.set(false)
            else -> {
                uiEvents.post(Event.OnDataloadFailed)
            }
        }
    }

    //fetch data from offline
    fun fetchOfflinePatternDetails() {
        //disposable += getPattern.getOfflinePatternById("30644ba1e7aa41cfa9b17b857739968a")
        disposable += getPattern.getOfflinePatternById(clickedTailornovaID.get().toString())
            .delay(600, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleOfflineFetchResult(it) }
    }

    private fun handleOfflineFetchResult(result: Result<PatternIdData>) {
        when (result) {
            is Result.OnSuccess -> {
                data.value = result.data
                uiEvents.post(Event.OnDataUpdated)
            }
            is Result.OnError -> handleError(result.error)
        }
    }

    fun fetchPattern() {
        //disposable += getPattern.getPattern("30644ba1e7aa41cfa9b17b857739968a")
        disposable += getPattern.getPattern(clickedTailornovaID.get() ?: "")
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }

    private fun handleFetchResult(result: Result<PatternIdData>) {
        when (result) {
            is Result.OnSuccess -> {
                data.value = result.data
                uiEvents.post(Event.OnDataUpdated)
            }
            is Result.OnError -> handleError(result.error)
        }
    }

    /**
     * [Function] ViewPager Previous Button Click
     */
    fun onClickWorkSpace() {
        if (resumeOrSubscription.get().toString() == "RENEW SUBSCRIPTION") {
            uiEvents.post(Event.onSubscriptionClicked)
        } else if (resumeOrSubscription.get().toString() == "WORKSPACE") {
            uiEvents.post(Event.OnWorkspaceButtonClicked)
        } else {
            uiEvents.post(Event.OnWorkspaceButtonClicked)
        }
    }


    fun onClickInstructions() {
        uiEvents.post(Event.OnInstructionsButtonClicked)
    }

    fun onFinished() {
        uiEvents.post(Event.OnDownloadComplete)
    }

    /**
     * Events for this view model
     */
    sealed class Event {

        object OnWorkspaceButtonClicked : Event()
        object onSubscriptionClicked : Event()
        object OnInstructionsButtonClicked : Event()
        object OnDataUpdated : Event()
        object OnDownloadComplete : Event()
        object OnDataloadFailed : Event()
        object OnImageDownloadComplete : Event()
        object OnNoNetworkToDownloadImage : Event()
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
            //If the pdf hosted site is to be authorized.
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
            //result = convertInputStreamToFile(inputStream,filename,patternFolderName?.replace(" ",""))
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

        val filename =
            "${patternFolderName.toString().replace("[^A-Za-z0-9 ]".toRegex(), "") + ".pdf"}"
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
            Log.d("Error", "", e)
        }
    }

    fun prepareDowloadList(hashMap: HashMap<String, String>) {
        Log.d("Download", ">>>>>>>>>>>>>>>>>>>> STARTED")
        Log.d("Download", "Hashmap size: ${hashMap?.size}")
        Log.d("Download", "Hashmap size: ${hashMap}")
        temp.clear()
        if (!hashMap.isEmpty()) {
            if (NetworkUtility.isNetworkAvailable(context)) {
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
                    uiEvents.post(Event.OnImageDownloadComplete)
                }
            } else {
                uiEvents.post(Event.OnNoNetworkToDownloadImage)
            }
        } else {
            uiEvents.post(Event.OnImageDownloadComplete)
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
            Log.d("DOWNLOAD", "key: $filename patternUri : ${patternUri.get()}")
            temp.add(path.toString())
        }
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

    fun imageFilesToDownload(hashMap: HashMap<String, String>): HashMap<String, String> {
        imagesToDownload.clear()
        hashMap.forEach { (key, value) ->
            if (!(key.isNullOrEmpty())) {
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
        }
        return imagesToDownload
    }

    fun versionCheck() {
        utility.checkVersion()
    }
}