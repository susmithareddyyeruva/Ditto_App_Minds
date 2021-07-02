package com.ditto.mylibrary.data.request

data class OrderFilter(
    var allQuery: Boolean,
    var emailID: String,
    var purchasedPattern: Boolean,
    var subscriptionList: Boolean,
    var trialPattern: Boolean
)