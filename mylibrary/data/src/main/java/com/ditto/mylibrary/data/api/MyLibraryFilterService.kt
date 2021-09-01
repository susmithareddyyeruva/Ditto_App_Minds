package com.ditto.mylibrary.data.api

import com.ditto.mylibrary.domain.request.MyLibraryFilterRequestData
import com.ditto.mylibrary.model.MyLibraryResult
import core.lib.BuildConfig
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface MyLibraryFilterService {
    @Headers("Content-Type: application/json")
    @POST(BuildConfig.MYLIBRARY_ENDURL+"TraceAppMyLibrary-Shows")
    fun getAllPatternsPatterns(@Body body: MyLibraryFilterRequestData?, @Header("Authorization") header:String): Single<MyLibraryResult>

    @Headers("Content-Type: application/json")
    @POST(BuildConfig.MYLIBRARY_ENDURL+"TraceAppMyLibrary-Shows")
    fun getFilterredPatterns(@Body body: MyLibraryFilterRequestData?, @Header("Authorization") header:String): Single<MyLibraryResult>

    @Headers("Content-Type: application/json")
    @POST(BuildConfig.MYLIBRARY_ENDURL+"TraceAppMyLibrary-Shows")
    fun getFoldersList(@Body body: MyLibraryFilterRequestData?, @Header("Authorization") header:String): Single<MyLibraryResult>
}