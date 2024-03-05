package com.ducktappedapps.grpclogger.di

import android.content.Context
import com.ducktappedapps.grpclogger.ui.GrpcLoggerActivity
import dagger.BindsInstance
import dagger.Component
import io.grpc.ClientInterceptor
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        GrpcLoggingModule::class,
        ViewModelModule::class,
    ]
)
interface GrpcLoggingComponent {
    fun getGrpcLoggingInterceptor(): ClientInterceptor

    fun inject(activity: GrpcLoggerActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): GrpcLoggingComponent
    }
}