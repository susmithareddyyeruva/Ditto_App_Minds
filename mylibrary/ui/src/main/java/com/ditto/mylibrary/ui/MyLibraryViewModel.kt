package com.ditto.mylibrary.ui

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
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
import trace.mylibrary.domain.GetMylibraryData
import trace.mylibrary.domain.model.MyLibraryData
import javax.inject.Inject

class MyLibraryViewModel @Inject constructor(
    private val getMylibraryData: GetMylibraryData
) : BaseViewModel() {

    var data: MutableLiveData<List<MyLibraryData>> = MutableLiveData()
    val clickedId: ObservableInt = ObservableInt(-1)
    private val dbLoadError: ObservableBoolean = ObservableBoolean(false)
    var userId: Int = 0
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()

    init {
        fetchOnBoardingData()
        fetchDbUser()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    //fetch data from repo (via usecase)
    private fun fetchOnBoardingData() {
        disposable += getMylibraryData.invoke()
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }

    private fun fetchDbUser() {
        dbLoadError.set(false)
        disposable += getMylibraryData.getUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { it }
    }

    private fun handleFetchResult(result: Result<List<MyLibraryData>>) {
        when (result) {
            is Result.OnSuccess -> {
                data.value = result.data
                activeInternetConnection.set(true)
            }
            is Result.OnError -> handleError(result.error)
        }
    }

    //error handler for data fetch related flow
    private fun handleError(error: Error) {
        when (error) {
            is NoNetworkError -> activeInternetConnection.set(false)
            else -> {
            }
        }
    }

    fun activeProjects(){}
    fun completedProjects(){
        uiEvents.post(Event.completedProjects)
    }
    fun allPatterns(){}

    /**
     * Events for this view model
     */
    sealed class Event {
        object activeProjects : Event()
        object completedProjects : Event()
        object allPatterns : Event()
    }
}
