package com.ditto.data.api

import com.ditto.data.api.request.MyLibraryRequestData
import com.ditto.data.model.MyLibraryResult
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface MyLibraryService {
    @Headers("Content-Type: application/json")
    @POST("default/MySubsFilter-Shows")
    fun getHomeScreenDetails(@Body body: MyLibraryRequestData?): Single<MyLibraryResult>


}