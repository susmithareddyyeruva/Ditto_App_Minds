package com.ditto.mylibrary.ui

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.data.error.FilterError
import com.ditto.mylibrary.data.error.TailornovaAPIError
import com.ditto.mylibrary.data.mapper.toDomain12
import com.ditto.mylibrary.domain.MyLibraryUseCase
import com.ditto.mylibrary.domain.model.*
import core.PDF_DOWNLOAD_URL
import core.YARDAGE_PDF_DOWNLOAD_URL
import core.appstate.AppState
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
    private val getPattern: MyLibraryUseCase,
) :
    BaseViewModel() {
    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(PatternDescriptionViewModel::class.java.simpleName)
    }
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    val isShowindicator: ObservableBoolean = ObservableBoolean(true)

    //    val clickedTailornovaID: ObservableField<String> = ObservableField("30644ba1e7aa41cfa9b17b857739968a")
    val clickedTailornovaID: ObservableField<String> = ObservableField("")
    val mannequinId: ObservableField<String> = ObservableField("")
    val mannequinName: ObservableField<String> = ObservableField("")
    var clickedOrderNumber: ObservableField<String> = ObservableField("")//todo
    var data: MutableLiveData<PatternIdData> = MutableLiveData()
    val patternName: ObservableField<String> = ObservableField("")
    var yardageDetails: List<String> = emptyList()
    val expiredPausedStatus: ObservableField<String> =
        ObservableField("Your subscription has EXPIRED. Please contact Customer Service to reactivate your subscription")
    val tailornovaDesignpatternName: ObservableField<String> = ObservableField("")
    val prodSize: ObservableField<String> = ObservableField("")
    val modificationDate: ObservableField<String> = ObservableField("")
    val isFromDeepLinking: ObservableBoolean = ObservableBoolean(false)
    val patternpdfuri: ObservableField<String> = ObservableField("")
    val patternDescription: ObservableField<String> = ObservableField("NA")
    val patternStatus: ObservableField<String> = ObservableField("")
    val isFromDeeplink: ObservableBoolean = ObservableBoolean(false)

    val isFinalPage: ObservableBoolean = ObservableBoolean(false)
    val isStartingPage: ObservableBoolean = ObservableBoolean(true)

    //    val resumeOrSubscription: ObservableField<String> = ObservableField("RESUME")  to remove resume text chage in PD
    val resumeOrSubscription: ObservableField<String> = ObservableField("WORKSPACE")
    val isSubscriptionExpired: ObservableBoolean = ObservableBoolean(false)
    val isStatusLayoutVisible: ObservableBoolean = ObservableBoolean(false)
    val isYardageAvailable: ObservableBoolean = ObservableBoolean(false)
    val isNotionAvailable: ObservableBoolean = ObservableBoolean(false)
    val isYardagePDFAvailable: ObservableBoolean = ObservableBoolean(false)
    val yardageDescription: ObservableField<String> = ObservableField("")
    val notionsDescription: ObservableField<String> = ObservableField("")
    val selectedSize: ObservableField<String> = ObservableField("") ////selected size string
    val showActive: ObservableBoolean = ObservableBoolean(false)
    val showPurchased: ObservableBoolean = ObservableBoolean(false)
    val showLine: ObservableBoolean = ObservableBoolean(false)
    val showResumButton: ObservableBoolean = ObservableBoolean(false)
    val showWorkspaceOrRenewSubscriptionButton: ObservableBoolean = ObservableBoolean(false)
    val isDataReceived: ObservableBoolean = ObservableBoolean(false)
    val patternUri: ObservableField<String> = ObservableField("")
    val imagesToDownload = hashMapOf<String, String>()
    val temp = ArrayList<String>()
    var mannequinList: ArrayList<MannequinDataDomain>? = ArrayList(emptyList())
    var clickedProduct: ProdDomain? = null
    val isShowSpinner: ObservableBoolean = ObservableBoolean(false)
    var tailornovaApiError: String? = null
    var thirdpartyApiError: String? = null
    var patternsInDB: MutableList<ProdDomain>? = null
    val patternSizeList = arrayListOf(SizeDomain("","","Select Size")) // size list respective to view/cup
    val patternVariationList = arrayListOf<VariationDomain>(VariationDomain(emptyList(),"Select View / Cup Size","")) // view/cup size list
    val isDittoPattern: ObservableBoolean = ObservableBoolean(false)
    val isLastDateAvailable: ObservableBoolean = ObservableBoolean(false)
    val isSizeSpinnerVisible: ObservableBoolean = ObservableBoolean(false) // to display Online view/cup & size spinner
    val isOfflineSizeVisible: ObservableBoolean = ObservableBoolean(false) // to display offline view/cup & size textviews
    val selectedViewOrCupStyle : ObservableField<String> = ObservableField() //selected view/cup size string
    lateinit var selectedSizeDomain: SizeDomain
    val selectedSizePosition: ObservableField<Int> = ObservableField()
    val selectedViewCupPosition: ObservableField<Int> = ObservableField()
    val isShowYardageEmptyView: ObservableBoolean = ObservableBoolean(false)

    //error handler for data fetch related flow
    private fun handleError(error: Error) {
        when (error) {
            is NoNetworkError -> activeInternetConnection.set(false)
            is TailornovaAPIError -> {
                tailornovaApiError = error.message
                uiEvents.post(Event.OnDataloadFailed)
            }
            is FilterError -> {
                thirdpartyApiError = error.message
                if(!thirdpartyApiError.isNullOrEmpty()) uiEvents.post(Event.OnThridPartyFetchError)
            }
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
                mannequinId.set(result.data.selectedMannequinId)
                mannequinName.set(result.data.selectedMannequinName)
                clickedProduct?.mannequin =
                    result.data.mannequin?.map { it.toDomain12() }  //Saving arraylist of mannequin
               // uiEvents.post(Event.OnShowMannequinData)

                //fetch selected view/cup & size from offline data
                selectedSize.set(result.data.size ?: "")
                selectedViewOrCupStyle.set(result.data.selectedViewCupStyle ?: "")
                modificationDate.set(result.data.lastDateOfModification ?: "")
                isLastDateAvailable.set(!modificationDate.get().isNullOrEmpty())
                uiEvents.post(Event.OnDataUpdated)
            }
            is Result.OnError -> handleError(result.error)
        }
    }

    fun fetchPattern() {
        disposable += getPattern.getPattern(
            clickedTailornovaID.get() ?: "",
            mannequinId.get() ?: ""
        )
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }

    fun deletePattern() {
        disposable += getPattern.deletePattern(
            "Trial",
            AppState.getCustID() ?: "",
            clickedTailornovaID.get() ?: ""
        )
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleDeletePattenResult(it) }
    }

    private fun handleDeletePattenResult(result: Result<Boolean>?) {

        when (result) {
            is Result.OnSuccess -> {
                Log.d("PattenDescViewModel", ">>>>>>>>>handleDeletePattenResult OnSuccess ")
                // insert to DB
                if ((NetworkUtility.isNetworkAvailable(context))) {
                    if (isFromDeepLinking.get()) {
                        insertTailornovaDetailsToDB(
                            data.value!!,
                            clickedOrderNumber.get(),
                            tailornovaDesignpatternName.get(),
                            prodSize.get(),
                            clickedProduct?.status,
                            mannequinId.get(),
                            mannequinName.get(),
                            clickedProduct?.mannequin ?: emptyList(),
                            "Purchased",
                            "",
                            "",
                            ""
                        )
                    } else {
                        data.value?.notionDetails = clickedProduct?.notionDetails
                        data.value?.yardageDetails = clickedProduct?.yardageDetails
                        insertTailornovaDetailsToDB(
                            data.value!!,
                            clickedOrderNumber.get(),
                            tailornovaDesignpatternName.get(),
                            prodSize.get(),
                            clickedProduct?.status,
                            mannequinId.get(),
                            mannequinName.get(),
                            clickedProduct?.mannequin ?: emptyList(),
                            clickedProduct?.patternType,
                            modificationDate.get(),
                            selectedViewOrCupStyle.get(),
                            clickedProduct?.yardagePdfUrl
                        )
                    }
                } else {
                    //offline if all images are downloaded
                    uiEvents.post(Event.OnWorkspaceButtonClicked)
                }
            }
            is Result.OnError -> handleError(result.error)
            else -> {}
        }
    }

    private fun handleFetchResult(result: Result<PatternIdData>) {
        when (result) {
            is Result.OnSuccess -> {
                data.value = result.data
                data.value?.selectedMannequinId =
                    mannequinId.get()//getting selected MANNEQUIN ID
                // insert to db here
                if (!isFromDeepLinking.get()) {
                    data.value?.patternName = clickedProduct?.prodName
                    data.value?.description = clickedProduct?.description
                }
                uiEvents.post(Event.OnDataUpdated)
                /*insertTailornovaDetailsToDB(
                    data.value!!,
                    clickedOrderNumber.get(),
                    tailornovaDesignpatternName.get(),
                    prodSize.get(),
                    clickedProduct?.status,
                    mannequinId.get(),
                    mannequinName.get(),
                    clickedProduct?.mannequin ?: emptyList()
                )*/// todo uncomment this line
                //data.value?.thumbnailImageName=clickedProduct?.image //todo need from SFCC
                //data.value?.thumbnailImageUrl=clickedProduct?.image //todo need from SFCC


            }
            is Result.OnError -> handleError(result.error)
        }
    }

    fun fetchThirdPartyData() {
        disposable += getPattern.getThirdPartyPatternData(clickedProduct?.iD ?: "")
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                handleThirdPartyFetchResult(it)
            }
    }

    private fun handleThirdPartyFetchResult(result: Result<ThirdPartyDomain>?) {
        when(result) {
            is Result.OnSuccess -> {
                val variations = result.data.variationDomain ?: emptyList()
                patternVariationList.clear()
                patternVariationList.add(VariationDomain(emptyList(),"Select View / Cup Size",""))
                patternVariationList.addAll(variations)
                uiEvents.post(Event.OnThirdPartyDataFetchSuccess)
            }
            is Result.OnError -> {
                handleError(result.error)
            }
            else -> {}
        }
    }

    private fun insertTailornovaDetailsToDB(
        patternIdData: PatternIdData,
        orderNo: String?,
        tailornovaDesignpatternName: String?,
        prodSize: String?,
        status: String?,
        mannequinId: String?,
        mannequinName: String?,
        mannequin: List<MannequinDataDomain>?,
        patternType: String?,
        lastDateOfModification: String?,
        selectedViewCupStyle: String?,
        yardagePdfUrl: String?
    ) {
        disposable += getPattern.insertTailornovaDetails(
            patternIdData,
            orderNo,
            tailornovaDesignpatternName,
            prodSize,
            status,
            mannequinId,
            mannequinName,
            mannequin,
            patternType,
            lastDateOfModification,
            selectedViewCupStyle,
            yardagePdfUrl
        )
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                handleInsertTailornovaResult(it)
            }
    }

    private fun handleInsertTailornovaResult(result: Any) {
        when (result) {
            is Result.OnSuccess<*> -> {
                logger.d("handlInsertTailornovRes, OnSuccess")
                // delete folder and PDF flow starting here
                if (AppState.getIsLogged()) {
                    fetchDemoPatternList()
                } else {
                    uiEvents.post(Event.OnGuestUSerWSClick)
                }
            }
            is Result.OnError<*> -> {
                logger.d("handlInsertTailornovRes, onFailed")
                handleError(result.error)
            }
        }
    }

    fun fetchDemoPatternList() {
        disposable += getPattern.getAllPatternsInDB()
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleDemoResult(it) }
    }

    private fun handleDemoResult(result: Result<List<ProdDomain>>?) {
        when (result) {
            is Result.OnSuccess -> {
                patternsInDB = result.data.toMutableList()
                logger.d("deleteFolderFun, before : ${patternsInDB.toString()}")
                uiEvents.post(Event.OnDeletePatternFolder)
            }

            is Result.OnError -> {

            }
            else -> {}
        }
    }

    /**
     * [Function] ViewPager Previous Button Click
     */
    fun onClickWorkSpace() {
        if (resumeOrSubscription.get().toString() == "RENEW SUBSCRIPTION") {
            uiEvents.post(Event.OnSubscriptionClicked)
        } else if (resumeOrSubscription.get().toString() == "WORKSPACE") {
            if (mannequinId?.get()
                    ?.isEmpty() == true && !(clickedProduct?.patternType.toString()
                    .equals("Trial", true))
            ) {
                /**
                 * Restricting user to enter into workspace without selecting any customization if Network is Connected
                 */
                uiEvents.post(Event.OnMannequinNameEmpty)
            } else {
                uiEvents.post(Event.OnWorkspaceButtonClicked)
            }
        } else {
            if (mannequinId?.get()
                    ?.isEmpty() == true && !(clickedProduct?.patternType.toString()
                    .equals("Trial", true))
            ) {
                /**
                 * Restricting user to enter into workspace without selecting any customization if Network is Connected
                 */
                uiEvents.post(Event.OnMannequinNameEmpty)
            } else {

                uiEvents.post(Event.OnWorkspaceButtonClicked)
            }
        }
    }


    fun onClickInstructions() {
        uiEvents.post(Event.OnInstructionsButtonClicked)
    }

    fun onClickYardage() {
        uiEvents.post(Event.OnYardageButtonClicked)
    }

    fun onFinished() {
        uiEvents.post(Event.OnDownloadComplete)
    }

    /**
     * Events for this view model
     */
    sealed class Event {

        object OnWorkspaceButtonClicked : Event()
        object OnSubscriptionClicked : Event()
        object OnInstructionsButtonClicked : Event()
        object OnYardageButtonClicked : Event()
        object OnDataUpdated : Event()
        object OnShowMannequinData : Event()
        object OnDownloadComplete : Event()
        object OnDataloadFailed : Event()
        object OnImageDownloadComplete : Event()
        object OnNoNetworkToDownloadImage : Event()
        object OnDeletePatternFolder : Event()
        object OnMannequinNameEmpty : Event()
        object OnGuestUSerWSClick : Event()
        object OnThirdPartyDataFetchSuccess : Event()
        object OnThirdPartyDataFetchResume : Event()
        object OnApiCallInitiated : Event()
        object OnThridPartyFetchError : Event()
        object OnYardagePdfAvailable : Event()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun downloadPDF(url: String, filename: String, patternFolderName: String?) {
        performtask(url, filename, patternFolderName)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun performtask(url: String, filename: String, patternFolderName: String?) {
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
                //result = convertInputStreamToFile(inputStream,filename,patternFolderName?.replace(" ",""))
                    result = convertInputStreamToFile(inputStream, filename, patternFolderName)
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
        filename: String, patternFolderName: String?,
    ): File? {
        var result: File? = null
        var dittofolder: File? = null
        dittofolder = if (Build.VERSION.SDK_INT >= 30) {
            File(
                context?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                    .toString() + "/" + "Ditto"
            )
        } else {
            File(
                Environment.getExternalStorageDirectory().toString() + "/" + "Ditto"
            )
        }

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

    private fun File.copyInputStreamToFile(inputStream: InputStream) {
        try {
            this.outputStream().use { fileOut ->
                inputStream.copyTo(fileOut)
            }
        } catch (e: Exception) {
            logger.d("Error, $e")
        }
    }

    fun prepareDowloadList(hashMap: HashMap<String, String>) {
        logger.d("Download, >>>>>>>>>>>>>>>>>>>> STARTED")
        logger.d("Download, Hashmap size: ${hashMap?.size}")
        temp.clear()
        if (!hashMap.isEmpty()) {
            if (NetworkUtility.isNetworkAvailable(context)) {
//                GlobalScope.launch {
                runBlocking {
                    hashMap.forEach { (key, value) ->
                        logger.d("DOWNLOAD, file not present KEY: $key \t VALUE : $value")
                        if (!(key.isNullOrEmpty()) && !(value.isNullOrEmpty()) && (value != "null")) {
                            val folderName = Utility.getPatternDownloadFolderName(clickedTailornovaID.get() ?: "",
                                   mannequinId.get() ?: "")
                            logger.d("Folder-Name $folderName")

                            downloadEachPatternPiece(
                                imageUrl = value,
                                filename = key,
                                patternFolderName = folderName ?: "Pattern Piece"
                            )
                        }

                    }
                }
                uiEvents.post(Event.OnImageDownloadComplete)
//                }
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
        patternFolderName: String?,
    ) {
        try {
            withContext(Dispatchers.IO) {
                val inputStream: InputStream
                var result: File? = null
                val url: URL = URL(imageUrl)
                val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connect() //conection failed
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    patternpdfuri.set("")
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
                temp.add(path.toString())
            }
        } catch (e: Exception) {
            logger.d("PatternDescriptionViMol, ${e.message}")
        }
    }

    private fun convertInputStreamToFileForPatterns(
        inputStream: InputStream,
        filename: String,
        patternFolderName: String?,
    ): File? {
        var result: File? = null
        var dittofolder: File? = null
        var subFolder: File? = null
        val contextWrapper = ContextWrapper(context)
        dittofolder = contextWrapper.getDir("Ditto", Context.MODE_PRIVATE)
        /* dittofolder = File(
             Environment.getExternalStorageDirectory().toString() + "/" + "Ditto"
         )*/

        subFolder = File(
            dittofolder,
            "/${patternFolderName.toString().replace("[^A-Za-z0-9 ]".toRegex(), "")}"
        )

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

    fun imageFilesToDownload(hashMap: HashMap<String, String>): HashMap<String, String> {
        imagesToDownload.clear()
        hashMap.forEach { (key, value) ->
            if (!(key.isNullOrEmpty())) {
                val availableUri = key.let {
                    core.ui.common.Utility.isImageFileAvailable(
                        it,
                        Utility.getPatternDownloadFolderName(clickedTailornovaID.get() ?: "",
                            mannequinId.get() ?: ""),
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

    fun versionCheck() {
        utility.checkVersion()
    }

    fun setYardageDetails(yardageDetails: java.util.ArrayList<String>) {
        if (yardageDetails.isNullOrEmpty()) {
            isYardageAvailable.set(false)
        } else {
            isYardageAvailable.set(true)
            var temp: String = ""
            for (i in 0 until yardageDetails.size) {
                if (i == 0) {
                    temp += yardageDetails[i]
                } else {
                    temp = temp + "<br>" + yardageDetails[i]
                }
            }
            yardageDescription.set(temp)
        }
    }

    fun setNotionDetails(notionDetails: String?) {
        if (notionDetails.isNullOrEmpty()) {
            isNotionAvailable.set(false)
        } else {
            isNotionAvailable.set(true)
            if (notionDetails.contains("�")) notionsDescription.set(notionDetails.replace("�", ""))
            else notionsDescription.set(notionDetails)
        }
    }

    /**
     * on view/cup size selection
     */
    fun onVariationSelection(
        clickedvariation: VariationDomain, position: Int
    ) {
        selectedViewOrCupStyle.set(clickedvariation.style)
        selectedViewCupPosition.set(position)
        //reset size list
        patternSizeList.clear()

        //reset mannequin details
        mannequinId.set("")
        mannequinName.set("")
        patternSizeList.add(SizeDomain("", "", "Select Size"))
        patternSizeList.addAll(clickedvariation.sizeDomain ?: emptyList())
        this.selectedSize.set(patternSizeList[0].size)
    }

    /**
     * set Online ui for ditto & non ditto patterns
     */
    internal fun setPatternUiBasedOnPatternType() {
        if (clickedProduct?.prodBrand.equals("Ditto")) {
            isDittoPattern.set(true)
            modificationDate.set(clickedProduct?.lastModifiedSizeDate ?: "")
            isLastDateAvailable.set(!clickedProduct?.lastModifiedSizeDate.isNullOrEmpty())
            //val dateText = Utility.getFormattedDateTime(clickedProduct?.dateOfModification ?: "")
            isOfflineSizeVisible.set(false)
            isSizeSpinnerVisible.set(false)
        } else {
            isDittoPattern.set(false)
            isOfflineSizeVisible.set(false)
            isSizeSpinnerVisible.set(true)
            //call 3p api
            if(selectedSize.get().isNullOrEmpty() &&  selectedViewOrCupStyle.get().isNullOrEmpty()) {
                uiEvents.post(Event.OnApiCallInitiated)
                fetchThirdPartyData()
            } else {
                uiEvents.post(Event.OnThirdPartyDataFetchResume)
            }

        }
    }

    /**
     * set offline ui for ditto & non ditto patterns
     */
    internal fun setOfflinePatternUiBasedOnPatternType() {
        if (clickedProduct?.prodBrand.equals("Ditto")) {
            isDittoPattern.set(true)
            isLastDateAvailable.set(!modificationDate.get().isNullOrEmpty())
            isOfflineSizeVisible.set(false)
            isSizeSpinnerVisible.set(false)
        } else {
            isDittoPattern.set(false)
            isOfflineSizeVisible.set(true)
            isSizeSpinnerVisible.set(false)
        }
    }

    /**
     * on size selection for respective view/cup
     */
    internal fun onSizeSelected(selectedSize: SizeDomain, position: Int) {
        this.selectedSize.set(selectedSize.size)
        this.selectedSizeDomain = selectedSize
        selectedSizePosition.set(position)
        if(!selectedSize.designID.isNullOrEmpty() && !selectedSize.mannequinID.isNullOrEmpty()){
            mannequinId.set(selectedSize.mannequinID)
            mannequinName.set(selectedSize.designID)
            clickedTailornovaID.set(selectedSize.designID)
            uiEvents.post(Event.OnApiCallInitiated)
            fetch3pPattern(selectedSize.designID, selectedSize.mannequinID)
        } else {
            mannequinId.set("")
            mannequinName.set("")
            logger.d("onSizeSelected - empty size domain details")
        }
    }

    /***
     * fetch 3p tailornova pattern pieces
     */
    private fun fetch3pPattern(designID: String?, mannequinID: String?) {
        disposable += getPattern.getPattern(
            designID ?: "",
            mannequinID ?: ""
        )
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUI() {
        //when nothing is available
        if (YARDAGE_PDF_DOWNLOAD_URL.isNullOrEmpty() && !isNotionAvailable.get() &&
            !isYardageAvailable.get() && !isYardagePDFAvailable.get()
        ) {
            //Show nothing available msg
            isShowYardageEmptyView.set(true)
        }
        //when yardage & notion not available show pdf
        else if (!YARDAGE_PDF_DOWNLOAD_URL.isNullOrEmpty()) {
            isShowYardageEmptyView.set(false)
            isNotionAvailable.set(false)
            isYardageAvailable.set(false)
            uiEvents.post(Event.OnYardagePdfAvailable)
        }
        else if (isNotionAvailable.get() && isYardageAvailable.get()) {
            isYardagePDFAvailable.set(false)
            isShowYardageEmptyView.set(false)
        }
    }
}