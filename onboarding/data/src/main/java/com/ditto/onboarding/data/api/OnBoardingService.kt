package com.ditto.onboarding.data.api

import com.ditto.onboarding.data.model.OnBoardingResult
import core.lib.BuildConfig
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface OnBoardingService {
    @Headers("Content-Type: application/json")
    @GET(BuildConfig.COMMON_ENDURL+"content/tutorial?")
    fun getContentApi( @Query("client_id") client_id: String?): Single<OnBoardingResult>
}