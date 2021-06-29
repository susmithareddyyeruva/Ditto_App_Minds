package com.ditto.home.ui

import android.util.Log
import androidx.databinding.ObservableField
import com.ditto.home.domain.MyLibraryUseCase
import com.ditto.home.domain.model.HomeData
import com.ditto.home.domain.model.MyLibraryDetailsDomain
import com.ditto.storage.domain.StorageManager
import com.example.home_ui.R
import core.USER_FIRST_NAME
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
import javax.inject.Inject

class HomeViewModel @Inject constructor(val storageManager: StorageManager, val useCase: MyLibraryUseCase) : BaseViewModel() {
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    val homeItem: ArrayList<HomeData> = ArrayList()
    var header: ObservableField<String> = ObservableField()
    var errorString: ObservableField<String> = ObservableField("")

    sealed class Event {
        object OnClickMyPatterns : Event()
        object OnClickDitto : Event()
        object OnClickJoann : Event()
        object OnClickTutorial : Event()
        object OnResultSuccess : HomeViewModel.Event()
        object OnShowProgress : HomeViewModel.Event()
        object OnHideProgress : HomeViewModel.Event()
        object OnResultFailed : HomeViewModel.Event()
        object NoInternet : HomeViewModel.Event()
    }

    init {
        setHomeHeader()
        setHomeItems()
    }

    fun onItemClick(id: Int) {
        when (id) {
            0 -> {
                uiEvents.post(Event.OnClickMyPatterns)
            }
            1 -> {
                uiEvents.post(Event.OnClickDitto)
            }
            2 -> {
                uiEvents.post(Event.OnClickJoann)
            }
            3 -> {
                uiEvents.post(Event.OnClickTutorial)
            }
        }
    }

    fun setHomeHeader() {
        if (!AppState.getIsLogged()) {
            isGuest.set(true)
            header.set("Hi there,")
        } else {
            isGuest.set(false)
            header.set(storageManager.getStringValue(USER_FIRST_NAME).toString())
        }
    }

    fun setHomeItems() {
        val images = intArrayOf(
            R.drawable.ic_home_pattern_library, R.drawable.ic_home_ditto,
            R.drawable.ic_home_joann, R.drawable.ic_home_tutorial
        )

        val title = intArrayOf(
            R.string.pattern_library_count, R.string.more_patterns_available_at,
            R.string.for_fine_crafts_and_fabrics_visit_our_site, R.string.beam_setup_and_calibration
        )

        val description = intArrayOf(
            R.string.all_your_patterns_in_one_place, R.string.ditto_patterns_site,
            R.string.joann_site, R.string.view_tutorial
        )

        for (item in images.indices) {
            var homeItems: HomeData = HomeData(
                item,
                title[item],
                description[item],
                images[item],
            )
            homeItem.add(homeItems)
        }
    }
    fun fetchData() {
        disposable += useCase.getMyLibraryDetails()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }


    }
    /**
     * Handling fetch result here.....
     */
    private fun handleFetchResult(result: Result<MyLibraryDetailsDomain>?) {
        uiEvents.post(Event.OnHideProgress)
        when (result) {
            is Result.OnSuccess-> {
                uiEvents.post(Event.OnHideProgress)
                uiEvents.post(Event.OnResultSuccess)

            }
            is Result.OnError -> {
                uiEvents.post(Event.OnHideProgress)
                Log.d("faq_glossary", "Failed")
                handleError(result.error)
            }
        }
    }
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



}