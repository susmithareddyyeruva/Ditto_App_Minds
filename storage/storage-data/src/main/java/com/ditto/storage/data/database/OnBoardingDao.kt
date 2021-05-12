package com.ditto.storage.data.database

import androidx.room.*
import com.ditto.storage.data.model.OnBoarding


/**
 * This Data Access Object handles Room database operations for the [OnBoarding] class.
 */
@Dao
abstract class OnBoardingDao {
    @Query("SELECT * FROM onboard_data")
    abstract fun getOnboardingData(): List<OnBoarding>

    @Query("SELECT * FROM onboard_data WHERE id = :instructionID")
    abstract fun getOnboardingDataByID(instructionID: Int): OnBoarding

    /**
     * Sets the [OnBoarding]. This method guarantees that only one
     * OnBoarding record is present in the table by first deleting all table
     * data before inserting the OnBoarding.
     *
     */
    @Transaction
    open fun setOnboardingData(onBoarding: OnBoarding) {
        deleteOnboardingData()
        insertOnboardingData(onBoarding)
    }

    @Query("DELETE FROM onboard_data")
    abstract fun deleteOnboardingData()

    /**
     * This method should not be used.  Instead, use [setOnboardingData],
     * as that method guarantees only a single [OnBoarding] will reside
     * in the table.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOnboardingData(onBoarding: OnBoarding)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    abstract fun insertAllOnboardingData(onBoarding: List<OnBoarding>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    abstract fun insertAllOnboardingDataDup(onBoarding: List<OnBoarding>)
}
