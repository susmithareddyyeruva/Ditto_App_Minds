package com.ditto.mylibrary.ui

import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.ditto.mylibrary.domain.GetMylibraryData
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
import javax.inject.Inject

class PatternDescriptionViewModel @Inject constructor(private val getPattern: GetMylibraryData) :
    BaseViewModel() {
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    val isShowindicator: ObservableBoolean = ObservableBoolean(true)
    val clickedID: ObservableInt = ObservableInt(1)
    var data: MutableLiveData<MyLibraryData> = MutableLiveData()
    val patternName: ObservableField<String> = ObservableField("")
    val patternpdfuri: ObservableField<String> = ObservableField("")
    val patternDescription: ObservableField<String> = ObservableField("")
    val patternStatus: ObservableField<String> = ObservableField("")

    val isFinalPage: ObservableBoolean = ObservableBoolean(false)
    val isStartingPage: ObservableBoolean = ObservableBoolean(true)


    init {

    }

    //error handler for data fetch related flow
    private fun handleError(error: Error) {
        when (error) {
            is NoNetworkError -> activeInternetConnection.set(false)
            else -> {
                Log.d("error","Error undefined")
            }
        }
    }

    //fetch data from repo (via usecase)
    /* fun fetchOnPatternData() {
         disposable += getPattern.invoke()
             .whileSubscribed { it }
             .subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribeBy { handleFetchResult(it) }
     }*/

    fun fetchPattern() {
        disposable += getPattern.getPattern(clickedID.get())
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }

    private fun handleFetchResult(result: Result<MyLibraryData>) {
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
        uiEvents.post(Event.OnWorkspaceButtonClicked)
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

        object OnInstructionsButtonClicked : Event()

        object OnDataUpdated : Event()

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

    private fun convertInputStreamToFile(inputStream: InputStream,filename: String): File? {
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