package core.ui.errors


import com.google.gson.annotations.SerializedName

data class Fault(
    @SerializedName("message")
    var message: String,
    @SerializedName("type")
    var type: String
)