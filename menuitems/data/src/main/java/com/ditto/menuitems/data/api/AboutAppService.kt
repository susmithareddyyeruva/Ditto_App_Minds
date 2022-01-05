package com.ditto.menuitems.data.api

import com.ditto.menuitems.domain.model.AboutAppResponseData
import core.lib.BuildConfig
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface AboutAppService {
    @Headers("Content-Type: application/json")
    @GET(BuildConfig.COMMON_ENDURL+"content/privacy-policy")
    fun getAboutAndPrivacyPolicy(@Query("client_id") clientId: String? ) : Single<AboutAppResponseData>
}