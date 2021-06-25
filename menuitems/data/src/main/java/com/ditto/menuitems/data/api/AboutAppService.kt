package com.ditto.menuitems.data.api

import com.ditto.menuitems.domain.model.AboutAppResponseData
import io.reactivex.Single
import non_core.lib.Result
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface AboutAppService {
    @Headers("Content-Type: application/json")
    @GET("content/privacy-policy")
    fun getAboutAndPrivacyPolicy(@Query("client_id") client_id: String? ) : Single<AboutAppResponseData>
}