package com.ditto.menuitems.data.api

import com.ditto.menuitems.data.model.WSProUpdateResult
import com.ditto.menuitems.domain.model.WSSettingsInputData
import core.lib.BuildConfig
import io.reactivex.Single
import retrofit2.http.*

interface WsSettingsService {
    @Headers("Content-Type: application/json")
    @POST(BuildConfig.COMMON_ENDURL+"customers/{Customer_ID}?method=PATCH")
    fun postSettingRequest(
        @Path("Customer_ID") custid : String?,
        @Query("client_id") clientId: String?,
        @Body settingsData: WSSettingsInputData, @Header("Authorization") header:String): Single<WSProUpdateResult>
}