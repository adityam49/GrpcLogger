package com.ducktappedapps.grpclogger.di

import android.content.Context
import io.grpc.ClientInterceptor

class GrpcLogger(
    private val context: Context,
) {

    @Volatile
    private var grpcLoggingInterceptor: ClientInterceptor? = null

    fun getInterceptor(): ClientInterceptor {
        if (grpcLoggingInterceptor == null) {
            synchronized(this) {
                if (grpcLoggingInterceptor == null) {
                    val component = GrpcLoggerComponentHolder.getGrpcLoggerComponent(context)
                    grpcLoggingInterceptor = component.getGrpcLoggingInterceptor()
                }
            }
        }
        return grpcLoggingInterceptor!!
    }
}