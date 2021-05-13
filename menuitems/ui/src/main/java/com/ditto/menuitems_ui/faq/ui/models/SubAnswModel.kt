package com.ditto.menuitems_ui.faq.ui.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SubAnswModel (
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("short_description")
    var description: String? = null,
    @SerializedName("image_path")
    var image_path: String? = null
) : Serializable