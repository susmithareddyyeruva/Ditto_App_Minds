package com.ditto.mylibrary.ui

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.ditto.mylibrary.domain.GetMylibraryData
import com.ditto.mylibrary.domain.model.*
import com.ditto.mylibrary.domain.request.*
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
import non_core.lib.whileSubscribed
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
    private val GETFOLDER = "getFolders"
    val addFolder = "ADD"
    val rename = "RENAME"
    val delete = "DELETE"
    var folderList = ArrayList<MyFolderData>()
    var folderToDelete: String = ""
    var folderToRename: String = ""
    var myFolderDetailHeader: String = ""

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

    fun addToFolder(product: ProdDomain, newFolderName: String, action: String) {
        uiEvents.post(Event.OnMyFolderShowProgress)
        val hashMap = HashMap<String, ArrayList<String>>()
        var methodName: String = ""
        Log.d("DESIGN ID==", product.tailornovaDesignId ?: "")
        if (action == addFolder) {
            methodName = "update"
            hashMap[newFolderName] = arrayListOf(product.tailornovaDesignId ?: "")
        } else if (action == rename) {
            methodName = "rename"
        } else if (action == delete) {
            methodName = "remove"
            hashMap[folderToDelete] = ArrayList()
        }
        val favReq = FolderRequest(
            OrderFilter(
                true,
                CUSTOMER_EMAIL,
                purchasedPattern = true,
                subscriptionList = true,
                trialPattern = false
            ),
            FoldersConfig = hashMap
        )


        if (methodName != "rename") {
            disposable += getPatternsData.addFolder(favReq, methodName = methodName)
                .subscribeOn(Schedulers.io())
                .whileSubscribed { isLoading.set(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy { handleFolderApiResult(it, product) }
        } else {
            val renameReq = FolderRenameRequest(
                OrderFilterRename(
                    true,
                    CUSTOMER_EMAIL,
                    purchasedPattern = true,
                    subscriptionList = true,
                    trialPattern = false,
                    oldname = folderToRename,
                    newname = newFolderName
                )
            )
            disposable += getPatternsData.renameFolder(renameReq, methodName = methodName)
                .subscribeOn(Schedulers.io())
                .whileSubscribed { isLoading.set(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy { handleFolderApiResult(it, product) }
        }

    }


    private fun handleFolderApiResult(
        result: Result<AddFavouriteResultDomain>,
        product: ProdDomain
    ) {
        when (result) {
            is Result.OnSuccess -> {
                if (result.data.responseStatus) {
                    if (result.data.queryString.equals("method=update")) {
                        uiEvents.post(Event.OnNewFolderAdded)
                    } else if (result.data.queryString.equals("method=remove")) {
                        uiEvents.post(Event.OnFolderRemoved)
                    } else if (result.data.queryString.equals("method=rename")) {
                        uiEvents.post(Event.OnFolderRemoved)
                    }

                }
                uiEvents.post(Event.OnMyFolderHideProgress)
            }
            is Result.OnError -> handleError(result.error)


        }
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

    fun getFoldersList() {
        val folderRequest = GetFolderRequest(
            OrderFilter(
                true,
                CUSTOMER_EMAIL,
                purchasedPattern = false,
                subscriptionList = false,
                trialPattern = true
            )
        )
        disposable += getPatternsData.invokeFolderList(folderRequest, GETFOLDER)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFolderFetchResult(it) }

    }

    private fun handleFolderFetchResult(folderResult: Result<FoldersResultDomain>?) {
        folderList = arrayListOf(
            MyFolderData(
                R.drawable.ic_newfolder,
                "Add Folder",
                false
            ),
            MyFolderData(
                R.drawable.ic_owned,
                "Owned",
                false
            )
        )
        when (folderResult) {
            is Result.OnSuccess -> {
                folderResult.data.responseStatus.forEach {
                    folderList.add(
                        MyFolderData(
                            url = null,
                            title = it,
                            isAction = true
                        )
                    )
                }
                uiEvents.post(Event.OnMyFolderListUpdated)
            }
            is Result.OnError -> {
                handleError(folderResult.error)
            }

        }
    }

    fun onCreateFoldersSuccess() {
        Log.d("pattern", "onSearchClick : viewModel")
        uiEvents.post(Event.OnFolderCreated)
    }

    fun createFolderEvent() {
        uiEvents.post(Event.OnMyFolderCreateFolderClicked)
    }

    fun navigateToFolderDetails(title: String) {
        clickedFolderName = title
        uiEvents.post(Event.OnNavigtaionToFolderDetail)
    }

    fun createJson(currentPage: Int, value: String): MyLibraryFilterRequestData {
        val filterCriteria: MyLibraryFilterRequestData
        if (myFolderDetailHeader == "Owned") {
            /**
             * If is owned folder Purchase pattern will be true and folder name will be empty
             */
            filterCriteria = MyLibraryFilterRequestData(
                OrderFilter(
                    false,
                    CUSTOMER_EMAIL,
                    purchasedPattern = true,
                    subscriptionList = false,
                    trialPattern = false,
                    FolderName = ""
                ), pageId = currentPage, patternsPerPage = 12, searchTerm = value
            )
        } else {
            /**
             *  all query will be true folder name not be empty
             */
            filterCriteria = MyLibraryFilterRequestData(
                OrderFilter(
                    true,
                    CUSTOMER_EMAIL,
                    purchasedPattern = false,
                    subscriptionList = false,
                    trialPattern = false,
                    FolderName = myFolderDetailHeader
                ), pageId = currentPage,
                patternsPerPage = 12,
                searchTerm = value
            )
        }

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
        object OnMyFolderListUpdated : MyFolderViewModel.Event()
        object OnMyFolderCreateFolderClicked : MyFolderViewModel.Event()
        object OnNavigtaionToFolderDetail : MyFolderViewModel.Event()
        object OnFolderCreated : MyFolderViewModel.Event()
        object MyFolderSyncClick : MyFolderViewModel.Event()
        object OnMyFolderSearchClick : MyFolderViewModel.Event()
        object OnCreateFolder : MyFolderViewModel.Event()
        object OnMyFolderResultSuccess : MyFolderViewModel.Event()
        object OnNewFolderAdded : MyFolderViewModel.Event()
        object OnFolderRemoved : MyFolderViewModel.Event()
        object OnFolderRenamed : MyFolderViewModel.Event()
        object OnMyFolderShowProgress : MyFolderViewModel.Event()
        object OnMyFolderHideProgress : MyFolderViewModel.Event()
        object OnMyFolderResultFailed : MyFolderViewModel.Event()
        object NoInternet : MyFolderViewModel.Event()
        object OnMyFolderUpdateFilterImage : MyFolderViewModel.Event()
        object OnMyFolderUpdateDefaultFilter : MyFolderViewModel.Event()
    }
}