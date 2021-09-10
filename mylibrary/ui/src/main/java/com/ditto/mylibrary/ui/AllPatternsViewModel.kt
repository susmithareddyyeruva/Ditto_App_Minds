package com.ditto.mylibrary.ui

import android.util.Log
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.ditto.mylibrary.domain.GetMylibraryData
import com.ditto.mylibrary.domain.model.Filter
import com.ditto.mylibrary.domain.model.FilterCriteria
import com.ditto.mylibrary.domain.model.MyLibraryData
import com.google.gson.Gson
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AllPatternsViewModel @Inject constructor(
    private val getPatternsData: GetMylibraryData,
    val utility: Utility
) : BaseViewModel() {

    var data: MutableLiveData<List<MyLibraryData>> = MutableLiveData()
    val clickedId: ObservableInt = ObservableInt(-1)
    private val dbLoadError: ObservableBoolean = ObservableBoolean(false)
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    var userId: Int = 0
    val isLoading: ObservableBoolean = ObservableBoolean(false)
    val isFilterResult : ObservableBoolean = ObservableBoolean(false)

    init {
        if (Utility.isTokenExpired()) {
            utility.refreshToken()
        }
    }

    //error handler for data fetch related flow
    private fun handleError(error: Error) {
        when (error) {
            is NoNetworkError -> activeInternetConnection.set(false)
            else -> {
                Log.d("AllPatternsViewModel", "handleError")
            }
        }
    }

    //fetch data from repo (via usecase)
    fun fetchOnPatternData() {
        disposable += getPatternsData.invoke()
            .delay(600, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .whileSubscribed { handleProgress(it)}
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }

    private fun handleProgress(isProgress: Boolean) {
            uiEvents.post(if (isProgress) Event.OnLoadingStarts else Event.OnLoadingCompleted)
    }

    private fun handleFetchResult(result: Result<List<MyLibraryData>>) {
        when (result) {
            is Result.OnSuccess -> {
                data.value = result.data
                uiEvents.post(Event.OnDataUpdated)
            }
            is Result.OnError -> handleError(result.error)
        }
    }

    fun onItemClick(id: Int) {
        clickedId.set(id)
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

        object OnLoadingStarts : Event()

        object OnLoadingCompleted : Event()

        class OnOptionsClicked(
            val view: View,
            val patternId: Int
        ) : Event()

        object OnFilterClick : Event()
        object OnSyncClick : Event()
        object OnSearchClick : Event()
    }

    fun createJson() {
        val genderAsString =
            Filter.genderList.filter { it.isSelected }.map { it.title }.joinToString(",")
        val brandAsString =
            Filter.brandList.filter { it.isSelected }.map { it.title }.joinToString(",")
        val categoryAsString =
            Filter.categoryList.filter { it.isSelected }.map { it.title }.joinToString(",")
        val sizeAsString =
            Filter.sizeList.filter { it.isSelected }.map { it.title }.joinToString(",")
        val typeAsString =
            Filter.typeList.filter { it.isSelected }.map { it.title }.joinToString(",")
        val seasonAsString =
            Filter.seasonList.filter { it.isSelected }.map { it.title }.joinToString(",")
        val occasionAsString =
            Filter.occasionList.filter { it.isSelected }.map { it.title }.joinToString(",")
        val suitableAsString =
            Filter.suitableList.filter { it.isSelected }.map { it.title }.joinToString(",")
        val customizationAsString =
            Filter.customizationList.filter { it.isSelected }.map { it.title }.joinToString(",")
        val filterCriteria = FilterCriteria()
        filterCriteria.category = categoryAsString
        filterCriteria.brand = brandAsString
        filterCriteria.gender = genderAsString
        filterCriteria.size = sizeAsString
        filterCriteria.type = typeAsString
        filterCriteria.season = seasonAsString
        filterCriteria.occasion = occasionAsString
        filterCriteria.suitable = suitableAsString
        filterCriteria.customization = customizationAsString

        val json = Gson().toJson(filterCriteria)
        Log.d("JSON===", json)

    }

}