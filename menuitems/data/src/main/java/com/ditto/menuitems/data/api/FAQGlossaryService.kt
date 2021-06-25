package com.ditto.menuitems.data.api

import com.ditto.menuitems.data.model.faq.FAqGlossaryResultClass
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface FAQGlossaryService {
    @Headers("Content-Type: application/json")
    @GET("content/hamburgerAsset?")
    fun getFAQGlossaryData(@Query("client_id") client_id: String?): Single<FAqGlossaryResultClass>

}