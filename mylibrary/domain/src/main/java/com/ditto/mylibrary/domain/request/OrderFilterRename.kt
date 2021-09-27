package com.ditto.mylibrary.domain.request


data class OrderFilterRename(
    var allQuery: Boolean,
    var emailID: String,
    var purchasedPattern: Boolean,
    var subscriptionList: Boolean,
    var trialPattern: Boolean? = false,
    var oldname: String,
    var newname: String
)