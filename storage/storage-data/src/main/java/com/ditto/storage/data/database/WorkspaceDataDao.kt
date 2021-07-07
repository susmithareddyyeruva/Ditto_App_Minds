package com.ditto.storage.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.ditto.storage.data.model.WorkspaceData

@Dao
abstract class WorkspaceDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertWorkspcaeData(workspaceData: WorkspaceData): Long
}