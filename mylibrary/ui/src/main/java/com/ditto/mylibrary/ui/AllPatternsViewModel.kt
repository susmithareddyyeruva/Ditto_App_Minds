package com.ditto.mylibrary.ui

import android.util.Log
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.ditto.mylibrary.domain.GetMylibraryData
import com.ditto.mylibrary.domain.model.MyLibraryData
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AllPatternsViewModel @Inject constructor(
    private val getPatternsData: GetMylibraryData
) : BaseViewModel() {

    var data: MutableLiveData<List<MyLibraryData>> = MutableLiveData()
    val clickedId: ObservableInt = ObservableInt(-1)
    private val dbLoadError: ObservableBoolean = ObservableBoolean(false)
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    var userId: Int = 0
    val isLoading: ObservableBoolean = ObservableBoolean(false)
    val isFilterResult : ObservableBoolean = ObservableBoolean(true)

    init {
    }

    //error handler for data fetch related flow
    private fun handleError(error: Error) {
        when (error) {
            is NoNetworkError -> activeInternetConnection.set(false)
            else -> {
                Log.d("AllPatternsViewModel","handleError")
            }
        }
    }

    //fetch data from repo (via usecase)
    fun fetchOnPatternData() {
        disposable += getPatternsData.invoke()
            .delay(600, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .whileSubscribed { isLoading.set(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
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
        Log.d("pattern","Removed")

    }

    fun onFilterClick() {
        Log.d("pattern","onFilterClick : viewModel")
        uiEvents.post(Event.OnFilterClick)
    }

    fun onSyncClick() {
        Log.d("pattern","onSyncClick : viewModel")
        uiEvents.post(Event.OnSyncClick)
    }

    fun onSearchClick() {
        Log.d("pattern","onSearchClick : viewModel")
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

        class OnOptionsClicked(val view: View,
                               val patternId: Int) : Event()

        object OnFilterClick : Event()
        object OnSyncClick : Event()
        object OnSearchClick : Event()
    }

}