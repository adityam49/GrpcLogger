package com.ducktappedapps.grpclogger.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity
class Log(
    @PrimaryKey(autoGenerate = true) var uid: Int? = null,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "data") val data: String,
    @ColumnInfo(name = "callId") val callId: String,
    @ColumnInfo(name = "state") val callState: CallState
) {
    fun getFormattedDateTimestamp(): String = dateTimeFormatter.format(Date(timestamp))

    fun getFormattedTimestamp(): String = timeFormatter.format(Date(timestamp))

    private companion object {
        val dateTimeFormatter = SimpleDateFormat("hh:mm:ss dd-mm-yyyy", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    }
}

enum class CallState {
    HEADERS, REQUEST, RESPONSE, CLOSE
}

class CallStateConverters {

    @TypeConverter
    fun toCallState(value: Int) = enumValues<CallState>()[value]

    @TypeConverter
    fun fromHealth(value: CallState) = value.ordinal
}