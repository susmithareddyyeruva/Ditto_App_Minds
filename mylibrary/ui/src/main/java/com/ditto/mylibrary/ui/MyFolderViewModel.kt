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
import core.CUSTOMER_EMAIL
import core.event.UiEvents
import core.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import non_core.lib.error.Error
import non_core.lib.error.NoNetworkError
import org.json.JSONObject
import javax.inject.Inject

class MyFolderViewModel @Inject constructor(private val getPatternsData: GetMylibraryData) :
    BaseViewModel() {
    private val uiEvents = UiEvents<MyFolderViewModel.Event>()
    val events = uiEvents.stream()
    val clickedId: ObservableInt = ObservableInt(-1)
    var mutableLiveData: MutableLiveData<List<MyLibraryData>> = MutableLiveData()
    var errorString: ObservableField<String> = ObservableField("")
    var userId: Int = 0
    val isLoading: ObservableBoolean = ObservableBoolean(false)
    val isFilterResult: ObservableBoolean = ObservableBoolean(false)
    var myfolderList: MutableLiveData<List<ProdDomain>> = MutableLiveData()
    var myfolderArryList = mutableListOf<ProdDomain>()
    var patterns = MutableLiveData<ArrayList<ProdDomain>>()
    var myfolderMap = HashMap<String, List<String>>()
    val myfolderMenu = hashMapOf<String, ArrayList<FilterItems>>()
    val resultmapFolder = hashMapOf<String, ArrayList<String>>()
    var totalPageCount: Int = 0
    var totalPatternCount: Int = 0
    var currentPageId: Int = 1
    var isFilterApplied: Boolean? = false
    var clickedFolderName: String? = ""
    var isFilter: Boolean? = false

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
        uiEvents.post(Event.OnMyFolderItemClick)
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
                uiEvents.post(Event.OnMyFolderResultFailed)
            }
        }
    }

    //fetch data from repo (via usecase)
    fun fetchOnPatternData(
        createJson: MyLibraryFilterRequestData
    ) {

        uiEvents.post(Event.OnMyFolderShowProgress)
        disposable += getPatternsData.invoke(createJson)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }

    private fun handleFetchResult(result: Result<AllPatternsDomain>) {
        uiEvents.post(Event.OnMyFolderHideProgress)
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
                uiEvents.post(Event.OnMyFolderResultSuccess)
                if (isFilterApplied == false) {
                    setList()  // For Displaying menu item without any filter applied
                    uiEvents.post(Event.OnMyFolderUpdateDefaultFilter)
                    isFilterResult.set(false)
                } else {
                    uiEvents.post(Event.OnMyFolderUpdateFilterImage)
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
        uiEvents.post(Event.OnMyFolderCreateFolderClicked)
    }
    fun navigateToFolderDetails(title: String) {
        clickedFolderName=title
        uiEvents.post(Event.OnNavigtaionToFolderDetail)
    }

    fun createJson(currentPage: Int, value: String): MyLibraryFilterRequestData {
        val filterCriteria = MyLibraryFilterRequestData(
            OrderFilter(
                true,
                CUSTOMER_EMAIL,
                true,
                true,
                trialPattern = false
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
        if (filteredMap.isNotEmpty() && value.isNotEmpty()) {
            isFilter = true
        } else if (filteredMap.isEmpty() && value.isEmpty()) {
            isFilter = false
        } else if (filteredMap.isNotEmpty() && value.isEmpty()) {
            isFilter = true
        } else if (filteredMap.isNotEmpty()) {
            isFilter = true
        } else if (filteredMap.isEmpty() && value.isNotEmpty()) {
            isFilter = false
        }

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
        uiEvents.post(Event.MyFolderSyncClick)
    }

    fun onSearchClick() {
        Log.d("pattern", "onSearchClick : viewModel")
        uiEvents.post(Event.OnMyFolderSearchClick)
    }
    sealed class Event {
        object OnMyFolderItemClick : MyFolderViewModel.Event()
        object OnMyFolderDataUpdated : MyFolderViewModel.Event()
        object OnMyFolderCreateFolderClicked : MyFolderViewModel.Event()
        object OnNavigtaionToFolderDetail : MyFolderViewModel.Event()
        object OnFolderCreated : MyFolderViewModel.Event()
        object MyFolderSyncClick : MyFolderViewModel.Event()
        object OnMyFolderSearchClick : MyFolderViewModel.Event()
        object OnCreateFolder : MyFolderViewModel.Event()
        object OnMyFolderResultSuccess : MyFolderViewModel.Event()
        object OnMyFolderShowProgress : MyFolderViewModel.Event()
        object OnMyFolderHideProgress : MyFolderViewModel.Event()
        object OnMyFolderResultFailed : MyFolderViewModel.Event()
        object NoInternet : MyFolderViewModel.Event()
        object OnMyFolderUpdateFilterImage : MyFolderViewModel.Event()
        object OnMyFolderUpdateDefaultFilter : MyFolderViewModel.Event()
    }
}