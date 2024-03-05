package com.ducktappedapps.grpclogger.di

import android.content.Context

object GrpcLoggerComponentHolder {
    @Volatile
    private var grpcLoggingComponent: GrpcLoggingComponent? = null

    fun getGrpcLoggerComponent(context: Context): GrpcLoggingComponent {
        synchronized(this) {
            if (grpcLoggingComponent == null) {
                grpcLoggingComponent = DaggerGrpcLoggingComponent.factory().create(context)
            }
            return grpcLoggingComponent!!
        }
    }
}