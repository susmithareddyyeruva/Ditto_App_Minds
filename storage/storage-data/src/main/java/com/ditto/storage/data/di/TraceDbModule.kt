package com.ditto.storage.data.di

import android.content.Context
import com.ditto.storage.data.database.*
import dagger.Module
import dagger.Provides
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
    fun provideWorkspaceDataDao(db: TraceDataDatabase): OfflinePatternDataDao {
        return db.offlinePatternDataDao()
    }

    @Provides
    @Singleton
    fun provideTraceDataDatabase(context: Context): TraceDataDatabase {
        return TraceDataDatabase.getInstance(context)
    }
}