package com.ditto.mylibrary.ui

import android.text.Editable
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ditto.mylibrary.domain.MyLibraryUseCase
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
import javax.inject.Inject

class MyLibraryViewModel @Inject constructor(
    private val getMylibraryData: MyLibraryUseCase
) : BaseViewModel() {

    var data: MutableLiveData<List<MyLibraryData>> = MutableLiveData()
    val clickedId: ObservableInt = ObservableInt(-1)
    private val dbLoadError: ObservableBoolean = ObservableBoolean(false)
    var userId: Int = 0
    var myLibraryTitle: ObservableField<String> = ObservableField("")
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    var  isSearchEnabled=ObservableBoolean()
    var tabPosition: Int=0
    private var patternSearchName = MutableLiveData<String?>()
    var searchText: LiveData<String?>? = null
        get() = patternSearchName

    init {
        // fetchOnBoardingData()
        fetchDbUser()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    //fetch data from repo (via usecase)
/*    private fun fetchOnBoardingData() {
        disposable += getMylibraryData.invoke()
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }*/

    private fun fetchDbUser() {
        dbLoadError.set(false)
        disposable += getMylibraryData.getUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { it }
    }
    fun passEventForAllPattern(){
        uiEvents.post(Event.OnNetworkCheck)
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
                Log.d("MyLibraryViewModel", "handleError")
            }
        }
    }

    /**
     * Events for this view model
     */
    sealed class Event {
        object OnFilterClick : Event()
        object MyLibrarySync : Event()
        object OnSearchClick : Event()
        object OnCancelClick :Event()
        object OnNetworkCheck :Event()
    }
    fun onCancelSearchClick() {
        uiEvents.post(Event.OnCancelClick)
    }
    fun onFilterClick() {
        Log.d("pattern", "onFilterClick : viewModel")
        uiEvents.post(Event.OnFilterClick)
    }

    fun onSyncClick() {
        Log.d("pattern", "onSyncClick : viewModel")
        uiEvents.post(Event.MyLibrarySync)
    }

    fun onSearchClick() {
        Log.d("pattern", "onSearchClick : viewModel")
        uiEvents.post(Event.OnSearchClick)
    }

    fun setPatternSearch(s: Editable){
        patternSearchName.value = s.toString()
    }

}
