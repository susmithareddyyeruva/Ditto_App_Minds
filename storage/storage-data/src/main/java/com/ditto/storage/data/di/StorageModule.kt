package com.ditto.storage.data.di

import dagger.Binds
import dagger.Module
import com.ditto.storage.data.StorageManagerImpl
import com.ditto.storage.domain.StorageManager

@Module
interface StorageModule {
    @Binds
    fun bindStorageManager(storageManagerImpl: StorageManagerImpl): StorageManager
}