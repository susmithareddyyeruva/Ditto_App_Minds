package com.ditto.mylibrary.ui

import com.ditto.mylibrary.domain.model.MyFolderData
import core.ui.BaseViewModel
import javax.inject.Inject

class MyFolderViewModel @Inject constructor() : BaseViewModel() {
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
}