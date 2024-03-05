package com.ducktappedapps.grpclogger

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall
import io.grpc.ForwardingClientCallListener
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.Status
import java.util.UUID

class GrpcLoggingInterceptor internal constructor(
    private val logsManager: LogManager,
) : ClientInterceptor {

    override fun <ReqT, RespT> interceptCall(
        method: MethodDescriptor<ReqT, RespT>?,
        callOptions: CallOptions?,
        next: Channel
    ): ClientCall<ReqT, RespT> {
        val callId = UUID.randomUUID().toString()
        return object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
            next.newCall(
                method,
                callOptions
            )
        ) {
            override fun start(responseListener: Listener<RespT>?, headers: Metadata?) {
                super.start(LoggingResponseListener(responseListener, callId), headers)
            }

            override fun sendMessage(message: ReqT) {
                logsManager.logGrpcRequest(data = message.toString(), callId = callId)
                super.sendMessage(message)
            }

            override fun cancel(message: String?, cause: Throwable?) {
                super.cancel(message, cause)
            }


        }
    }

    private inner class LoggingResponseListener<RespT>(
        private val delegate: ClientCall.Listener<RespT>?,
        private val callId: String,
    ) :
        ForwardingClientCallListener<RespT>() {

        override fun onHeaders(headers: Metadata?) {
            logsManager.logGrpcHeaders(data = headers.toString(), callId = callId)
            delegate?.onHeaders(headers)
        }

        override fun onMessage(message: RespT) {
            logsManager.logGrpcResponse(data = message.toString(), callId = callId)
            delegate?.onMessage(message)
        }

        override fun onClose(status: Status?, trailers: Metadata?) {
            delegate?.onClose(status, trailers)
            logsManager.logGrpcClose(data = status.toString(), callId = callId)
            super.onClose(status, trailers)
        }

        override fun delegate(): ClientCall.Listener<RespT>? {
            return delegate
        }
    }
}