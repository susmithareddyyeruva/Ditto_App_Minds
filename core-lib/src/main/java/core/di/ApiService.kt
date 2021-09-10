package core.di


import core.data.model.TokenResult
import io.reactivex.Single
import retrofit2.http.GET

interface ApiService {
    @GET("session")
    fun refreshToken(): Single<TokenResult>

}