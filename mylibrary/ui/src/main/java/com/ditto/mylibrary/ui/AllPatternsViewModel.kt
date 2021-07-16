package com.ditto.mylibrary.ui

import android.util.Log
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.ditto.mylibrary.domain.GetMylibraryData
import com.ditto.mylibrary.domain.model.*
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
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList
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
    var map = HashMap<String, List<String>>()
    val menuList = hashMapOf<String, ArrayList<FilterItems>>()
    val jsonList = hashMapOf<String, ArrayList<String>>()

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
    fun fetchOnPatternData() {
        uiEvents.post(Event.OnShowProgress)
        disposable += getPatternsData.invoke()
            .delay(600, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .whileSubscribed { isLoading.set(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }


    fun getFilteredPatternsData(createJson: ProductFilter) {
        uiEvents.post(Event.OnShowProgress)
        disposable += getPatternsData.getFilteredPatterns(createJson)
            .delay(600, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .whileSubscribed { isLoading.set(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFilterResult(it) }
    }

    private fun handleFilterResult(result: Result<AllPatternsDomain>) {
        uiEvents.post(Event.OnHideProgress)
        when (result) {
            is Result.OnSuccess -> {
                patternList.value = result.data.prod
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
        if (id == "10617801") {
            clickedId.set(1)
        } else if (id == "11836020") {
            clickedId.set(2)
        } else if (id == "13239611") {
            clickedId.set(3)
        } else if (id == "15131196") {
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
    }

    fun createJson(): ProductFilter {
        val filterCriteria = ProductFilter()
        val json = Gson().toJson(filterCriteria)
        /*  for ((key, value) in menuList) {
              Log.d("RESULT==", ":$key = $value")
          }*/
        val json1 = Gson().toJson(menuList)
        Log.d("JSON===", json1)

        val resultMap = hashMapOf<String, ArrayList<String>>()

        val filteredMap: HashMap<String, Array<FilterItems>> = HashMap()
        menuList.forEach { (key, value) ->
            val filtered=value.filter {prod-> prod.isSelected }
            if(filtered.isNotEmpty()){
                filteredMap[key]=filtered.toTypedArray()


            }
        }

        for ((key, value) in filteredMap) {
            Log.d("FILTER MAP==", ":$key = $value")

        }

        for ((key, value) in filteredMap) {
            var arraYlist = ArrayList<String>()
            for (result in value) {
                arraYlist.add(result.title)
                resultMap[key]=arraYlist

            }

        }
        val resultJson = Gson().toJson(resultMap)
        Log.d("JSON===", resultJson)



        return filterCriteria
    }


}