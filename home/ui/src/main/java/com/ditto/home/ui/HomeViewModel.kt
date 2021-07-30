package com.ditto.home.ui

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.ditto.home.domain.MyLibraryUseCase
import com.ditto.home.domain.model.HomeData
import com.ditto.home.domain.model.MyLibraryDetailsDomain
import com.ditto.storage.domain.StorageManager
import com.example.home_ui.R
import core.USER_FIRST_NAME
import core.appstate.AppState
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
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    val storageManager: StorageManager,
    val useCase: MyLibraryUseCase,
    private val utility: Utility
) : BaseViewModel() {
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    val homeItem: ArrayList<HomeData> = ArrayList()
    var header: ObservableField<String> = ObservableField()
    var errorString: ObservableField<String> = ObservableField("")
    var homeDataResponse: MutableLiveData<MyLibraryDetailsDomain> = MutableLiveData()
    var productCount: Int = 0
    val resultMap = hashMapOf<String, ArrayList<String>>()

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
        if (Utility.isTokenExpired()) {
            utility.refreshToken()
        }
        setHomeHeader()

    }

    fun onItemClick(id: Int) {
        when (id) {
            0 -> {
                uiEvents.post(Event.OnClickTutorial)

            }
            1 -> {
                uiEvents.post(Event.OnClickMyPatterns)

            }
            2 -> {
                uiEvents.post(Event.OnClickDitto)

            }
            3 -> {
                uiEvents.post(Event.OnClickJoann)
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
   /*     val images = intArrayOf(
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
        )*/
        val images = intArrayOf(
            R.drawable.ic_home_tutorial, R.drawable.ic_home_pattern_library,
            R.drawable.ic_home_ditto, R.drawable.ic_home_joann
        )

        val title = intArrayOf(
            R.string.beam_setup_and_calibration, R.string.pattern_library_count,
            R.string.more_patterns_available_at, R.string.shop_fabric_supplies
        )

        val description = intArrayOf(
            R.string.view_tutorial,  R.string.all_your_patterns_in_one_place, R.string.ditto_patterns_site,
            R.string.joann_site
        )
        for (item in images.indices) {
            var homeItems: HomeData = HomeData(
                item,
                title[item],
                description[item],
                images[item]
            )
            homeItem.add(homeItems)

        }


    }

    fun fetchData() {
        uiEvents.post(Event.OnShowProgress)
        disposable += useCase.getMyLibraryDetails(
            com.ditto.home.domain.request.MyLibraryFilterRequestData(
                com.ditto.home.domain.request.OrderFilter(
                    true,
                    "subscustomerOne@gmail.com",
                    true,
                    true
                ), ProductFilter = resultMap,patternsPerPage = 12,pageId = 1
            )
        )
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
            is Result.OnSuccess -> {
                uiEvents.post(Event.OnHideProgress)
                homeDataResponse.value = result.data
                Log.d("Home Screen", "$homeDataResponse.value.prod.size")
                productCount = homeDataResponse.value!!.totalPatternCount
                AppState.setPatternCount(productCount)
                Log.d("Home Screen", "${productCount}")
                setHomeItems()  //Preparing menu items
                uiEvents.post(Event.OnResultSuccess)
            }
            is Result.OnError -> {
                uiEvents.post(Event.OnHideProgress)
                uiEvents.post(Event.OnResultFailed)
                Log.d("Home Screen", "Failed")
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