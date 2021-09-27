package com.ditto.workspace.data.api

import com.ditto.workspace.data.model.WSInputData
import com.ditto.workspace.data.model.WSUpdateResult
import com.ditto.workspace.data.model.WorkspaceResult
import io.reactivex.Single
import retrofit2.http.*

interface GetWorkspaceService {
    @Headers("Content-Type: application/json")
    //@GET(core.lib.BuildConfig.COMMON_ENDURL+"custom_objects/traceWorkSpace/{id}?")// path id
    @GET(core.lib.BuildConfig.COMMON_ENDURL+"custom_objects/traceWorkSpace/00008501_00002507_3125c79dced64c46b92079b648c19c8f?")// path id
    fun getWorkspceDataFromApi(/*@Path ("id") id:String?,*/ @Query("client_id") client_id:String):Single<WorkspaceResult>

    @Headers("Content-Type: application/json")
    //@POST(core.lib.BuildConfig.WORKSPACE_ENDURL+"custom_objects/traceWorkSpace/{id}?method=PATCH")
    @POST(core.lib.BuildConfig.WORKSPACE_ENDURL+"custom_objects/traceWorkSpace/00008501_00002507_3125c79dced64c46b92079b648c19c8f?method=PATCH")
    fun updateWorkspaceDataFromApi(
       /* @Path("id") id: String?,*/
        @Query("client_id") client_id: String?,
        @Query("site_id") site_id: String?,
        @Body wsInputData: WSInputData, @Header("Authorization") header:String): Single<WSUpdateResult>

    @Headers("Content-Type: application/json")
    //@PUT(core.lib.BuildConfig.WORKSPACE_ENDURL+"custom_objects/traceWorkSpace/{id}?")
    @PUT(core.lib.BuildConfig.WORKSPACE_ENDURL+"custom_objects/traceWorkSpace/00008501_00002507_3125c79dced64c46b92079b648c19c8f?")
    fun createWorkspaceDataFromApi(
        /*@Path("id") id: String?,*/
        @Query("client_id") client_id: String?,
        @Query("site_id") site_id: String?,
        @Body wsInputData: WSInputData, @Header("Authorization") header:String): Single<WSUpdateResult>
}