package com.ducktappedapps.grpclogger

import android.content.Context
import com.ducktappedapps.grpclogger.di.GrpcLoggerComponentHolder
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