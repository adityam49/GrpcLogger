package com.ducktappedapps.grpclogger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ducktappedapps.grpclogger.data.Log
import com.ducktappedapps.grpclogger.data.LogsDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class GrpcLoggingViewModel @Inject constructor(
    private val logsDao: LogsDao,
    private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val _logs: MutableStateFlow<List<Log>> = MutableStateFlow(emptyList())
    val logs: StateFlow<List<Log>>
        get() = _logs

    private val _detailedLogs: MutableStateFlow<List<Log>> = MutableStateFlow(emptyList())
    val detailedLogs: StateFlow<List<Log>>
        get() = _detailedLogs

    private val _sharingTextFlow: MutableSharedFlow<String> = MutableSharedFlow()
    val sharingTextFlow: SharedFlow<String>
        get() = _sharingTextFlow

    init {
        viewModelScope.launch {
            logsDao.getAllRequests().collect {
                _logs.value = it
            }
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            logsDao.deleteAll()
        }
    }

    private var detailedLogsJob: Job? = null
    fun showDetailedLogsFor(callId: String) {
        detailedLogsJob?.cancel()
        detailedLogsJob = viewModelScope.launch {
            logsDao.observeLogsForCallId(callId).onEach {
                _detailedLogs.value = it
            }.collect()
        }
    }

    fun shareText(logsToShare: List<Log>) {
        viewModelScope.launch(defaultDispatcher) {
            _sharingTextFlow.emit(logsToShare.joinToString { it.data })
        }
    }
}