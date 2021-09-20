package com.ditto.mylibrary.ui

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.ditto.mylibrary.domain.GetMylibraryData
import com.ditto.mylibrary.domain.model.*
import com.ditto.mylibrary.domain.request.MyLibraryFilterRequestData
import com.ditto.mylibrary.domain.request.OrderFilter
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
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MyFolderViewModel @Inject constructor(private val getPatternsData: GetMylibraryData) :
    BaseViewModel() {
    private val uiEvents = UiEvents<MyFolderViewModel.Event>()
    val events = uiEvents.stream()
    var clickedTailornovaID: ObservableField<String> = ObservableField("")//todo
    var clickedOrderNumber: ObservableField<String> = ObservableField("")//todo
    var mutableLiveData: MutableLiveData<List<MyLibraryData>> = MutableLiveData()
    var errorString: ObservableField<String> = ObservableField("")
    var userId: Int = 0
    val isLoading: ObservableBoolean = ObservableBoolean(false)
    val isFilterResult: ObservableBoolean = ObservableBoolean(false)
    var myfolderList: MutableLiveData<List<ProdDomain>> = MutableLiveData()
    var myfolderArryList = mutableListOf<ProdDomain>()
    var patterns = MutableLiveData<ArrayList<ProdDomain>>()
    var folderTitle: String? = ""
    var myfolderMap = HashMap<String, List<String>>()
    val myfolderMenu = hashMapOf<String, ArrayList<FilterItems>>()
    val resultmapFolder = hashMapOf<String, ArrayList<String>>()
    var totalPageCount: Int = 0
    var totalPatternCount: Int = 0
    var currentPageId: Int = 1
    var isFilterApplied: Boolean? = false
    var clickedFolderName: String? = ""

    fun onItemClickPattern(id: String,orderNumber: String) {
        if (id == "10140549") {
            clickedTailornovaID.set("1")
            clickedOrderNumber.set(orderNumber)
        } else if (id == "10544781") {
            clickedTailornovaID.set("2")
            clickedOrderNumber.set(orderNumber)
        } else if (id == "10140606") {
            clickedTailornovaID.set("3")
            clickedOrderNumber.set(orderNumber)
        } else {
            clickedTailornovaID.set("4")
            clickedOrderNumber.set(orderNumber)
        }
        uiEvents.post(Event.OnItemClick)
    }

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
        uiEvents.post(Event.OnHideProgress)
        when (result) {
            is Result.OnSuccess -> {
                myfolderList.value = result.data.prod

                result.data.prod.forEach {
                    myfolderArryList.add(it)
                }

                //AppState.setPatternCount(result.data.totalPatternCount)
                totalPatternCount = result.data.totalPatternCount ?: 0
                Log.d("PATTERN  COUNT== ", totalPatternCount.toString())
                totalPageCount = result.data.totalPageCount ?: 0
                currentPageId = result.data.currentPageId ?: 0
                myfolderMap = result.data.menuItem ?: hashMapOf() //hashmap
                uiEvents.post(Event.OnResultSuccess)
                if (isFilterApplied == false) {
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

    fun setList() {

        for ((key, value) in myfolderMap) {
            var menuValues: ArrayList<FilterItems> = ArrayList()
            for (aString in value) {
                menuValues?.add(FilterItems(aString))

            }
            //  Filter.menuItemListFilter[key] = menuValues
            myfolderMenu[key] = menuValues
        }

        Log.d("MAP  RESULT== ", myfolderMenu.size.toString())
        uiEvents.post(Event.OnUpdateFilter)

    }

    fun getList(): List<MyFolderData> {
        val list = listOf<MyFolderData>(
            MyFolderData(
                R.drawable.ic_newfolder,
                "Add Folder",
                false
            ),
            MyFolderData(
                R.drawable.ic_owned,
                "Owned",
                false
            ),
            MyFolderData(
                0,
                "Favorites",
                true
            ),
            MyFolderData(
                0,
                "Emma's Patterns",
                true
            )
        )
        return list
    }

    fun onCreateFoldersSuccess() {
        Log.d("pattern", "onSearchClick : viewModel")
        uiEvents.post(Event.OnFolderCreated)
    }

    fun createFolderEvent() {
        uiEvents.post(Event.OnCreateFolderClicked)
    }
    fun navigateToFolderDetails(title: String) {
        clickedFolderName=title
        uiEvents.post(Event.OnNavigtaionToFolderDetail)
    }

    fun createJson(currentPage: Int, value: String): MyLibraryFilterRequestData {
        val filterCriteria = MyLibraryFilterRequestData(
            OrderFilter(
                true,
                "subscustomerOne@gmail.com",
                true,
                true
            ), pageId = currentPage, patternsPerPage = 12, searchTerm = value
        )
        val json1 = Gson().toJson(myfolderMenu)
        Log.d("JSON===", json1)
        val filteredMap: HashMap<String, Array<FilterItems>> = HashMap()
        myfolderMenu.forEach { (key, value) ->
            val filtered = value.filter { prod -> prod.isSelected }
            if (filtered.isNotEmpty()) {
                filteredMap[key] = filtered.toTypedArray()

            }
        }
        isFilterApplied = !(filteredMap.isEmpty() && value.isEmpty())
        val jsonProduct = JSONObject()
        for ((key, value) in filteredMap) {
            var arraYlist = ArrayList<String>()
            for (result in value) {
                arraYlist.add(result.title)
                resultmapFolder[key] = arraYlist
                jsonProduct.put(key, arraYlist)


            }


        }
        filterCriteria.ProductFilter = resultmapFolder
        val resultJson = Gson().toJson(resultmapFolder)
        Log.d("JSON===", resultJson)

        val jsonString: String = resultJson

        val resultString: String = resultJson.substring(1, resultJson.toString().length - 1)
        Log.d("RESULT STRING===", resultString)
        return filterCriteria
    }
    fun onSyncClick() {
        Log.d("pattern", "onSyncClick : viewModel")
        uiEvents.post(Event.OnSyncClick)
    }

    fun onSearchClick() {
        Log.d("pattern", "onSearchClick : viewModel")
        uiEvents.post(Event.OnSearchClick)
    }
    sealed class Event {
        object OnItemClick : MyFolderViewModel.Event()
        object OnDataUpdated : MyFolderViewModel.Event()
        object OnCreateFolderClicked : MyFolderViewModel.Event()
        object OnNavigtaionToFolderDetail : MyFolderViewModel.Event()
        object OnFolderCreated : MyFolderViewModel.Event()
        object OnSyncClick : MyFolderViewModel.Event()
        object OnSearchClick : MyFolderViewModel.Event()
        object OnCreateFolder : MyFolderViewModel.Event()
        object OnResultSuccess : MyFolderViewModel.Event()
        object OnShowProgress : MyFolderViewModel.Event()
        object OnHideProgress : MyFolderViewModel.Event()
        object OnResultFailed : MyFolderViewModel.Event()
        object NoInternet : MyFolderViewModel.Event()
        object OnUpdateFilter : MyFolderViewModel.Event()
        object UpdateFilterImage : MyFolderViewModel.Event()
        object UpdateDefaultFilter : MyFolderViewModel.Event()
    }
}