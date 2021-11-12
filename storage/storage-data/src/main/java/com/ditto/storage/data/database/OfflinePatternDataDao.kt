package com.ditto.storage.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ditto.storage.data.model.NumberOfCompletedPiecesOffline
import com.ditto.storage.data.model.OfflinePatterns
import com.ditto.storage.data.model.PatternPiecesOffline
import com.ditto.storage.data.model.WorkspaceItemOffline

@Dao
abstract class OfflinePatternDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOfflinePatternData(offlinePatterns: OfflinePatterns): Long


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    abstract fun insertOfflinePatternDataList(offlinePatterns: List<OfflinePatterns>)

    @Query("SELECT * FROM offline_pattern_data")//todo need to fetch respective of id i.e custmoer id
    abstract fun getTailernovaData(): List<OfflinePatterns>

    @Query("SELECT * FROM offline_pattern_data WHERE designId = :id")
    abstract fun getTailernovaDataByID(id: String): OfflinePatterns


    @Query("SELECT * FROM offline_pattern_data WHERE patternType = :type")
    abstract fun getListOfTrialPattern(type: String): List<OfflinePatterns>

    @Query("UPDATE offline_pattern_data SET selectedTab = :selectedTab , status = :status , numberOfCompletedPiece = :numberOfCompletedPiece , patternPieces = :patternPieces , garmetWorkspaceItems = :garmetWorkspaceItems , liningWorkspaceItems = :liningWorkspaceItems ,interfaceWorkspaceItems = :interfaceWorkspaceItems WHERE tailornaovaDesignId = :tailornaovaDesignId")
    abstract fun updateOfflinePatternData(
        tailornaovaDesignId: String?,
        selectedTab: String?,
        status: String?,
        numberOfCompletedPiece: NumberOfCompletedPiecesOffline?,
        patternPieces: List<PatternPiecesOffline>?,
        garmetWorkspaceItems: MutableList<WorkspaceItemOffline>,
        liningWorkspaceItems: MutableList<WorkspaceItemOffline>,
        interfaceWorkspaceItems: MutableList<WorkspaceItemOffline>
    ): Int

    //if patternType!= trial >> delete it (keeping trial patterns only)
    @Query("DELETE from offline_pattern_data where patternType  != :patternType and custId == :custId")// for specific user
    abstract fun deletePatternsExceptTrial(patternType: String, custId: String?)

}