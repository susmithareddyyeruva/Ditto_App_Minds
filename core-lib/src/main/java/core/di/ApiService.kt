package core.di


import core.CLIENT_ID
import core.OCAPI_PASSWORD
import core.data.TokenRequest
import core.data.TokenResultDomain
import core.data.model.TokenResult
import io.reactivex.Single
import retrofit2.http.*
import non_core.lib.Result

interface ApiService {

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("oauth2/access_token?client_id=$CLIENT_ID")
    fun refreshToken(@FieldMap req : Map<String, String> ): Single <TokenResult>

}