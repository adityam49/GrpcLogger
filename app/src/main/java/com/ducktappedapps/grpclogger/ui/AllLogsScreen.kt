package com.ducktappedapps.grpclogger.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.SwitchColors
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ducktappedapps.grpclogger.data.CallState
import com.ducktappedapps.grpclogger.data.Log
import com.ducktappedapps.grpclogger.ui.theme.GrpcLoggerTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
internal fun AllLogsScreen(
    modifier: Modifier,
    clearLogs: () -> Unit,
    toggleLogging: () -> Unit,
    logsEnabled: Boolean,
    showDetailedLogs: (String) -> Unit,
    allLogs: LazyPagingItems<Log>,
    goBack: () -> Unit,
) {
    BackHandler {
        goBack()
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.surface,
                title = { Text(text = "All Logs") },
                actions = {
                    Switch(checked = logsEnabled, onCheckedChange = { toggleLogging() })
                    IconButton(onClick = clearLogs) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = Icons.Default.Delete.name
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues),
        ) {
            items(allLogs.itemCount) { index ->
                allLogs[index]?.let { log ->
                    Log(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                            .clickable {
                                showDetailedLogs(log.callId)
                            },
                        log = log
                    )
                    if (allLogs.itemCount - 1 != index) {
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
private fun Log(
    modifier: Modifier,
    log: Log
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        TimeTag(
            timeTag = log.getFormattedDateTimestamp(),
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            text = log.data,
            maxLines = 3,
            style = MaterialTheme.typography.body1
        )
    }
}


@Composable
internal fun <T> Flow<T>.CollectAsEffect(
    context: CoroutineContext = EmptyCoroutineContext,
    block: (T) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        onEach(block)
            .flowOn(context)
            .launchIn(this)
    }
}

@Preview(
    showSystemUi = false, showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun Test() {
    val viewModel = FakeGrpcLoggingViewModel()
    val logs = viewModel.logs.collectAsLazyPagingItems()
    GrpcLoggerTheme {
        AllLogsScreen(
            modifier = Modifier.fillMaxSize(),
            allLogs = logs,
            clearLogs = viewModel::clearLogs,
            showDetailedLogs = {
                viewModel.showDetailedLogsFor(it)
            },
            goBack = { },
            logsEnabled = true,
            toggleLogging = {},
        )
    }
}