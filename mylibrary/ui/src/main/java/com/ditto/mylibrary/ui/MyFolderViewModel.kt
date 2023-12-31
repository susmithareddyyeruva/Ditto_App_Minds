package com.ditto.mylibrary.ui

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.domain.MyLibraryUseCase
import com.ditto.mylibrary.domain.model.*
import com.ditto.mylibrary.domain.request.*
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
import non_core.lib.whileSubscribed
import org.json.JSONObject
import javax.inject.Inject

class MyFolderViewModel @Inject constructor(private val myLibraryUseCase: MyLibraryUseCase) :
    BaseViewModel() {
    @Inject
    lateinit var loggerFactory: LoggerFactory

    val logger: Logger by lazy {
        loggerFactory.create(MyFolderViewModel::class.java.simpleName)
    }
    private val uiEvents = UiEvents<MyFolderViewModel.Event>()
    val events = uiEvents.stream()
    var clickedTailornovaID: ObservableField<String> = ObservableField("")
    var clickedOrderNumber: ObservableField<String> = ObservableField("")
    var clickedProduct: ProdDomain? = null
    var mutableLiveData: MutableLiveData<List<MyLibraryData>> = MutableLiveData()
    var errorString: ObservableField<String> = ObservableField("")
    var userId: Int = 0
    val isLoading: ObservableBoolean = ObservableBoolean(false)
    val isFilterResult: ObservableBoolean = ObservableBoolean(false)
    var myfolderList: MutableLiveData<List<ProdDomain>> = MutableLiveData()
    var patterns = MutableLiveData<ArrayList<ProdDomain>>()
    var folderTitle: String? = ""
    var myfolderMap = HashMap<String, List<String>>()
    val myfolderMenu = hashMapOf<String, ArrayList<FilterItems>>()
    val resultmapFolder = hashMapOf<String, ArrayList<String>>()
    var totalPageCount: Int = 0
    var totalPatternCount: Int = 0
    var currentPageId: Int = 1
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

    fun onItemClickPattern(id: String, orderNumber: String, pattern: ProdDomain) {
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
            clickedTailornovaID.set(id)
            clickedOrderNumber.set(orderNumber)
            clickedProduct = pattern
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
        disposable += myLibraryUseCase.getPatterns(createJson)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }

    private fun isFolderPresent(newFolderName: String): Boolean {
        folderList.forEach {
            if (it.title.equals(newFolderName, true)) {
                logger.d("FOLDER, ALready exist")
                return true
            }
        }
        return false
    }

    fun addToFolder(product: ProdDomain, newFolderName: String, action: String) {
        uiEvents.post(Event.OnMyFolderShowProgress)
        val hashMap = HashMap<String, ArrayList<String>>()
        var methodName: String = ""
        logger.d("DESIGN ID==, product.tailornovaDesignId ?: ")
        if (action == addFolder) {
            methodName = "update"
            hashMap[newFolderName] = arrayListOf(product.tailornovaDesignId ?: "")
        } else if (action == rename) {
            methodName = "rename"
        } else if (action == delete) {
            methodName = "remove"
            hashMap[folderToDelete] = ArrayList()
        }

        if ((action == rename || action == addFolder) && (newFolderName.equals(
                "favorites",
                true
            ) || newFolderName.equals("owned", true) || isFolderPresent(newFolderName))
        ) {
            uiEvents.post(Event.OnMyFolderShowAlert)
        } else {
            val favReq = FolderRequest(
                OrderFilter(
                    false,
                    AppState.getEmail(),
                    purchasedPattern = true,
                    subscriptionList = true,
                    trialPattern = false
                ),
                FoldersConfig = hashMap
            )
            if (methodName != "rename") {
                disposable += myLibraryUseCase.addFolder(favReq, methodName = methodName)
                    .subscribeOn(Schedulers.io())
                    .whileSubscribed { isLoading.set(it) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy { handleFolderApiResult(it) }
            } else {
                val renameReq = FolderRenameRequest(
                    OrderFilterRename(
                        false,
                        AppState.getEmail(),
                        purchasedPattern = true,
                        subscriptionList = true,
                        trialPattern = false,
                        oldname = folderToRename,
                        newname = newFolderName
                    )
                )
                disposable += myLibraryUseCase.renameFolder(renameReq, methodName = methodName)
                    .subscribeOn(Schedulers.io())
                    .whileSubscribed { isLoading.set(it) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy { handleFolderApiResult(it) }
            }
        }


    }


    private fun handleFolderApiResult(
        result: Result<AddFavouriteResultDomain>
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
                var temp: ArrayList<ProdDomain> =
                    if (myfolderList.value == null) ArrayList() else myfolderList.value as ArrayList<ProdDomain>
                temp?.addAll(result.data.prod)
                myfolderList.value = temp
                // myfolderList.value = result.data.prod

                /* result.data.prod.forEach {
                     myfolderArryList.add(it)
                 }*/

                //AppState.setPatternCount(result.data.totalPatternCount)
                totalPatternCount = result.data.totalPatternCount ?: 0
                logger.d("PATTERN  COUNT== , totalPatternCount.toString()")
                totalPageCount = result.data.totalPageCount ?: 0
                currentPageId = result.data.currentPageId ?: 0
                myfolderMap = result.data.menuItem ?: hashMapOf() //hashmap
                uiEvents.post(Event.OnMyFolderResultSuccess)
                if (isFilter == false) {
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
            val menuValues: ArrayList<FilterItems> = ArrayList()
            for (aString in value) {
                menuValues?.add(FilterItems(aString))

            }
            //  Filter.menuItemListFilter[key] = menuValues
            val sortedMenuValues = ArrayList(menuValues.sortedWith(compareBy { it.title }))
            myfolderMenu[key] = sortedMenuValues
        }

        logger.d("MAP  RESULT== , myfolderMenu.size.toString()")


    }

    fun getFoldersList() {
        val folderRequest = GetFolderRequest(
            OrderFilter(
                false,
                AppState.getEmail(),
                purchasedPattern = true,
                subscriptionList = true,
                trialPattern = false
            )
        )
        disposable += myLibraryUseCase.invokeFolderList(folderRequest, GETFOLDER)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFolderFetchResult(it) }

    }

    private fun handleFolderFetchResult(folderResult: Result<FoldersResultDomain>?) {
        folderList = arrayListOf(
            MyFolderData(
                R.drawable.folder_white_icon,
                "Create New Folder",
                false
            ),
            MyFolderData(
                R.drawable.folder_yellow_icon,
                "Owned",
                false
            ),
            MyFolderData(
                R.drawable.folder_yellow_icon,
                "Favorites",
                false
            )
        )
        when (folderResult) {
            is Result.OnSuccess -> {
                folderResult.data.responseStatus.forEach {
                    folderList.add(
                        MyFolderData(
                            url = R.drawable.folder_yellow_icon,
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

            else -> {}
        }
    }

    fun createFolderEvent() {
        logger.d("Testing, >>>>>>   Myfolder createFolderEvent ")
        uiEvents.post(Event.OnMyFolderCreateFolderClicked)
    }

    fun navigateToFolderDetails(title: String) {
        logger.d("Testing, >>>>>>   Myfolder navigateToFolderDetails ")
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
                    AppState.getEmail(),
                    purchasedPattern = true,
                    subscriptionList = false,
                    trialPattern = false,
                    FolderName = ""
                ), pageId = currentPage, patternsPerPage = 12, searchTerm = value
            )
        } else if (myFolderDetailHeader == "Favorites") {
            /**
             * If is Fav folder Allquery true
             */
            filterCriteria = MyLibraryFilterRequestData(
                OrderFilter(
                    false,
                    AppState.getEmail(),
                    purchasedPattern = true,
                    subscriptionList = true,
                    trialPattern = false,
                    FolderName = "Favorite"
                ), pageId = currentPage, patternsPerPage = 12, searchTerm = value
            )
        } else {
            /**
             *  all query will be true folder name not be empty
             */
            filterCriteria = MyLibraryFilterRequestData(
                OrderFilter(
                    false,
                    AppState.getEmail(),
                    purchasedPattern = true,
                    subscriptionList = true,
                    trialPattern = false,
                    FolderName = myFolderDetailHeader
                ), pageId = currentPage,
                patternsPerPage = 12,
                searchTerm = value
            )
        }

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
        resultmapFolder.clear()
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
        logger.d("JSON===, resultJson")

        return filterCriteria
    }

    fun onSyncClick() {
        logger.d("pattern, onSyncClick : viewModel")
        uiEvents.post(Event.MyFolderSyncClick)
    }

    fun onSearchClick() {
        logger.d("pattern, onSearchClick : viewModel")
        uiEvents.post(Event.OnMyFolderSearchClick)
    }


    sealed class Event {
        object OnMyFolderItemClick : MyFolderViewModel.Event()
        object OnMyFolderListUpdated : MyFolderViewModel.Event()
        object OnMyFolderCreateFolderClicked : MyFolderViewModel.Event()
        object OnNavigtaionToFolderDetail : MyFolderViewModel.Event()
        object MyFolderSyncClick : MyFolderViewModel.Event()
        object OnMyFolderSearchClick : MyFolderViewModel.Event()
        object OnMyFolderResultSuccess : MyFolderViewModel.Event()
        object OnNewFolderAdded : MyFolderViewModel.Event()
        object OnFolderRemoved : MyFolderViewModel.Event()
        object OnFolderRenamed : MyFolderViewModel.Event()
        object OnMyFolderShowProgress : MyFolderViewModel.Event()
        object OnMyFolderHideProgress : MyFolderViewModel.Event()
        object OnMyFolderResultFailed : MyFolderViewModel.Event()
        object OnMyFolderShowAlert : MyFolderViewModel.Event()
        object NoInternet : MyFolderViewModel.Event()
        object OnMyFolderUpdateFilterImage : MyFolderViewModel.Event()
        object OnMyFolderUpdateDefaultFilter : MyFolderViewModel.Event()
    }
}