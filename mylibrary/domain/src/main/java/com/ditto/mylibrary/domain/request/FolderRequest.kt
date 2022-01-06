package com.ditto.mylibrary.domain.request

data class FolderRequest(
    var orderFilter: OrderFilter,
    var foldersConfig: HashMap<String,ArrayList<String>>,
)