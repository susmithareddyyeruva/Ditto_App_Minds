package com.ditto.logger.di

import com.ditto.logger.LoggerFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LoggerModule {

    @Provides
    @Singleton
    fun providesLoggerFactory(): LoggerFactory {
        return LoggerFactory()
    }
}