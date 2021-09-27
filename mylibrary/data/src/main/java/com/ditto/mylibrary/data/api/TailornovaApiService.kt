package com.ditto.mylibrary.data.api

import com.ditto.mylibrary.domain.model.PatternIdData
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.Url


interface TailornovaApiService {

    @GET
    fun getPatternDetailsByDesignId(@Url url : String?, @Query("os") one: String? ) : Single<PatternIdData>

}

