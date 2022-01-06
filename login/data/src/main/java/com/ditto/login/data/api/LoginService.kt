package com.ditto.login.data.api

import com.ditto.login.data.model.LandingContentResult
import com.ditto.login.data.model.LoginRequest
import com.ditto.login.data.model.LoginResult
import core.lib.BuildConfig
import io.reactivex.Single
import retrofit2.http.*

interface LoginService {
    @Headers("Content-Type: application/json")
    @POST(BuildConfig.COMMON_ENDURL+"customers/auth?")
    fun loginWithCredential(
        @Query("client_id") clientId: String?,
        @Body loginRequest: LoginRequest, @Header("Authorization") header: String
    ): Single<LoginResult>

    @Headers("Content-Type: application/json")
    @GET(BuildConfig.COMMON_ENDURL+"content/LandingContent?")
    fun getLandingContentDetails( @Query("client_id") clientId: String?): Single<LandingContentResult>
}