package com.ducktappedapps.grpclogger.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ducktappedapps.grpclogger.data.CallState
import com.ducktappedapps.grpclogger.data.Log
import com.ducktappedapps.grpclogger.ui.theme.GrpcLoggerTheme
import com.ducktappedapps.grpclogger.ui.theme.requestBlue
import com.ducktappedapps.grpclogger.ui.theme.responseGreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun DetailScreen(
    modifier: Modifier,
    onClickBack: () -> Unit,
    logs: List<Log>,
    shareText: (log: List<Log>) -> Unit,
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.surface,
                title = { Text(text = "Detailed Logs") },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = Icons.AutoMirrored.Default.ArrowBack.name
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { shareText(logs) }) {
                        Icon(
                            modifier = Modifier,
                            imageVector = Icons.Default.Share,
                            contentDescription = Icons.Default.Star.name
                        )
                    }
                }
            )
        }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            itemsIndexed(logs) { index, item ->
                CollapsableLog(
                    modifier = Modifier.animateItemPlacement(),
                    log = item,
                    shareText = shareText,
                    isLastItem = index == logs.lastIndex
                )
            }
        }
    }
}


@Composable
fun Logblock(log: Log, modifier: Modifier) {
    Box(
        modifier = modifier.border(
            width = 1.dp,
            color = log.callState.toColor(),
            shape = RoundedCornerShape(4.dp)
        )
    ) {
        SelectionContainer {
            Text(
                text = log.data,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun CollapsableLog(
    modifier: Modifier,
    log: Log,
    shareText: (log: List<Log>) -> Unit,
    isLastItem  : Boolean,
) {
    var isCollapsed by remember {
        mutableStateOf(false)
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .clickable { isCollapsed = !isCollapsed }
            .padding(horizontal = 16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier
                    .size(32.dp),
                shape = CircleShape,
                color = log.callState.toColor(),
            ) {
                Icon(
                    tint = MaterialTheme.colors.onSecondary,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp),
                    imageVector = when (log.callState) {
                        CallState.HEADERS -> Icons.AutoMirrored.Default.List
                        CallState.REQUEST -> Icons.Default.ArrowUpward
                        CallState.RESPONSE -> Icons.Default.ArrowDownward
                        CallState.CLOSE -> Icons.Default.DoneAll
                    },
                    contentDescription = Icons.AutoMirrored.Default.ArrowBack.name
                )
            }
            if (!isLastItem) {
                TabRowDefaults.Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp),
                    color = MaterialTheme.colors.onSurface,
                )
            }
        }
        Column(
            modifier = Modifier
                .wrapContentWidth()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row {
                Column {
                    Text(
                        text = log.getFormattedTimestamp(),
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier
                    )
                    Text(
                        text = log.callState.name.camelCase(),
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { shareText(listOf(log)) }) {
                    Icon(Icons.Default.Share, Icons.Default.Share.name)
                }
            }

            if (!isCollapsed) {
                Logblock(
                    log = log,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true, device = Devices.PIXEL_7)
private fun PreviewDetailedScreen() {
    GrpcLoggerTheme {
        DetailScreen(
            modifier = Modifier.fillMaxSize(),
            onClickBack = { },
            logs = listOf(
                Log(
                    uid = 21,
                    timestamp = System.currentTimeMillis(),
                    data = "jlfkds",
                    callId = "324",
                    callState = CallState.REQUEST
                ),
                Log(
                    uid = 21,
                    timestamp = System.currentTimeMillis(),
                    data = "jlfkds",
                    callId = "324",
                    callState = CallState.HEADERS
                ),
                Log(
                    uid = 21,
                    timestamp = System.currentTimeMillis(),
                    data = "jlfkds",
                    callId = "324",
                    callState = CallState.RESPONSE
                ),
                Log(
                    uid = 21,
                    timestamp = System.currentTimeMillis(),
                    data = "jlfkds",
                    callId = "324",
                    callState = CallState.CLOSE
                )
            ),
            shareText = {},
        )
    }
}

@Composable
private fun CallState.toColor(): Color {
    return when (this) {
        CallState.HEADERS -> MaterialTheme.colors.secondary
        CallState.REQUEST -> MaterialTheme.colors.requestBlue
        CallState.RESPONSE -> MaterialTheme.colors.responseGreen
        CallState.CLOSE -> MaterialTheme.colors.error
    }
}

private fun String.camelCase(): String {
    return first().uppercase() +
            if (length > 1)
                substring(1, length).lowercase()
            else
                ""
}