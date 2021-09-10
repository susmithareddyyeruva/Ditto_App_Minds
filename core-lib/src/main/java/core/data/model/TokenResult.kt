package core.data.model

import com.google.gson.annotations.SerializedName

data class TokenResult (
	@SerializedName("response") val response : TokenDetails,
	@SerializedName("errors") val errors : Errors
)