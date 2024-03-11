package com.ducktappedapps.grpclogger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ducktappedapps.grpclogger.data.Log
import com.ducktappedapps.grpclogger.data.LogsDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class GrpcLoggingViewModel @Inject constructor(
    private val logsDao: LogsDao,
    private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val _logs: MutableStateFlow<PagingData<Log>> = MutableStateFlow(PagingData.empty())
    val logs: StateFlow<PagingData<Log>>
        get() = _logs

    private val _detailedLogs: MutableStateFlow<List<Log>> = MutableStateFlow(emptyList())
    val detailedLogs: StateFlow<List<Log>>
        get() = _detailedLogs

    private val _sharingTextFlow: MutableSharedFlow<String> = MutableSharedFlow()
    val sharingTextFlow: SharedFlow<String>
        get() = _sharingTextFlow

    private val _logSortedByAscendingOrder: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val logSortedByAscendingOrder: StateFlow<Boolean>
        get() = _logSortedByAscendingOrder

    init {
        viewModelScope.launch {
            Pager(
                config = PagingConfig(
                    pageSize = 20, // Define the number of items per page
                    enablePlaceholders = true,
                    maxSize = 100
                ),
                pagingSourceFactory = { logsDao.getPagedAllRequests() }
            )
                .flow
                .collect {
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun showDetailedLogsFor(callId: String) {
        detailedLogsJob?.cancel()
        detailedLogsJob = viewModelScope.launch {
            logSortedByAscendingOrder
                .flatMapLatest { isAscending ->
                    if (isAscending) logsDao.observeLogsForCallIdAscending(callId)
                    else logsDao.observeLogsForCallIdDescending(callId)
                }.map {
                    _detailedLogs.value = it
                }.collect()
        }
    }

    fun shareText(logsToShare: List<Log>) {
        viewModelScope.launch(defaultDispatcher) {
            _sharingTextFlow.emit(logsToShare.joinToString { it.data })
        }
    }

    fun flipSorting() {
        viewModelScope.launch {
            _logSortedByAscendingOrder.emit(!_logSortedByAscendingOrder.value)
        }
    }
}