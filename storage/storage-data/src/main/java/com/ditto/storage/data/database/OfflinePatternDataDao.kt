package com.ditto.storage.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ditto.storage.data.model.*

@Dao
abstract class OfflinePatternDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOfflinePatternData(offlinePatterns: OfflinePatterns): Long


    @Query("SELECT * FROM offline_pattern_data WHERE tailornaovaDesignId = :id")
    abstract fun getTailernovaDataByID(id: String): OfflinePatterns
    //updateing where tairlnova id == 1

    @Query("UPDATE offline_pattern_data SET selectedTab = :selectedTab , status = :status , numberOfCompletedPieces = :numberOfCompletedPiece , patternPieces = :patternPieces , garmetWorkspaceItems = :garmetWorkspaceItems , liningWorkspaceItems = :liningWorkspaceItems ,interfaceWorkspaceItems = :interfaceWorkspaceItems WHERE tailornaovaDesignId = :tailornaovaDesignId")
    abstract fun updateOfflinePatternData(
        tailornaovaDesignId: String, selectedTab: String?, status:String?,
        numberOfCompletedPiece: NumberOfCompletedPiecesOffline?,
        patternPieces: List<PatternPiecesOffline>,
        garmetWorkspaceItems: MutableList<WorkspaceItemOffline>,
        liningWorkspaceItems: MutableList<WorkspaceItemOffline>,
        interfaceWorkspaceItems: MutableList<WorkspaceItemOffline> ): Int

    //if patternType!= trial >> delete it (keeping trial patterns only )
    @Query("DELETE from offline_pattern_data where patternType  != :patternType ")
    abstract fun deletePatternsExceptTrial(patternType:String)
}