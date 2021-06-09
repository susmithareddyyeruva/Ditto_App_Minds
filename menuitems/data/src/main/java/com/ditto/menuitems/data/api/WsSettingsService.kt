package com.ditto.menuitems.data.api

import com.ditto.menuitems.domain.model.WSSettingsInputData
import io.reactivex.Single
import retrofit2.http.*


interface WsSettingsService {
    @Headers("Content-Type: application/json")
    @POST("customers/auth?")
    fun postSettingRequest(
        @Query("client_id") client_id: String?,
        @Body settingsData: WSSettingsInputData, @Header("Authorization") header:String): Single<Boolean>
}