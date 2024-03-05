package com.ducktappedapps.grpclogger.ui

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ducktappedapps.grpclogger.data.CallState
import com.ducktappedapps.grpclogger.data.Log
import com.ducktappedapps.grpclogger.ui.theme.GrpcLoggerTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
internal fun AllLogsScreen(
    modifier: Modifier,
    clearLogs: () -> Unit,
    showDetailedLogs: (String) -> Unit,
    allLogs: List<Log>,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.surface,
                title = { Text(text = "All Logs") },
                actions = {
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
            itemsIndexed(allLogs) { index, item ->
                Log(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable {
                            showDetailedLogs(item.callId)
                        }, log = item
                )
                if (index != allLogs.lastIndex) {
                    Divider(color = MaterialTheme.colors.onSurface, thickness = 1.dp)
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
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            text = log.getFormattedDateTimestamp(),
            style = MaterialTheme.typography.caption
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
    showSystemUi = true, showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun Test() {
    GrpcLoggerTheme {
        AllLogsScreen(
            modifier = Modifier.fillMaxSize(),
            clearLogs = { },
            showDetailedLogs = { },
            allLogs = buildList {
                repeat(10) {
                    add(
                        Log(
                            timestamp = System.currentTimeMillis(),
                            callState = CallState.REQUEST,
                            callId = "jlfkds$it",
                            data = "Some data 123123123"
                        )
                    )
                }
            })
    }
}