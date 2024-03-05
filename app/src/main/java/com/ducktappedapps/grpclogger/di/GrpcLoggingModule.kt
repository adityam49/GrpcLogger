package com.ducktappedapps.grpclogger.di

import android.content.Context
import androidx.room.Room
import com.ducktappedapps.grpclogger.GrpcLoggingInterceptor
import com.ducktappedapps.grpclogger.LogManager
import com.ducktappedapps.grpclogger.LogManagerImpl
import com.ducktappedapps.grpclogger.data.LogsDao
import com.ducktappedapps.grpclogger.data.LogsDb
import dagger.Module
import dagger.Provides
import io.grpc.ClientInterceptor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
object GrpcLoggingModule {

    @Provides
    fun provideCoroutineIODispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    @Provides
    @Singleton
    fun providesLogsDb(context: Context): LogsDb {
        return Room.databaseBuilder(
            context,
            LogsDb::class.java, "logs-db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideLogsDao(db: LogsDb): LogsDao {
        return db.logsDao()
    }

    @Provides
    @Singleton
    fun provideLoggingClientInterceptor(
        logsManager: LogManager,
    ): ClientInterceptor {
        return GrpcLoggingInterceptor(logsManager)
    }

    @Singleton
    @Provides
    fun provideLoggingManager(logsManager: LogManagerImpl): LogManager {
        return logsManager
    }
}