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
import com.ditto.mylibrary.domain.GetMylibraryData
import com.ditto.mylibrary.domain.model.PatternIdData
import com.ditto.mylibrary.domain.model.ProdDomain
import com.ditto.mylibrary.domain.MyLibraryUseCase
import com.ditto.mylibrary.domain.model.MyLibraryData
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PatternDescriptionViewModel @Inject constructor(private val context: Context,
                                                      private val getPattern: MyLibraryUseCase) :
    BaseViewModel() {
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    val isShowindicator: ObservableBoolean = ObservableBoolean(true)
    val clickedTailornovaID: ObservableField<String> = ObservableField("demo-design-id-png")
    var clickedOrderNumber: ObservableField<String> = ObservableField("")//todo
    var data: MutableLiveData<PatternIdData> = MutableLiveData()
    val patternName: ObservableField<String> = ObservableField("")
    val patternpdfuri: ObservableField<String> = ObservableField("")
    val patternDescription: ObservableField<String> = ObservableField("")
    val patternStatus: ObservableField<String> = ObservableField("")

    val isFinalPage: ObservableBoolean = ObservableBoolean(false)
    val isStartingPage: ObservableBoolean = ObservableBoolean(true)
    val resumeOrSubscription : ObservableField<String> =ObservableField("RESUME")
    val isSubscriptionExpired: ObservableBoolean = ObservableBoolean(false)
    val isStatusLayoutVisible: ObservableBoolean= ObservableBoolean(false)
    val showActive: ObservableBoolean = ObservableBoolean(false)
    val showPurchased: ObservableBoolean = ObservableBoolean(false)
    val showLine: ObservableBoolean = ObservableBoolean(false)
    val showResumButton: ObservableBoolean = ObservableBoolean(false)
    val showWorkspaceOrRenewSubscriptionButton: ObservableBoolean = ObservableBoolean(false)
    val isDataReceived: ObservableBoolean = ObservableBoolean(false)

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
    fun fetchOfflinePatterns() {
        disposable += getPattern.getOfflinePatternById("demo-design-id-png")
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
        disposable += getPattern.getPattern("demo-design-id-png")
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
        if(resumeOrSubscription.get().toString() == "RENEW SUBSCRIPTION"){
            uiEvents.post(Event.onSubscriptionClicked)
        }else if(resumeOrSubscription.get().toString()=="WORKSPACE"){
            uiEvents.post(Event.OnWorkspaceButtonClicked)
        }else{
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun downloadPDF(url: String, filename: String, patternFolderName: String?){
        performtask(url,filename,patternFolderName)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun performtask(url: String, filename: String, patternFolderName: String?){

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
            if(inputStream != null)
                //result = convertInputStreamToFile(inputStream,filename,patternFolderName?.replace(" ",""))
                result = convertInputStreamToFile(inputStream,filename,patternFolderName)
            val path = Uri.fromFile(result)
            patternpdfuri.set(path.toString())
            onFinished()
        }
    }

    private fun convertInputStreamToFile(
        inputStream: InputStream,
        filename: String, patternFolderName: String?
    ): File? {
        var result : File? = null
        val outputFile : File? = null
        var dittofolder : File? = null

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
}