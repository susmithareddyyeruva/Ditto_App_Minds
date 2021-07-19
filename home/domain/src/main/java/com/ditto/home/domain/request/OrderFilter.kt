package com.ditto.home.domain.request

data class OrderFilter(
    var allQuery: Boolean,
    var emailID: String,
    var purchasedPattern: Boolean,
    var subscriptionList: Boolean
)