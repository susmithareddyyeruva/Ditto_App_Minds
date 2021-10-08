package core.ui.errors


import com.google.gson.annotations.SerializedName

data class CommonError(
    @SerializedName("action")
    val action: String,
    @SerializedName("errorMsg")
    val errorMsg: String,
    @SerializedName("locale")
    val locale: String,
    @SerializedName("queryString")
    val queryString: String
)