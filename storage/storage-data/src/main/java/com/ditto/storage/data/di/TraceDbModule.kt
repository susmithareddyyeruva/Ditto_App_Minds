package com.ditto.storage.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import com.ditto.storage.data.database.OnBoardingDao
import com.ditto.storage.data.database.PatternsDao
import com.ditto.storage.data.database.TraceDataDatabase
import com.ditto.storage.data.database.UserDao
import javax.inject.Singleton

/**
 * Dagger module to provide injections for DB/DAO classes
 */
@Module
class TraceDbModule {

    @Provides
    @Singleton
    fun provideOnBoardingDataDao(db: TraceDataDatabase): OnBoardingDao {
        return db.onBoardingDataDao()
    }

    @Provides
    @Singleton
    fun provideUserDataDao(db: TraceDataDatabase): UserDao {
        return db.userDataDao()
    }

    @Provides
    @Singleton
    fun providePatternsDataDao(db: TraceDataDatabase): PatternsDao {
        return db.patternDataDao()
    }

    @Provides
    @Singleton
    fun provideTraceDataDatabase(context: Context): TraceDataDatabase {
        return TraceDataDatabase.getInstance(context)
    }
}