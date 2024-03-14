package com.ducktappedapps.grpclogger

import android.content.Context
import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor

class GrpcLogger(
    private val context: Context,
) {

    private val noOpClientInterceptor : NoOpClientInterceptor by lazy {
        NoOpClientInterceptor()
    }
    fun getInterceptor(): ClientInterceptor {
        return noOpClientInterceptor
    }
}

class NoOpClientInterceptor internal constructor() : ClientInterceptor {
    override fun <ReqT : Any, RespT : Any> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel
    ): ClientCall<ReqT, RespT> {
        return object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
            next.newCall(
                method,
                callOptions
            )
        ) {
            override fun start(responseListener: Listener<RespT>?, headers: Metadata?) {
                super.start(responseListener, headers)
            }

            override fun sendMessage(message: ReqT) {
                super.sendMessage(message)
            }
        }
    }
}