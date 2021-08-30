package com.ditto.mylibrary.ui

import android.util.Log
import com.ditto.mylibrary.domain.model.MyFolderData
import core.event.UiEvents
import core.ui.BaseViewModel
import javax.inject.Inject

class MyFolderViewModel @Inject constructor() : BaseViewModel() {
    private val uiEvents = UiEvents<MyFolderViewModel.Event>()
    val events = uiEvents.stream()
    fun getList(): List<MyFolderData> {
        val list = listOf<MyFolderData>(
            MyFolderData(R.drawable.ic_newfolder,
                "Add Folder",
                false
            ),
            MyFolderData(R.drawable.ic_owned,
                "Owned",
                false
            ),
            MyFolderData(0,
                "Favorites",
                true
            ),
            MyFolderData(0,
                "Emma's Patterns",
                true
            )
        )
        return list
    }
    fun onCreateFoldersSuccess() {
        Log.d("pattern", "onSearchClick : viewModel")
        uiEvents.post(MyFolderViewModel.Event.OnFolderCreated)
    }
    fun createFolderEvent(){
      uiEvents.post(MyFolderViewModel.Event.OnCreateFolderClicked)
    }
   sealed class Event{
       object OnCreateFolderClicked : MyFolderViewModel.Event()
       object OnFolderCreated : MyFolderViewModel.Event()
   }
}