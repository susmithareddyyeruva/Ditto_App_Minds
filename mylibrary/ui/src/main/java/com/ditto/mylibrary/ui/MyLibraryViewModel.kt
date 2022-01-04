package com.ditto.mylibrary.ui

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.ditto.mylibrary.domain.MyLibraryUseCase
import com.ditto.mylibrary.domain.model.MyLibraryData
import core.event.UiEvents
import core.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MyLibraryViewModel @Inject constructor(
    private val getMylibraryData: MyLibraryUseCase
) : BaseViewModel() {

    var data: MutableLiveData<List<MyLibraryData>> = MutableLiveData()
    private val dbLoadError: ObservableBoolean = ObservableBoolean(false)
    var userId: Int = 0
    var myLibraryTitle: ObservableField<String> = ObservableField("")
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    var isSearchEnabled = ObservableBoolean()
    private var patternSearchName = MutableLiveData<String?>()
        get() = patternSearchName

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    private fun fetchDbUser() {
        dbLoadError.set(false)
        disposable += getMylibraryData.getUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { it }
    }

    fun passEventForAllPattern() {
        uiEvents.post(Event.OnNetworkCheck)
    }

    /**
     * Events for this view model
     */
    sealed class Event {
        object OnFilterClick : Event()
        object MyLibrarySync : Event()
        object OnSearchClick : Event()
        object OnCancelClick : Event()
        object OnNetworkCheck : Event()
    }

    fun onCancelSearchClick() {
        uiEvents.post(Event.OnCancelClick)
    }

    fun onFilterClick() {
        uiEvents.post(Event.OnFilterClick)
    }

    fun onSyncClick() {
        uiEvents.post(Event.MyLibrarySync)
    }

    fun onSearchClick() {
        uiEvents.post(Event.OnSearchClick)
    }


}
