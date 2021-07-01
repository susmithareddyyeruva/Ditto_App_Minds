package com.ditto.data.api

import com.ditto.data.api.request.MyLibraryRequestData
import com.ditto.data.model.MyLibraryResult
import core.lib.BuildConfig
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface MyLibraryService {
    @Headers("Content-Type: application/json")
    @POST(BuildConfig.MYLIBRARY_ENDURL+"TraceAppMyLibrary-Shows")
    fun getHomeScreenDetails(@Body body: MyLibraryRequestData?, @Header("Authorization") header:String): Single<MyLibraryResult>


}