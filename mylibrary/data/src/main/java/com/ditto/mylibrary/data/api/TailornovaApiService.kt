package com.ditto.mylibrary.data.api

import com.ditto.mylibrary.domain.model.PatternIdData
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface TailornovaApiService {

    @GET("designId")
    fun getPatternDetailsByDesignId(@Query("designId") designId : String? ) : Single<PatternIdData>

}

