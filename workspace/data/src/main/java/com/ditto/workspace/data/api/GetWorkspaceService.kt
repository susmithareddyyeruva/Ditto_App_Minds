package com.ditto.workspace.data.api

import com.ditto.workspace.data.model.WSInputData
import com.ditto.workspace.data.model.WSUpdateResult
import com.ditto.workspace.data.model.WorkspaceResult
import io.reactivex.Single
import retrofit2.http.*

interface GetWorkspaceService {
    @Headers("Content-Type: application/json")
    @GET(core.lib.BuildConfig.COMMON_ENDURL+"custom_objects/traceWorkSpace/1_1_22?")
    fun getWorkspceDataFromApi(@Query("client_id") client_id:String):Single<WorkspaceResult>

    @Headers("Content-Type: application/json")
    @POST(core.lib.BuildConfig.WORKSPACE_ENDURL+"custom_objects/traceWorkSpace/1_1_11?method=PATCH")
    fun updateWorkspaceDataFromApi(
        @Query("client_id") client_id: String?,
        @Query("site_id") site_id: String?,
        @Body wsInputData: WSInputData, @Header("Authorization") header:String): Single<WSUpdateResult>

    @Headers("Content-Type: application/json")
    @PUT(core.lib.BuildConfig.WORKSPACE_ENDURL+"custom_objects/traceWorkSpace/1_1_122?")
    fun createWorkspaceDataFromApi(
        @Query("client_id") client_id: String?,
        @Query("site_id") site_id: String?,
        @Body wsInputData: WSInputData, @Header("Authorization") header:String): Single<WSUpdateResult>
}
