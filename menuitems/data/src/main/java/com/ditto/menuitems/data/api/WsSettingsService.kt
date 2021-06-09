package com.ditto.menuitems.data.api

import com.ditto.menuitems.domain.model.LoginResult
import com.ditto.menuitems.domain.model.WSSettingsInputData
import core.appstate.AppState
import io.reactivex.Single
import okhttp3.Response
import retrofit2.http.*
import non_core.lib.Result

interface WsSettingsService {
    @Headers("Content-Type: application/json")
    @POST("customers/{Customer_ID}?method=PATCH")
    fun postSettingRequest(
        @Path("Customer_ID") custid : String?,
        @Query("client_id") client_id: String?,
        @Body settingsData: WSSettingsInputData, @Header("Authorization") header:String): Single<Result<LoginResult>>
}