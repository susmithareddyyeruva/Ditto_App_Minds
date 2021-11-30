package com.ditto.mylibrary.data.api

import com.ditto.mylibrary.domain.model.PatternIdData
import com.ditto.mylibrary.domain.model.TailornovaTrialPatternResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url


interface TailornovaApiService {

    @GET
    fun getPatternDetailsByDesignId(@Url url : String?, @Query("os") one: String?,@Query("mannequinId") mannequinId: String? ) : Single<PatternIdData>

    @GET
    fun getTrialPatterns(@Url url: String?): Single<TailornovaTrialPatternResponse>
}

