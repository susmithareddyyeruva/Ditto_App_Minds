package com.ditto.login.data.api

import com.ditto.login.data.model.LoginRequest
import com.ditto.login.data.model.LoginResult
import com.ditto.storage.data.model.User
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginService {
    @Headers("Content-Type: application/json")
    @POST("customers/auth?")
    fun userLogin(): Single<User>

    @Headers("Content-Type: application/json")
    @POST("customers/auth?")
    fun loginWithCredential(@Query("client_id") client_id: String?, @Body loginRequest: LoginRequest): Single<LoginResult>
}