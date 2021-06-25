package com.ditto.menuitems.data.model.faq


import com.google.gson.annotations.SerializedName

data class FAQ(
    @SerializedName("Answ")
    val answ: String,
    @SerializedName("Ques")
    val ques: String,
    @SerializedName("SubAnsw")
    val subAnsw: List<SubAnsw>,
    @SerializedName("video_url")
    val videoUrl: String,
    @SerializedName("web_url")
    val webUrl: String
)