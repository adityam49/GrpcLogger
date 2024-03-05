package com.ducktappedapps.grpclogger

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ducktappedapps.grpclogger.data.CallState
import com.ducktappedapps.grpclogger.data.Log
import com.ducktappedapps.grpclogger.data.LogsDao
import com.ducktappedapps.grpclogger.ui.GrpcLoggerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

interface LogManager {

    fun logGrpcRequest(data: String, callId: String)

    fun logGrpcHeaders(data: String, callId: String)

    fun logGrpcResponse(data: String, callId: String)

    fun logGrpcClose(data :String, callId: String)

}

class LogManagerImpl @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val dao: LogsDao,
    private val context: Context,
) : LogManager {
    init {
        createNotificationChannel()
    }

    override fun logGrpcRequest(data: String, callId: String) {
        coroutineScope.launch {
            val log = Log(
                timestamp = System.currentTimeMillis(),
                data = data,
                callState = CallState.REQUEST,
                callId = callId
            )
            dao.insertAll(log)
            showNotification(log.data)
        }
    }

    override fun logGrpcHeaders(data: String, callId: String) {
        coroutineScope.launch {
            val log = Log(
                timestamp = System.currentTimeMillis(),
                data = data,
                callState = CallState.HEADERS,
                callId = callId
            )
            dao.insertAll(log)
            showNotification(log.data)
        }
    }

    override fun logGrpcResponse(data: String, callId: String) {
        coroutineScope.launch {
            val log = Log(
                timestamp = System.currentTimeMillis(),
                data = data,
                callState = CallState.RESPONSE,
                callId = callId
            )
            dao.insertAll(log)
            showNotification(log.data)
        }
    }

    override fun logGrpcClose(data: String,callId: String) {
        coroutineScope.launch {
            val log = Log(
                timestamp = System.currentTimeMillis(),
                data = data,
                callState = CallState.CLOSE,
                callId = callId
            )
            dao.insertAll(log)
            showNotification(log.data)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "grpc_logs_channel"
            val descriptionText = "grpc logs"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("grpc_logs_channel", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    @SuppressLint("MissingPermission")
    private fun showNotification(message: String) {
        val intent = Intent(context, GrpcLoggerActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, "grpc_logs_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Grpc Logs")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(3248423, builder.build())
            }
        }
    }
}