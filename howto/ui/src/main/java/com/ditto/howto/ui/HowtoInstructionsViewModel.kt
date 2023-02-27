package com.ditto.howto.ui

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableField
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import core.event.UiEvents
import core.ui.BaseViewModel
import core.ui.common.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class HowtoInstructionsViewModel @Inject constructor(
    private val context: Context,
    val utility: Utility,
) : BaseViewModel() {
    val patternpdfuri: ObservableField<String> = ObservableField("")
    var toolbarTitle: ObservableField<String> = ObservableField("")

    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(HowtoInstructionsViewModel::class.java.simpleName)
    }
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()

    sealed class Event {
        object OnDownloadComplete : Event()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun downloadPDF(url: String, filename: String) {
        performTask(url, filename)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun performTask(url: String, filename: String) {
        try {
            withContext(Dispatchers.IO) {
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
                    result = convertInputStreamToFile(inputStream, filename)
                val path = Uri.fromFile(result)
                patternpdfuri.set(path.toString())
                onFinished()
            }
        } catch (e: Exception) {
            logger.d("PatternDescriptionViMol, ${e.message}")
        }
    }

    private fun convertInputStreamToFile(
        inputStream: InputStream,
        filename: String,
    ): File? {
        var result: File? = null
        var dittofolder: File? = null
        dittofolder = if (Build.VERSION.SDK_INT >= 30) {
            File(
                context?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                    .toString() + "/" + "Tutorial"
            )
        } else {
            File(
                Environment.getExternalStorageDirectory().toString() + "/" + "Tutorial"
            )
        }

        if (!dittofolder.exists()) {
            dittofolder.mkdir()
        }

        val filename =
            "${filename.toString()}.pdf"
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
            logger.d("Error, $e")
        }
    }

    internal fun isFileAvailable(filename: String): Uri? {
        val directory = if (Build.VERSION.SDK_INT >= 30) {
            File(
                context?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                    .toString() + "/" + "Tutorial"
            )
        } else {
            File(
                Environment.getExternalStorageDirectory().toString() + "/" + "Tutorial"
            )
        }
        val pdfFile = File(directory, "${filename.toString()}.pdf")
        var path: Uri? = null
        if (pdfFile.exists()) {
            path = Uri.fromFile(pdfFile)
        } else {
            path = null
        }
        return path
    }

    fun onFinished() {
        uiEvents.post(Event.OnDownloadComplete)
    }
}