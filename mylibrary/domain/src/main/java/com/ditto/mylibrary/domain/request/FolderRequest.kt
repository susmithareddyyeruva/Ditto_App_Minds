package com.ditto.mylibrary.domain.request

data class FolderRequest(
    var OrderFilter: OrderFilter,
    var FoldersConfig: HashMap<String,ArrayList<String>>,
)