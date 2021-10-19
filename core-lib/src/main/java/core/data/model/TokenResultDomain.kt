package core.data.model

import com.google.gson.annotations.SerializedName
import core.ui.errors.TokenError

class TokenResultDomain(@SerializedName("response") val response : TokenDetails,
                        @SerializedName("errors") val errors : TokenError)