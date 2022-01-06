package com.ditto.home.ui

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.ditto.home.domain.HomeUsecase
import com.ditto.home.domain.model.HomeData
import com.ditto.home.domain.model.MyLibraryDetailsDomain
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.domain.model.OfflinePatternData
import com.ditto.mylibrary.domain.model.PatternIdData
import com.ditto.mylibrary.domain.model.ProdDomain
import com.ditto.storage.domain.StorageManager
import com.example.home_ui.R
import core.USER_FIRST_NAME
import core.appstate.AppState
import core.data.model.SoftwareUpdateResult
import core.event.UiEvents
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
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.component1
import kotlin.collections.component2

class HomeViewModel @Inject constructor(
    private val context: Context,
    val storageManager: StorageManager,
    val useCase: HomeUsecase,
    private val utility: Utility
) : BaseViewModel() {
    @Inject
    lateinit var loggerFactory: LoggerFactory
    var versionResult: SoftwareUpdateResult? = null

    val logger: Logger by lazy {
        loggerFactory.create(HomeViewModel::class.java.simpleName)
    }
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    val homeItem: ArrayList<HomeData> = ArrayList()
    var header: ObservableField<String> = ObservableField()
    var errorString: ObservableField<String> = ObservableField("")
    var homeDataResponse: MutableLiveData<MyLibraryDetailsDomain> = MutableLiveData()
    var trialPatternData: ArrayList<PatternIdData> = ArrayList()
    var productCount: Int = 0
    val resultMap = hashMapOf<String, ArrayList<String>>()
    val imagesToDownload = hashMapOf<String, String>()
    val patternUri: ObservableField<String> = ObservableField("")

    sealed class Event {
        object OnClickMyPatterns : Event()
        object OnClickDitto : Event()
        object OnClickJoann : Event()
        object OnClickTutorial : Event()
        object OnResultSuccess : HomeViewModel.Event()
        object OnShowProgress : HomeViewModel.Event()
        object OnHideProgress : HomeViewModel.Event()
        object OnResultFailed : HomeViewModel.Event()
        object NoInternet : HomeViewModel.Event()
       // object OnListenVersionEvent : HomeViewModel.Event()
        object OnTrialPatternSuccess : HomeViewModel.Event()
        object OnImageDownloadComplete : HomeViewModel.Event()
    }

    init {

        setHomeHeader()

    }

    fun onItemClick(id: Int) {
        when (id) {
            0 -> {
                uiEvents.post(Event.OnClickTutorial)

            }
            1 -> {
                uiEvents.post(Event.OnClickMyPatterns)

            }
            2 -> {
                uiEvents.post(Event.OnClickDitto)

            }
            3 -> {
                uiEvents.post(Event.OnClickJoann)
            }
        }
    }

    fun setHomeHeader() {
        if (!AppState.getIsLogged()) {
            isGuest.set(true)
            header.set("Hi there,")
        } else {
            isGuest.set(false)
            header.set(storageManager.getStringValue(USER_FIRST_NAME).toString())
        }
    }

    fun setHomeItems() {
        homeItem.clear()
        val images = intArrayOf(
            R.drawable.ic_home_tutorial, R.drawable.ic_home_pattern_library,
            R.drawable.ic_home_ditto, R.drawable.ic_home_joann
        )

        val title = intArrayOf(
            R.string.beam_setup_and_calibration, R.string.pattern_library_count,
            R.string.more_patterns_available_at, R.string.shop_fabric_supplies
        )

        val description = intArrayOf(
            R.string.view_tutorial,
            R.string.all_your_patterns_in_one_place,
            R.string.ditto_patterns_site,
            R.string.joann_site
        )
        for (item in images.indices) {
            var homeItems: HomeData = HomeData(
                item,
                title[item],
                description[item],
                images[item]
            )
            homeItem.add(homeItems)

        }
    }

    fun fetchData() {
        uiEvents.post(Event.OnShowProgress)
        disposable += useCase.getHomePatternsData(
            com.ditto.home.domain.request.MyLibraryFilterRequestData(
                com.ditto.home.domain.request.OrderFilter(
                    true,
                    AppState.getEmail(),
                    true,
                    true
                ), productFilter = resultMap, patternsPerPage = 12, pageId = 1
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }

    fun fetchOfflineData() {
        uiEvents.post(Event.OnShowProgress)
        disposable += useCase.getOfflinePatternDetails()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleOfflineFetchResult(it) }
    }

    // todo if no trial patterns available in DB then call this api (how to)
    fun fetchTailornovaTrialPattern() {
        uiEvents.post(Event.OnShowProgress)
        disposable += useCase.fetchTailornovaTrialPatterns()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleTrialPatternResult(it) }
    }

    fun fetchListOfTrialPatternFromInternalStorage() {
        uiEvents.post(Event.OnShowProgress)
        disposable += useCase.getTrialPatterns()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleTrialPatternResultForGuestUser(it) }
    }

    private fun handleTrialPatternResultForGuestUser(result: Result<List<ProdDomain>>?) {
        when (result) {
            is Result.OnSuccess -> {
                uiEvents.post(Event.OnTrialPatternSuccess)
                uiEvents.post(Event.OnHideProgress)
                var count: Int = result?.data?.size
                logger.d("Home Screen, $count")
                productCount = count
                AppState.setPatternCount(productCount)
                setHomeItems()  //Preparing menu items
                uiEvents.post(Event.OnResultSuccess)
            }
            is Result.OnError -> {
                uiEvents.post(Event.OnHideProgress)
               // uiEvents.post(Event.OnResultFailed)
                logger.d("DEBUG>>>>,handleTrialPatternResultForGuestUser error")
                handleError(result.error)
            }
        }
    }

    private fun handleTrialPatternResult(result: Result<List<PatternIdData>>?) {
        when (result) {
            is Result.OnSuccess -> {
                logger.d("DEBUG>>>>,handleTrialPatternResult Success")
                trialPatternData = result.data as ArrayList<PatternIdData>
                if (AppState.getIsLogged()) {
                    fetchData()
                } else {
                    logger.d("DEBUG>>>>,fetchListOfTrialPatternFromInternalStorage success")
                    fetchListOfTrialPatternFromInternalStorage()
                }
            }

            is Result.OnError -> {
                uiEvents.post(Event.OnHideProgress)
               // uiEvents.post(Event.OnResultFailed)
                logger.d("DEBUG>>>>,handleTrialPatternResultError")
                handleError(result.error)
            }
        }
    }

    /**
     * Handling fetch result here.....
     */
    private fun handleFetchResult(result: Result<MyLibraryDetailsDomain>?) {
        uiEvents.post(Event.OnHideProgress)
        when (result) {
            is Result.OnSuccess -> {
                uiEvents.post(Event.OnTrialPatternSuccess)
                uiEvents.post(Event.OnHideProgress)
                homeDataResponse.value = result.data
                logger.d("Home Screen, $homeDataResponse.value.prod.size")
                productCount = homeDataResponse.value!!.totalPatternCount
                AppState.setPatternCount(productCount)
                logger.d("Home Screen, ${productCount}")
                setHomeItems()  //Preparing menu items
                uiEvents.post(Event.OnResultSuccess)
               // uiEvents.post(Event.OnListenVersionEvent)
            }
            is Result.OnError -> {
                uiEvents.post(Event.OnHideProgress)
                //uiEvents.post(Event.OnResultFailed)
                handleError(result.error)
            }
        }
    }

    /**
     * Handling offline fetch result here.....
     */
    private fun handleOfflineFetchResult(result: Result<List<OfflinePatternData>>?) {
        uiEvents.post(Event.OnHideProgress)
        when (result) {
            is Result.OnSuccess -> {
                uiEvents.post(Event.OnHideProgress)
                var count: Int = result?.data?.size
                logger.d("Home Screen, $count")
                productCount = count
                AppState.setPatternCount(productCount)
                setHomeItems()  //Preparing menu items
                uiEvents.post(Event.OnResultSuccess)
            }
            is Result.OnError -> {
                uiEvents.post(Event.OnHideProgress)
                //uiEvents.post(Event.OnResultFailed)
                handleError(result.error)
            }
        }
    }


    private fun handleError(error: Error) {
        when (error) {
            is NoNetworkError -> {
                activeInternetConnection.set(false)
                errorString.set(error.message)
                uiEvents.post(Event.NoInternet)
            }
            else -> {
                errorString.set(error.message)
                uiEvents.post(Event.OnResultFailed)
            }

        }
    }

    fun versionCheck() {
        utility.checkVersion()
    }

    suspend fun prepareDowloadList(hashMap: HashMap<String, String>, patternName: String?) {
        logger.d("Download, >>>>>>>>>>>>>>>>>>>> STARTED for $patternName")
        logger.d("Download, Hashmap size Home: $patternName: >>  ${hashMap?.size}")
        if (!hashMap.isEmpty()) {
            hashMap.forEach { (key, value) ->
                logger.d("Download, file not present for $$patternName: KEY: $key \t VALUE : $value")
                if (!(key.isNullOrEmpty()) && !(value.isNullOrEmpty())) {
                    downloadEachPatternPiece(
                        imageUrl = value,
                        filename = key,
                        patternFolderName = patternName ?: "Pattern Piece"
                    )
                }

            }
            logger.d("Download, download completed for  $patternName")
        } else {
            logger.d("Download, download completed for  $patternName  0 images there")
        }
    }

    suspend fun downloadEachPatternPiece(
        imageUrl: String,
        filename: String,
        patternFolderName: String?
    ) {
        try {
            withContext(Dispatchers.IO) {
                val inputStream: InputStream
                var result: File? = null
                val url: URL = URL(imageUrl)
                val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connect()
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    patternUri.set("")
                    return@withContext
                }
                inputStream = conn.inputStream
                if (inputStream != null)
                    result =
                        convertInputStreamToFileForPatterns(
                            inputStream,
                            filename,
                            patternFolderName
                        )
                val path = Uri.fromFile(result)
                patternUri.set(path.toString())
                logger.d("PATTERN, ${patternUri.get() ?:""}")
            }
        }catch (e: Exception){
            logger.d("HomeViewModel, ${e.message}")
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
       /* dittofolder = File(
            Environment.getExternalStorageDirectory().toString() + "/" + "Ditto"
        )*/
        val contextWrapper = ContextWrapper(context)
        dittofolder = contextWrapper.getDir("Ditto", Context.MODE_PRIVATE)

        subFolder = File(dittofolder, "/${patternFolderName.toString().replace("[^A-Za-z0-9 ]".toRegex(), "")}")

        if (!dittofolder.exists()) {
            dittofolder.mkdir()
            if (!subFolder.exists()) {
                subFolder.mkdirs()
            }
        } else {
            if (!subFolder.exists()) {
                subFolder.mkdirs()
            } else {
                logger.d("Ditto Folder, ${patternFolderName}PRESENT IN DIRECTORY")
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
        }
    }

    fun imageFilesToDownload(
        hashMap: HashMap<String, String>,
        patternName: String?
    ): HashMap<String, String> {
        imagesToDownload.clear()
        hashMap.forEach { (key, value) ->
            if (!(key.isNullOrEmpty())) {
                val availableUri = key.let {
                    core.ui.common.Utility.isImageFileAvailable(
                        it,
                        "$patternName",
                        context
                    )
                }

                if (availableUri == null) {
                    imagesToDownload.put(key, value)
                }
            }
        }
        return imagesToDownload
    }

}