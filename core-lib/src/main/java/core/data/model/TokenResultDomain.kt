package core.data.model

import com.google.gson.annotations.SerializedName

class TokenResultDomain(@SerializedName("response") val response : TokenDetails,
                        @SerializedName("errors") val errors : Errors)