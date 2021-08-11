package core.di


import core.CLIENT_ID_DEV
import core.data.model.TokenResult
import core.lib.BuildConfig
import io.reactivex.Single
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST(BuildConfig.TOKEN_ENDURL+"?client_id=$CLIENT_ID_DEV")
    fun refreshToken(@FieldMap req : Map<String, String> ): Single <TokenResult>

}