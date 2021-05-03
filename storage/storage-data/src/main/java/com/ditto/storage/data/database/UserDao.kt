package com.ditto.storage.data.database

import androidx.room.*
import com.ditto.storage.data.model.User
import io.reactivex.Single


/**
 * This Data Access Object handles Room database operations for the [User] class.
 */
@Dao
abstract class UserDao {
    @Query("SELECT * FROM user_data")
    abstract fun getUserData(): User

    @Transaction
    open fun updateDndOnboarding(
        id: Int,
        dndOnboarding: Boolean,
        isLaterClicked: Boolean,
        isWifiLaterClicked: Boolean
    ): User {
        updateDndOnboardingUser(id, dndOnboarding, isLaterClicked, isWifiLaterClicked)
        return getUserData()
    }

    @Query("UPDATE user_data SET dndOnboarding=:dndOnboarding , bleDialogVisible=:isLaterClicked,wifiDialogVisible=:isWifiLaterClicked WHERE id = :id")
    abstract fun updateDndOnboardingUser(
        id: Int,
        dndOnboarding: Boolean,
        isLaterClicked: Boolean,
        isWifiLaterClicked: Boolean
    )


    /**
     * Sets the [User]. This method guarantees that only one
     * user record is present in the table by first deleting all table
     * data before inserting the user data.
     *
     */
    @Transaction
    open fun setUserData(user: User): Long {
        deleteUserData(user.id)
        return insertUserData(user)
    }

    @Transaction
    open fun deleteUserTable(user: User): Boolean {
        deleteUserData(user.id)
        return true
    }

    @Query("DELETE FROM user_data WHERE id= :id")
    abstract fun deleteUserData(id: Int)

    /**
     * This method should not be used.  Instead, use [setUserData],
     * as that method guarantees only a single [User] will reside
     * in the table.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUserData(user: User): Long


    fun deleteAndInsert(user: User): Single<Long> {
        return Single.create {
            it.onSuccess(setUserData(user))
        }
    }

    fun deleteUserData(user: User): Single<Boolean> {
        return Single.create {
            it.onSuccess(deleteUserTable(user))
        }
    }
}
