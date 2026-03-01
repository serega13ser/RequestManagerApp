package com.serega.requestmanager.di

import android.content.Context
import androidx.room.Room
import com.serega.requestmanager.data.local.RequestDao
import com.serega.requestmanager.data.local.RequestDatabase
import com.serega.requestmanager.data.repositoryImpl.RequestRepositoryImpl
import com.serega.requestmanager.domain.repository.RequestRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RequestDatabase {
        return Room.databaseBuilder(
            context,
            RequestDatabase::class.java,
            "requests.db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideRequestDao(db: RequestDatabase): RequestDao = db.requestDao()

    @Provides
    fun provideIoDispatcher(): kotlin.coroutines.CoroutineContext {
        return Dispatchers.IO
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryAppModule{

    @Binds
    @Singleton
    abstract fun bindRequestRepository(
        requestRepositoryImpl: RequestRepositoryImpl
    ): RequestRepository
}