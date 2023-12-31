package com.ditto.mylibrary.ui

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.domain.MyLibraryUseCase
import com.ditto.mylibrary.domain.model.*
import com.ditto.mylibrary.domain.request.FolderRequest
import com.ditto.mylibrary.domain.request.GetFolderRequest
import com.ditto.mylibrary.domain.request.MyLibraryFilterRequestData
import com.ditto.mylibrary.domain.request.OrderFilter
import core.appstate.AppState
import core.event.UiEvents
import core.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import non_core.lib.error.Error
import non_core.lib.error.NoNetworkError
import org.json.JSONObject
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class AllPatternsViewModel @Inject constructor(
    private val libraryUseCase: MyLibraryUseCase
) : BaseViewModel() {
    @Inject
    lateinit var loggerFactory: LoggerFactory

    val logger: Logger by lazy {
        loggerFactory.create(AllPatternsViewModel::class.java.simpleName)
    }


    var data: MutableLiveData<List<MyLibraryData>> = MutableLiveData()
    var clickedTailornovaID: ObservableField<String> = ObservableField("")
    var clickedOrderNumber: ObservableField<String> = ObservableField("")
    private val dbLoadError: ObservableBoolean = ObservableBoolean(false)
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    var errorString: ObservableField<String> = ObservableField("")
    val isLoading: ObservableBoolean = ObservableBoolean(false)
    val isFilterResult: ObservableBoolean = ObservableBoolean(false)
    var patternList: MutableLiveData<List<ProdDomain>> = MutableLiveData()
    var patterns = MutableLiveData<ArrayList<ProdDomain>>()
    var map = HashMap<String, List<String>>()
    val menuList = hashMapOf<String, ArrayList<FilterItems>>()
    var folderMainList = ArrayList<MyFolderList>()
    val resultMap = hashMapOf<String, ArrayList<String>>()
    var totalPageCount: Int = 0
    var totalPatternCount: Int = 0
    var currentPageId: Int = 1
    var isFilter: Boolean? = false
    var favorite: String = "Favorite"
    var ADD: String = "ADD"
    var RENAME: String = "RENAME"
    val GETFOLDER = "getFolders"
    var clickedProduct: ProdDomain? = null

    //error handler for data fetch related flow
    private fun handleError(error: Error) {
        when (error) {
            is NoNetworkError -> {
                activeInternetConnection.set(false)
                errorString.set(error.message)
                uiEvents.post(Event.NoInternet)
            }
            else -> {
                errorString.set(error.message)
                uiEvents.post(Event.OnAllPatternResultFailed)
            }

        }
        uiEvents.post(Event.OnAllPatternHideProgress)

    }

    //fetch data from offline
    fun fetchOfflinePatterns() {
        uiEvents.post(Event.OnAllPatternShowProgress)
        disposable += libraryUseCase.getOfflinePatternDetails()
            .delay(600, java.util.concurrent.TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleOfflineFetchResult(it) }
    }

    fun fetchTrialPatterns() {
        uiEvents.post(Event.OnAllPatternShowProgress)
        disposable += libraryUseCase.getTrialPatterns("Trial")
            .delay(600, java.util.concurrent.TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleTrialPatterns(it) }
    }

    private fun handleTrialPatterns(result: Result<List<ProdDomain>>?) {
        uiEvents.post(Event.OnAllPatternHideProgress)
        when (result) {
            is Result.OnSuccess -> {
                patternList.value = result.data
                totalPatternCount = patternList.value?.size ?: 0
                logger.d("PATTERN COUNT == $totalPatternCount")
                totalPageCount = totalPatternCount
                currentPageId = totalPatternCount
                uiEvents.post(Event.OnAllPatternResultSuccess)
            }
            is Result.OnError -> handleError(result.error)
            else -> {}
        }
    }

    private fun handleOfflineFetchResult(result: Result<List<ProdDomain>>) {
        uiEvents.post(Event.OnAllPatternHideProgress)
        when (result) {
            is Result.OnSuccess -> {
                patternList.value = result.data
                totalPatternCount = patternList.value?.size ?: 0
                logger.d("PATTERN  COUNT== $totalPatternCount")
                totalPageCount = totalPatternCount
                currentPageId = totalPatternCount
                uiEvents.post(Event.OnAllPatternResultSuccess)
            }
            is Result.OnError -> handleError(result.error)
        }
    }

    /**
     * Fetching  all patterns from remote data source
     */
    fun fetchOnPatternData(
        createJson: MyLibraryFilterRequestData
    ) {

        uiEvents.post(Event.OnAllPatternShowProgress)
        disposable += libraryUseCase.getPatterns(createJson)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }

    private fun handleFetchResult(result: Result<AllPatternsDomain>) {
        logger.d("Testing, >>>>>>   All Patterns handleFetchResult")
        when (result) {
            is Result.OnSuccess -> {
                var temp: ArrayList<ProdDomain> =
                    if (patternList.value == null) ArrayList() else patternList.value as ArrayList<ProdDomain>
                temp?.addAll(result.data.prod)
               /* val filteredList=  temp.distinctBy {
                    it.iD
                }*/
                patternList.value = temp
                logger.d("PATTERN  COUNT TEMP== ${temp.size}")

                /*result.data.prod.forEach {
                      patternArrayList.add(it)
                  }*/

                //AppState.setPatternCount(result.data.totalPatternCount)
                totalPatternCount = result.data.totalPatternCount ?: 0
                logger.d("PATTERN  COUNT== $totalPatternCount")
                totalPageCount = result.data.totalPageCount ?: 0
                currentPageId = result.data.currentPageId ?: 0
                map = result.data.menuItem ?: hashMapOf() //hashmap
                uiEvents.post(Event.OnAllPatternResultSuccess)
                if (isFilter == false) {
                    setList()  // For Displaying menu item without any filter applied
                    uiEvents.post(Event.UpdateDefaultFilter)
                    isFilterResult.set(false)
                } else {
                    uiEvents.post(Event.UpdateFilterImage)
                    isFilterResult.set(true)
                }
            }
            is Result.OnError -> handleError(result.error)
        }
    }

    private fun handleFolderAPIResult(
        result: Result<AddFavouriteResultDomain>,
        product: ProdDomain?,
        methodName: String
    ) {
        when (result) {
            is Result.OnSuccess -> {
                if (result.data.responseStatus) {
                    logger.d("ADD Folder API RESULT")
                    if (methodName == "update") {
                        uiEvents.post(Event.OnFolderCreated)
                    } else {
                        product?.isFavourite =
                            result.data.queryString.equals("method=addToFavorite")
                        uiEvents.post(Event.OnAllPatternResultSuccess)

                    }


                }
                uiEvents.post(Event.OnAllPatternHideProgress)
            }
            is Result.OnError -> handleError(result.error)


        }
    }

    fun setList() {

        for ((key, value) in map) {
            val menuValues: ArrayList<FilterItems> = ArrayList()
            for (aString in value) {
                menuValues.add(FilterItems(aString))

            }
            //  Filter.menuItemListFilter[key] = menuValues
            val sortedMenuValues = ArrayList(menuValues.sortedWith(compareBy { it.title }))
            menuList[key] = sortedMenuValues
        }

        logger.d("MAP  RESULT==${menuList.size} ")

    }

    fun onItemClick(id: String) {
        clickedTailornovaID.set(id)
        uiEvents.post(Event.OnItemClick)
    }

    fun onItemClickPattern(id: String, orderNumber: String, pattern: ProdDomain) {
        clickedTailornovaID.set(id)
        clickedOrderNumber.set(orderNumber)
        clickedProduct = pattern
        uiEvents.post(Event.OnItemClick)
    }

    fun onDialogPopupClick(product: ProdDomain) {
        uiEvents.post(Event.OnAllPatternShowProgress)
        val folderRequest = GetFolderRequest(
            OrderFilter(
                false,
                AppState.getEmail(),
                purchasedPattern = true,
                subscriptionList = true,
                trialPattern = false
            )
        )
        /**
         * Calling  get Folders API along with New Folder and Display it in POP UP Dialog
         */
        disposable += libraryUseCase.invokeFolderList(folderRequest, GETFOLDER)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResultFolders(it, product) }


    }

    private fun handleFetchResultFolders(
        folderResult: Result<FoldersResultDomain>?,
        product: ProdDomain
    ) {
        logger.d("DIALOG   :handleFetchResultFolders")
        folderMainList = arrayListOf<MyFolderList>(
            MyFolderList("Create New folder")
        )
        clickedProduct = product
        when (folderResult) {
            is Result.OnSuccess -> {
                folderResult.data.responseStatus.forEach {
                    folderMainList.add(
                        MyFolderList(
                            folderName = it
                        )
                    )
                }
                uiEvents.post(Event.OnPopupClick)
            }
            is Result.OnError -> {
                handleError(folderResult.error)
            }

            else -> {}
        }
    }

    fun navigateToAllPatterns() {
        uiEvents.post(Event.OnAddProjectClick)
    }

    fun onOptionsClicked(view: View, patternId: String) {
        uiEvents.post(
            Event.OnOptionsClicked(
                view,
                patternId
            )
        )
    }

    fun onSyncClick() {
        logger.d("onSyncClick : ALL PATTERN VIEW MODEL")
        uiEvents.post(Event.OnAllPatternSyncClick)
    }

    fun onSearchClick() {
        logger.d("onSearchClick : ALL PATTERN VIEW MODEL")
        uiEvents.post(Event.OnAllPatternSearchClick)
    }

    fun onCreateFolderClick() {
        logger.d("onCreateFolderClick : ALL PATTERN VIEW MODEL")
        uiEvents.post(Event.OnCreateFolder)
    }

    fun onFolderClick(folderName: String) {
        /**
         * API call for adding product to a Folder
         */
        logger.d("onFolderClick : ALL PATTERN VIEW MODEL")
        if (AppState.getIsLogged()) {
            addToFolder(
                product = clickedProduct,
                folderName = folderName
            )
        }

    }

    /**
     * Managing API call for Delete Favorite ADD and Update or Create Folder
     */

    fun addToFolder(product: ProdDomain?, folderName: String) {
        val hashMap = HashMap<String, ArrayList<String>>()
        hashMap[folderName] = arrayListOf(product?.tailornovaDesignId ?: "")
        var methodName: String? = ""
        logger.d("DESIGN ID==, product?.tailornovaDesignId ?: ")
        val favReq = FolderRequest(
            OrderFilter(
                false,
                AppState.getEmail(),
                purchasedPattern = true,
                subscriptionList = true,
                trialPattern = false
            ),
            FoldersConfig = hashMap
        )
        uiEvents.post(Event.OnAllPatternShowProgress)
        if (folderName == favorite) {
            methodName = if (product?.isFavourite == true) {
                "deleteFavorite"
            } else {
                "addToFavorite"

            }
        } else {
            methodName = "update"

        }

        disposable += libraryUseCase.addFolder(favReq, methodName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFolderAPIResult(it, product, methodName) }

    }

    /**
     * Events for this view model
     */
    sealed class Event {
        /**
         * Event emitted by [events] when Do not Show It Again is clicked
         */
        object OnItemClick : Event()

        object OnAddProjectClick : Event()

        object OnPopupClick : Event()

        class OnOptionsClicked(
            val view: View,
            val patternId: String
        ) : Event()

        object OnAllPatternSyncClick : Event()
        object OnAllPatternSearchClick : Event()
        object OnCreateFolder : Event()
        object OnFolderCreated : Event()
        object OnAllPatternResultSuccess : Event()
        object OnAllPatternShowProgress : Event()
        object OnAllPatternHideProgress : Event()
        object OnAllPatternResultFailed : Event()
        object NoInternet : Event()
        object UpdateFilterImage : Event()
        object UpdateDefaultFilter : Event()
    }

    fun createJson(currentPage: Int, value: String): MyLibraryFilterRequestData {
        val filterCriteria = MyLibraryFilterRequestData(
            OrderFilter(
                false,
                AppState.getEmail(),
                true,
                true,
                trialPattern = false
            ), pageId = currentPage, patternsPerPage = 12, searchTerm = value
        )
        logger.d("JSON=== json1")
        val filteredMap: HashMap<String, Array<FilterItems>> = HashMap()
        menuList.forEach { (key, value) ->
            val filtered = value.filter { prod -> prod.isSelected }
            if (filtered.isNotEmpty()) {
                filteredMap[key] = filtered.toTypedArray()

            }
        }
        // isFilter = (filteredMap.isNotEmpty())
        if (filteredMap.isNotEmpty() && value.isNotEmpty()) {
            isFilter = true
        } else if (filteredMap.isEmpty() && value.isEmpty()) {
            isFilter = false
        } else if (filteredMap.isNotEmpty() && value.isEmpty()) {
            isFilter = true
        } else if (filteredMap.isNotEmpty()) {
            isFilter = true
        } else if (filteredMap.isEmpty() && value.isNotEmpty()) {
            isFilter = false
        }
        resultMap.clear()
        val jsonProduct = JSONObject()
        for ((key, value) in filteredMap) {
            val arrayList = ArrayList<String>()
            for (result in value) {
                arrayList.add(result.title)
                resultMap[key] = arrayList
                jsonProduct.put(key, arrayList)


            }


        }
        filterCriteria.ProductFilter = resultMap
        logger.d("JSON=== resultJson")
        logger.d("RESULT STRING===, resultString")
        return filterCriteria
    }


}


