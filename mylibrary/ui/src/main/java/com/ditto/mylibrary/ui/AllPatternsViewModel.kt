package com.ditto.mylibrary.ui

import android.util.Log
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.ditto.mylibrary.domain.GetMylibraryData
import com.ditto.mylibrary.domain.model.*
import com.ditto.mylibrary.domain.request.FavouriteRequest
import com.ditto.mylibrary.domain.request.FoldersConfig
import com.ditto.mylibrary.domain.request.MyLibraryFilterRequestData
import com.ditto.mylibrary.domain.request.OrderFilter
import com.google.gson.Gson
import core.CUSTOMER_EMAIL
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
    val clickedId: ObservableInt = ObservableInt(-1)
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

    private fun handleAddToFavouriteResult(result: Result<AddFavouriteResult>) {
        when (result) {
            is Result.OnSuccess -> {
                uiEvents.post(Event.OnHideProgress)
                if (result.data.responseStatus) {
                  Log.d("Added to Favourite","FAVOURITE")
                  uiEvents.post(Event.FetchData)
                }

            }
            is Result.OnError -> handleError(result.error)
            else -> {

            }
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

    fun onItemClick(id: Int) {
        clickedId.set(id)
        uiEvents.post(Event.OnItemClick)
    }

    fun onItemClickPattern(id: String) {
        if (id == "10140549") {
            clickedId.set(1)
        } else if (id == "10544781") {
            clickedId.set(2)
        } else if (id == "10140606") {
            clickedId.set(3)
        } else {
            clickedId.set(4)
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

    fun onOptionsClicked(view: View, patternId: Int) {
        uiEvents.post(
            Event.OnOptionsClicked(
                view,
                patternId
            )
        )
    }

    fun updateProjectComplete(patternId: Int) {
        disposable += getPatternsData.completeProject(patternId)
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { uiEvents.post(Event.OnDataUpdated) }
    }

    fun removePattern(patternId: Int) {
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

    fun onAddtoFavourite(id: String) {
        Log.d("DESIGN ID==", id)
        val favReq = FavouriteRequest(
            OrderFilter(
                true,
                CUSTOMER_EMAIL,
                false,
                false,
                trialPattern = true
            ),
            ProductFilter = ProductFilter(),
            FoldersConfig = FoldersConfig(Favorite = arrayListOf(id))
        )
        uiEvents.post(Event.OnShowProgress)
        disposable += getPatternsData.addFavourite(favReq)
            .delay(600, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .whileSubscribed { isLoading.set(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleAddToFavouriteResult(it) }

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
            val patternId: Int
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
        object FetchData : Event()
    }

    fun createJson(currentPage: Int, value: String): MyLibraryFilterRequestData {
        val filterCriteria = MyLibraryFilterRequestData(
            OrderFilter(
                true,
                CUSTOMER_EMAIL,
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


