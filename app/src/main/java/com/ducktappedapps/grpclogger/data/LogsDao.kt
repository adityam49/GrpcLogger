package com.ducktappedapps.grpclogger.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LogsDao {
    @Query("SELECT * FROM Log WHERE state IS :state ORDER BY timestamp DESC")
    fun getAllRequests(state: CallState = CallState.REQUEST): Flow<List<Log>>

    @Query("SELECT * FROM Log WHERE callId IS :callId ORDER BY timestamp DESC")
    suspend fun getLogsForCallId(callId: String): List<Log>

    @Insert
    suspend fun insertAll(vararg logs: Log)

    @Query("DELETE FROM Log")
    suspend fun deleteAll()

    @Query("SELECT * FROM Log WHERE  callId IS :callId ORDER BY timestamp ASC")
     fun observeLogsForCallIdAscending(callId: String): PagingSource<Int,Log>


    @Query("SELECT * FROM Log WHERE  callId IS :callId ORDER BY timestamp DESC")
     fun observeLogsForCallIdDescending(callId: String): PagingSource<Int,Log>


    @Query("SELECT * FROM Log WHERE state IS :state ORDER BY timestamp DESC")
    fun getPagedAllRequests(state: CallState = CallState.REQUEST): PagingSource<Int,Log>
}