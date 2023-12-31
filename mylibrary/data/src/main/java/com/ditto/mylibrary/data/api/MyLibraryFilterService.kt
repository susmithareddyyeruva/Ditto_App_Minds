package com.ditto.mylibrary.data.api

import com.ditto.mylibrary.model.ThirdPartyResponse
import com.ditto.mylibrary.domain.request.*
import com.ditto.mylibrary.model.FavouriteResult
import com.ditto.mylibrary.model.FoldersResult
import com.ditto.mylibrary.model.MyLibraryResult
import core.lib.BuildConfig
import io.reactivex.Single
import retrofit2.http.*

interface MyLibraryFilterService {
    @Headers("Content-Type: application/json")
    @POST(BuildConfig.MYLIBRARY_ENDURL + "TraceAppMyLibrary-Shows")
    fun getAllPatternsPatterns(
        @Body body: MyLibraryFilterRequestData?,
        @Header("Authorization") header: String?
    ): Single<MyLibraryResult>

    @Headers("Content-Type: application/json")
    @POST(BuildConfig.MYLIBRARY_ENDURL + "TraceAppLibraryFolder-Modify")
    fun getFoldersList(
        @Body body: GetFolderRequest?,
        @Header("Authorization") header: String,
        @Query("method") method: String
    ): Single<FoldersResult>

    @Headers("Content-Type: application/json")
    @POST(BuildConfig.MYLIBRARY_ENDURL + "TraceAppLibraryFolder-Modify")
    fun addFolder(
        @Body body: FolderRequest?,
        @Header("Authorization") header: String,
        @Query("method") method: String
    ): Single<FavouriteResult>

    @Headers("Content-Type: application/json")
    @POST(BuildConfig.MYLIBRARY_ENDURL + "TraceAppLibraryFolder-Modify")
    fun renameFolder(
        @Body body: FolderRenameRequest?,
        @Header("Authorization") header: String,
        @Query("method") method: String
    ): Single<FavouriteResult>

    @Headers("Content-Type: application/json")
    @POST(BuildConfig.MYLIBRARY_ENDURL + "TraceAppThirdParty-Shows")
    fun getThirdPartyPatternData(
        @Body body: ThirdPartyDataRequest?,
        @Header("Authorization") header: String?
    ): Single<ThirdPartyResponse>
}