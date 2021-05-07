package com.ditto.onboarding.data.model


import com.google.gson.annotations.SerializedName

data class CBody(
    @SerializedName("onboarding")
    var onboarding: List<Onboarding>
)