package com.ditto.storage.data.database

import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import androidx.room.*
import io.reactivex.Single
import com.ditto.storage.data.model.OnBoarding
import com.ditto.storage.data.model.Patterns


/**
 * This Data Access Object handles Room database operations for the [OnBoarding] class.
 */
@Dao
abstract class PatternsDao {
    @Query("SELECT * FROM patterns_data order by id desc")
    abstract fun getPatternsData(): List<Patterns>

    @Query("SELECT * FROM patterns_data WHERE id = :patternId")
    abstract fun getPatternDataByID(patternId: Int): Patterns

    /**
     * Sets the [Patterns]. This method guarantees that only one
     * Patterns record is present in the table by first deleting all table
     * data before inserting the Patterns.
     *
     */
    @Transaction
    open fun setPatternsData(patterns: Patterns) {
        deletePatternsData(patterns.id)
        insertPatternsData(patterns)
    }

    @Query("DELETE FROM patterns_data where id = :id")
    abstract fun deletePatternsData(id: Int)

    /**
     * This method should not be used.  Instead, use [setPatternsData],
     * as that method guarantees only a single [Patterns] will reside
     * in the table.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPatternsData(patterns: Patterns): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    abstract fun insertAllPatternsData(patterns: List<Patterns>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertNewPattern(patterns: Patterns): Long

  /*  //@Update(onConflict = OnConflictStrategy.REPLACE)
    @Query("UPDATE patterns_data SET workspaceItems = :workspaceItems WHERE id = :id")
    abstract fun updatePattern(workspaceItems: MutableList<WorkspaceItems>, id: Int ): Single<Int>*/

    @Query("UPDATE patterns_data SET patternName = :patternName WHERE id = :id")
    abstract fun updatePattern(patternName: String, id: Int ): Single<Int>

    @Update
    abstract fun updatePattern(patterns: Patterns): Int


    @Transaction
    open fun upsert(obj: Patterns) {
        val id: Long = insertNewPattern(obj)
        Log.d("patternsDao", "insert $id")
        if (id == -1L) {
            val i = updatePattern(obj)
            Log.d("patternsDao", "update $i")
        }
    }
}
