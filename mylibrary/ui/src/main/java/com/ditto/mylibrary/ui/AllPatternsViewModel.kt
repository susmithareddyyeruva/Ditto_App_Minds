package com.ditto.mylibrary.ui

import android.util.Log
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.ditto.mylibrary.domain.GetMylibraryData
import com.ditto.mylibrary.domain.model.*
import com.ditto.mylibrary.domain.request.MyLibraryFilterRequestData
import com.ditto.mylibrary.domain.request.OrderFilter
import com.google.gson.Gson
import core.event.UiEvents
import core.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import non_core.lib.error.Error
import non_core.lib.error.NoNetworkError
import non_core.lib.whileSubscribed
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class AllPatternsViewModel @Inject constructor(
    private val getPatternsData: GetMylibraryData
) : BaseViewModel() {

    var data: MutableLiveData<List<MyLibraryData>> = MutableLiveData()
    var clickedTailornovaID: ObservableField<String> = ObservableField("")//todo
    var clickedOrderNumber: ObservableField<String> = ObservableField("")//todo
    private val dbLoadError: ObservableBoolean = ObservableBoolean(false)
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    var errorString: ObservableField<String> = ObservableField("")
    var userId: Int = 0
    val isLoading: ObservableBoolean = ObservableBoolean(false)
    val isFilterResult: ObservableBoolean = ObservableBoolean(false)
    var patternList: MutableLiveData<List<ProdDomain>> = MutableLiveData()
    var patternArrayList = mutableListOf<ProdDomain>()
    var patterns = MutableLiveData<ArrayList<ProdDomain>>()
    var map = HashMap<String, List<String>>()
    val menuList = hashMapOf<String, ArrayList<FilterItems>>()
    var folderMainList = ArrayList<MyFolderList>()
    val resultMap = hashMapOf<String, ArrayList<String>>()
    var totalPageCount: Int = 0
    var totalPatternCount: Int = 0
    var currentPageId: Int = 1
    var isFilter: Boolean? = false


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
                uiEvents.post(Event.OnResultFailed)
            }
        }
    }

    //fetch data from offline
    fun fetchOfflinePatterns() {
        uiEvents.post(Event.OnShowProgress)
        disposable += getPatternsData.getOfflinePatternDetails()
            .delay(600, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .whileSubscribed { isLoading.set(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleOfflineFetchResult(it) }
    }

    private fun handleOfflineFetchResult(result: Result<List<ProdDomain>>) {
        uiEvents.post(Event.OnHideProgress)
        when (result) {
            is Result.OnSuccess -> {
                patternArrayList.clear()
                patternArrayList.addAll(result.data)
                totalPatternCount = patternArrayList.size ?: 0
                Log.d("PATTERN  COUNT== ", totalPatternCount.toString())
                totalPageCount = patternArrayList.size ?: 0
                currentPageId = patternArrayList.size ?: 0
                uiEvents.post(Event.OnResultSuccess)
            }
            is Result.OnError -> handleError(result.error)
        }
    }
    
    
//fetch data from repo (via usecase)
    fun fetchOnPatternData(
        createJson: MyLibraryFilterRequestData
    ) {

        uiEvents.post(Event.OnShowProgress)
        disposable += getPatternsData.invoke(createJson)
            .delay(600, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .whileSubscribed { isLoading.set(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }

    private fun handleFetchResult(result: Result<AllPatternsDomain>) {
        uiEvents.post(Event.OnHideProgress)
        when (result) {
            is Result.OnSuccess -> {
                patternList.value = result.data.prod

                result.data.prod.forEach {
                    patternArrayList.add(it)
                }

                //AppState.setPatternCount(result.data.totalPatternCount)
                totalPatternCount = result.data.totalPatternCount ?: 0
                Log.d("PATTERN  COUNT== ", totalPatternCount.toString())
                totalPageCount = result.data.totalPageCount ?: 0
                currentPageId = result.data.currentPageId ?: 0
                map = result.data.menuItem ?: hashMapOf() //hashmap
                uiEvents.post(Event.OnResultSuccess)
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

    fun setList() {

        for ((key, value) in map) {
            var menuValues: ArrayList<FilterItems> = ArrayList()
            for (aString in value) {
                menuValues?.add(FilterItems(aString))

            }
            //  Filter.menuItemListFilter[key] = menuValues
            menuList[key] = menuValues
        }

        Log.d("MAP  RESULT== ", menuList.size.toString())
        uiEvents.post(Event.OnUpdateFilter)

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

    fun onDialogPopupClick() {
        folderMainList = arrayListOf<MyFolderList>(
            MyFolderList(1, "New folder"),
            MyFolderList(2, "Summer clothes"),
            MyFolderList(3, "Winter wear"),
            MyFolderList(4, "Emma’s patterns")
        )
        uiEvents.post(Event.OnPopupClick)
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

    fun updateProjectComplete(patternId: String) {
        disposable += getPatternsData.completeProject(patternId)
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { uiEvents.post(Event.OnDataUpdated) }
    }

    fun removePattern(patternId: String) {
        Log.d("pattern", "Removed")
    }


    fun onSyncClick() {
        Log.d("pattern", "onSyncClick : viewModel")
        uiEvents.post(Event.OnSyncClick)
    }

    fun onSearchClick() {
        Log.d("pattern", "onSearchClick : viewModel")
        uiEvents.post(Event.OnSearchClick)
    }

    fun onCreateFolderClick() {
        Log.d("pattern", "onSearchClick : viewModel")
        uiEvents.post(Event.OnCreateFolder)
    }

    fun onCreateFoldersSuccess() {
        Log.d("pattern", "onSearchClick : viewModel")
        uiEvents.post(Event.OnFolderCreated)
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

        object OnSyncClick : Event()
        object OnSearchClick : Event()
        object OnCreateFolder : Event()
        object OnFolderCreated : Event()
        object OnResultSuccess : Event()
        object OnShowProgress : Event()
        object OnHideProgress : Event()
        object OnResultFailed : Event()
        object NoInternet : Event()
        object OnUpdateFilter : Event()
        object UpdateFilterImage : Event()
        object UpdateDefaultFilter : Event()
    }

    fun createJson(currentPage: Int, value: String): MyLibraryFilterRequestData {
        val filterCriteria = MyLibraryFilterRequestData(
            OrderFilter(
                true,
                "subscustomerOne@gmail.com",
                true,
                true
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
        }
        else if (filteredMap.isEmpty()&&value.isEmpty()) {
            isFilter = false
        }
        else if (filteredMap.isNotEmpty()&&value.isEmpty()) {
            isFilter = true
        }

        else if (filteredMap.isNotEmpty()) {
            isFilter = true
        }
        else if (filteredMap.isEmpty()&&value.isNotEmpty()) {
            isFilter = false
        }

        val jsonProduct = JSONObject()
        for ((key, value) in filteredMap) {
            var arraYlist = ArrayList<String>()
            for (result in value) {
                arraYlist.add(result.title)
                resultMap[key] = arraYlist
                jsonProduct.put(key, arraYlist)


            }


        }
        filterCriteria.ProductFilter = resultMap
        val resultJson = Gson().toJson(resultMap)
        Log.d("JSON===", resultJson)

        val jsonString: String = resultJson

        val resultString: String = resultJson.substring(1, resultJson.toString().length - 1)
        Log.d("RESULT STRING===", resultString)
        return filterCriteria
    }


}


