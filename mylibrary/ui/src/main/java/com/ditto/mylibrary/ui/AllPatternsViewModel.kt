package com.ditto.mylibrary.ui

import android.util.Log
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.ditto.mylibrary.domain.MyLibraryUseCase
import com.ditto.mylibrary.domain.model.*
import com.ditto.mylibrary.domain.request.FolderRequest
import com.ditto.mylibrary.domain.request.GetFolderRequest
import com.ditto.mylibrary.domain.request.MyLibraryFilterRequestData
import com.ditto.mylibrary.domain.request.OrderFilter
import com.google.gson.Gson
import core.appstate.AppState
import core.event.UiEvents
import core.ui.BaseViewModel
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import non_core.lib.error.Error
import non_core.lib.error.NoNetworkError
import non_core.lib.whileSubscribed
import org.json.JSONObject
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class AllPatternsViewModel @Inject constructor(
    private val libraryUseCase: MyLibraryUseCase
) : BaseViewModel() {

    var data: MutableLiveData<List<MyLibraryData>> = MutableLiveData()
    var clickedTailornovaID: ObservableField<String> = ObservableField("")//todo
    var clickedOrderNumber: ObservableField<String> = ObservableField("")//todo
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
     var clickedProduct: ProdDomain?=null

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
            .whileSubscribed { isLoading.set(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleOfflineFetchResult(it) }
    }

    private fun handleOfflineFetchResult(result: Result<List<ProdDomain>>) {
        uiEvents.post(Event.OnAllPatternHideProgress)
        when (result) {
            is Result.OnSuccess -> {
                patternList.value = result.data
                totalPatternCount = patternList.value?.size ?: 0
                Log.d("PATTERN  COUNT== ", totalPatternCount.toString())
                totalPageCount = totalPatternCount
                currentPageId = totalPatternCount
                uiEvents.post(Event.OnAllPatternResultSuccess)
            }
            is Result.OnError -> handleError(result.error)
        }
    }

    //fetch data from repo (via usecase)
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
        Log.d("Testing", ">>>>>>   All Patterns handleFetchResult")
        when (result) {
            is Result.OnSuccess -> {
                var temp: ArrayList<ProdDomain> = if(patternList.value==null) ArrayList() else patternList.value as ArrayList<ProdDomain>
                temp?.addAll(result.data.prod)
                patternList.value = temp

                /*result.data.prod.forEach {
                      patternArrayList.add(it)
                  }*/

                //AppState.setPatternCount(result.data.totalPatternCount)
                totalPatternCount = result.data.totalPatternCount ?: 0
                Log.d("PATTERN  COUNT== ", totalPatternCount.toString())
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

    private fun handleAddToFavouriteResult(
        result: Result<AddFavouriteResultDomain>,
        product: ProdDomain?,
        methodName: String
    ) {
        when (result) {
            is Result.OnSuccess -> {
                if (result.data.responseStatus) {
                    Log.d("Added to Favourite", "FAVOURITE")
                    if (methodName == "update") {
                        uiEvents.post(Event.OnFolderCreated)
                    } else {
                        product?.isFavourite = result.data.queryString.equals("method=addToFavorite")
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
            menuList[key] = menuValues
        }

        Log.d("MAP  RESULT== ", menuList.size.toString())

    }

    fun onItemClick(id: String) {
        clickedTailornovaID.set(id)
        uiEvents.post(Event.OnItemClick)
    }

    fun onItemClickPattern(id: String,orderNumber: String) {
        if (id == "10140549") {
            clickedTailornovaID.set("1")
            clickedOrderNumber.set(orderNumber)
        } else if (id == "10544781") {
            clickedTailornovaID.set("2")
            clickedOrderNumber.set(orderNumber)
        } else if (id == "10140606") {
            clickedTailornovaID.set("3")
            clickedOrderNumber.set(orderNumber)
        } else {
            clickedTailornovaID.set("4")
            clickedOrderNumber.set(orderNumber)
        }
        uiEvents.post(Event.OnItemClick)
    }

    fun onDialogPopupClick(product: ProdDomain) {
        Log.d("DIALOG","onDialogPopupClick")
        uiEvents.post(Event.OnAllPatternShowProgress)
        val folderRequest = GetFolderRequest(
            OrderFilter(
                true,
                AppState.getEmail(),
                purchasedPattern = false,
                subscriptionList = false,
                trialPattern = true
            )
        )
        disposable += libraryUseCase.invokeFolderList(folderRequest, GETFOLDER)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResultFolders(it, product) }


    }

    private fun handleFetchResultFolders(
        folderResult: Result<FoldersResultDomain>?,
        product: ProdDomain
    ) {
        Log.d("DIALOG","handleFetchResultFolders")
        folderMainList = arrayListOf<MyFolderList>(
            MyFolderList("New folder")
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
        Log.d("pattern", "onSyncClick : viewModel")
        uiEvents.post(Event.OnAllPatternSyncClick)
    }

    fun onSearchClick() {
        Log.d("pattern", "onSearchClick : viewModel")
        uiEvents.post(Event.OnAllPatternSearchClick)
    }

    fun onCreateFolderClick() {
        Log.d("pattern", "onCreateFolderClick : viewModel")
        uiEvents.post(Event.OnCreateFolder)
    }

    fun onFolderClick(folderName: String) {
        Log.d("pattern", "onFolderClick : viewModel")
        Log.d("DIALOG","onFolderClick")
        if (AppState.getIsLogged() && !Utility.isTokenExpired()) {
            addToFolder(
                product = clickedProduct,
                folderName = folderName
            )
        }

    }


    fun addToFolder(product: ProdDomain?, folderName: String) {
        val hashMap = HashMap<String, ArrayList<String>>()
        hashMap[folderName] = arrayListOf(product?.tailornovaDesignId ?: "")
        var methodName: String? = ""
        Log.d("DESIGN ID==", product?.tailornovaDesignId ?: "")
        val favReq = FolderRequest(
            OrderFilter(
                true,
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
            .whileSubscribed { isLoading.set(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleAddToFavouriteResult(it, product, methodName) }

    }

    /**
     * Events for this view model
     */
    sealed class Event {
        /**
         * Event emitted by [events] when Do not Show It Again is clicked
         */
        object OnItemClick : Event()

        object OnDataUpdated : Event()

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
                true,
                AppState.getEmail(),
                true,
                true,
                trialPattern = false
            ), pageId = currentPage, patternsPerPage = 12, searchTerm = value
        )
        val json1 = Gson().toJson(menuList)
        Log.d("JSON===", json1)
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
        val resultJson = Gson().toJson(resultMap)
        Log.d("JSON===", resultJson)
        val resultString: String = resultJson.substring(1, resultJson.toString().length - 1)
        Log.d("RESULT STRING===", resultString)
        return filterCriteria
    }


}


