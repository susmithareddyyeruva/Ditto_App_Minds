package com.ditto.mylibrary.model

import com.google.gson.annotations.SerializedName

data class MannequinData(@SerializedName("mannequinId") val mannequinId : String?="",
                         @SerializedName("mannequinName") val mannequinName : String?="",)