package com.ditto.storage.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ditto.storage.data.model.NumberOfCompletedPiecesOffline
import com.ditto.storage.data.model.OfflinePatternData
import com.ditto.storage.data.model.PatternPiecesOffline
import com.ditto.storage.data.model.WorkspaceItemOffline

@Dao
abstract class OfflinePatternDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOfflinePatternData(offlinePatternData: OfflinePatternData): Long

    //updateing where tairlnova id == 1

    @Query("UPDATE offline_pattern_data SET selectedTab = :selectedTab , status = :status , numberOfCompletedPieces = :numberOfCompletedPiece , patternPieces = :patternPieces , garmetWorkspaceItems = :garmetWorkspaceItems , liningWorkspaceItems = :liningWorkspaceItems ,interfaceWorkspaceItems = :interfaceWorkspaceItem WHERE tailornaovaDesignId = :tailornaovaDesignId")
    abstract fun updateOfflinePatternData(tailornaovaDesignId: Int, selectedTab: String, status:String,numberOfCompletedPiece: NumberOfCompletedPiecesOffline,
                                          patternPieces: List<PatternPiecesOffline>,
                                          garmetWorkspaceItems: List<WorkspaceItemOffline>,
                                          liningWorkspaceItems: List<WorkspaceItemOffline>,
                                          interfaceWorkspaceItem: List<WorkspaceItemOffline> )

}