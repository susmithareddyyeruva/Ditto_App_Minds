package core.di


import core.data.model.TokenResult
import core.data.model.VersionResponse
import core.lib.BuildConfig
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {
    @Headers("Content-Type: application/json")
    @GET("session")
    fun refreshToken(): Single<TokenResult>

    @Headers("Content-Type: application/json")
    @GET("session")
    suspend fun refreshTokenAuthentication(): TokenResult

    @Headers("Content-Type: application/json")
    @GET("version")
    fun checkVersion(@Query("v") appVersion: String? = BuildConfig.VERSION_NAME, @Query("o") appOS: String? = "android"): Single<VersionResponse>

}