package core.data.model

import com.google.gson.annotations.SerializedName

data class TokenDetails(
    @SerializedName("access_token") val access_token : String?="",
    @SerializedName("expires_in") val expires_in : Int?=0,
    @SerializedName("token_type") val token_type : String?="",
    @SerializedName("sk") val sk : Int?=0

)