package com.ditto.mylibrary.ui

import android.util.Log
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.ditto.mylibrary.domain.GetMylibraryData
import com.ditto.mylibrary.domain.model.AllPatternsDomain
import com.ditto.mylibrary.domain.model.FilterItems
import com.ditto.mylibrary.domain.model.MyLibraryData
import com.ditto.mylibrary.domain.model.ProdDomain
import com.ditto.mylibrary.domain.request.MyLibraryFilterRequestData
import com.ditto.mylibrary.domain.request.OrderFilter
import com.google.gson.Gson
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
    val resultMap = hashMapOf<String, ArrayList<String>>()
    var totalPageCount: Int = 0
    var totalPatternCount: Int = 0
    var currentPageId: Int = 1


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


    /*   fun getFilteredPatternsData(request: MyLibraryFilterRequestData) {
           uiEvents.post(Event.OnShowProgress)
           disposable += getPatternsData.getFilteredPatterns(request)
               .delay(600, TimeUnit.MILLISECONDS)
               .subscribeOn(Schedulers.io())
               .whileSubscribed { isLoading.set(it) }
               .observeOn(AndroidSchedulers.mainThread())
               .subscribeBy { handleFilterResult(it) }
       }*/

    private fun handleFilterResult(result: Result<AllPatternsDomain>) {
        uiEvents.post(Event.OnHideProgress)
        when (result) {
            is Result.OnSuccess -> {
                patternArrayList.clear()
                patternList.value = result.data.prod
                result.data.prod.forEach {
                    patternArrayList.add(it)
                }

                AppState.setPatternCount(result.data.totalPatternCount)
                uiEvents.post(Event.OnDataUpdated)
            }
            is Result.OnError -> {
                handleError(result.error)
            }
        }

    }

    private fun handleFetchResult(result: Result<AllPatternsDomain>) {
        uiEvents.post(Event.OnHideProgress)
        when (result) {
            is Result.OnSuccess -> {
                patternList.value = result.data.prod

                result.data.prod.forEach {
                    patternArrayList.add(it)
                }

                AppState.setPatternCount(result.data.totalPatternCount)
                totalPatternCount = result.data.totalPatternCount
                Log.d("PATTERN  COUNT== ", totalPatternCount.toString())
                totalPageCount = result.data.totalPageCount
                currentPageId = result.data.currentPageId
                map = result.data.menuItem  //hashmap
                setList()
                uiEvents.post(Event.OnResultSuccess)
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

    fun onFilterClick() {
        Log.d("pattern", "onFilterClick : viewModel")
        uiEvents.post(Event.OnFilterClick)
    }

    fun onSyncClick() {
        Log.d("pattern", "onSyncClick : viewModel")
        uiEvents.post(Event.OnSyncClick)
    }

    fun onSearchClick() {
        Log.d("pattern", "onSearchClick : viewModel")
        uiEvents.post(Event.OnSearchClick)
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

        class OnOptionsClicked(
            val view: View,
            val patternId: Int
        ) : Event()

        object OnFilterClick : Event()
        object OnSyncClick : Event()
        object OnSearchClick : Event()
        object OnResultSuccess : Event()
        object OnShowProgress : Event()
        object OnHideProgress : Event()
        object OnResultFailed : Event()
        object NoInternet : Event()
        object OnUpdateFilter : Event()
        object UpdateFilterImage : Event()
        object UpdateDefaultFilter : Event()
    }

    fun createJson(currentPage: Int): MyLibraryFilterRequestData {
        val filterCriteria = MyLibraryFilterRequestData(
            OrderFilter(
                true,
                "subscustomerOne@gmail.com",
                true,
                true
            ), pageId = currentPage, patternsPerPage = 12
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
        if (filteredMap.isEmpty()) {
            isFilterResult.set(false)
            uiEvents.post(Event.UpdateDefaultFilter)
        } else {
            isFilterResult.set(true)
            uiEvents.post(Event.UpdateFilterImage)
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


