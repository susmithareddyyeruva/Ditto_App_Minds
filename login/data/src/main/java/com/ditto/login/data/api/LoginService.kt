package com.ditto.login.data.api

import com.ditto.storage.data.model.User
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers

interface LoginService {
    @Headers("Content-Type: application/json")
    @GET("/api/unknown")
    fun userLogin(): Single<User>
}