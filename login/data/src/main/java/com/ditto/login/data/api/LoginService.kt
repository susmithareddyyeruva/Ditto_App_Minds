package com.ditto.login.data.api

import com.ditto.login.data.model.LandingContentResult
import com.ditto.login.data.model.LoginRequest
import com.ditto.login.data.model.LoginResult
import io.reactivex.Single
import retrofit2.http.*

interface LoginService {
    @Headers("Content-Type: application/json")
    @POST("customers/auth?")
    fun loginWithCredential(
        @Query("client_id") client_id: String?,
        @Body loginRequest: LoginRequest, @Header("Authorization") header: String
    ): Single<LoginResult>

    @Headers("Content-Type: application/json")
    @GET("content/LandingContent?")
    fun getLandingContentDetails( @Query("client_id") client_id: String?): Single<LandingContentResult>
}