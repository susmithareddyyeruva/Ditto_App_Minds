package core.di


import core.data.model.TokenResult
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService {
    @Headers("Content-Type: application/json")
    @GET("session")
    fun refreshToken(): Single<TokenResult>

    @Headers("Content-Type: application/json")
    @GET("session")
    suspend fun refreshTokenAuthentication(): TokenResult

}