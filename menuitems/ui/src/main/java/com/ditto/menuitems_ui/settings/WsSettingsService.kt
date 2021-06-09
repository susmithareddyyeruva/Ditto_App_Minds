package com.ditto.menuitems_ui.settings

import com.ditto.menuitems_ui.settings.data.LoginResult
import com.ditto.menuitems_ui.settings.data.SettingsRequestData
import com.ditto.menuitems_ui.settings.model.WSSettingsInputData
import io.reactivex.Single
import retrofit2.http.*


interface WsSettingsService {
    @Headers("Content-Type: application/json")
    @POST("customers/auth?")
    fun postSettingRequest(
        @Query("client_id") client_id: String?,
        @Body settingsData: WSSettingsInputData, @Header("Authorization") header:String): Single<Result<LoginResult>>
}