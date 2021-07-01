package com.ditto.workspace.data.api

import com.ditto.workspace.data.model.WSInputData
import com.ditto.workspace.data.model.WSUpdateResult
import com.ditto.workspace.data.model.WorkspaceResult
import io.reactivex.Single
import retrofit2.http.*

interface GetWorkspaceService {
    @Headers("Content-Type: application/json")
    @GET("custom_objects/traceWorkSpace/1_1_11?")
    fun getWorkspceDataFromApi(@Query("client_id") client_id:String):Single<WorkspaceResult>

    @Headers("Content-Type: application/json")
    @POST("custom_objects/traceWorkSpace/1_1_11?")
    fun updateWorkspaceDataFromApi(
        @Query("client_id") client_id: String?,
        @Query("site_id") site_id: String?,
        @Body wsInputData: WSInputData, @Header("Authorization") header:String): Single<WSUpdateResult>
}
