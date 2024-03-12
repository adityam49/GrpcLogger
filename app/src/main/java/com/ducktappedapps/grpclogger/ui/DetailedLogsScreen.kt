package com.ducktappedapps.grpclogger.ui

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.MoveUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ducktappedapps.grpclogger.data.CallState
import com.ducktappedapps.grpclogger.data.Log
import com.ducktappedapps.grpclogger.ui.theme.ActionButtonBackground
import com.ducktappedapps.grpclogger.ui.theme.GrpcLoggerTheme
import com.ducktappedapps.grpclogger.ui.theme.requestBlue
import com.ducktappedapps.grpclogger.ui.theme.responseGreen
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
internal fun DetailScreen(
    modifier: Modifier,
    logs: LazyPagingItems<Log>,
    shareText: (log: List<Log>) -> Unit,
    isSortingAscending: Boolean,
    flipSorting: () -> Unit,
    goBack: () -> Unit,
    showToast: (String) -> Unit,
) {
    var logOpenedInDetails by remember {
        mutableStateOf<Log?>(null)
    }
    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = false
    )

    BackHandler {
        if (modalSheetState.isVisible) {
            coroutineScope.launch { modalSheetState.hide() }
        } else {
            showToast("Going back from swipe back")
            goBack()
        }
    }
    ModalBottomSheetLayout(
        sheetShape = RoundedCornerShape(
            if (modalSheetState.currentValue == ModalBottomSheetValue.Expanded)
                0.dp
            else
                16.dp
        ),
        sheetContent = {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                logOpenedInDetails?.let {
                    LogBlock(
                        log = it,
                        modifier = Modifier.fillMaxSize(),
                        closeDetailedLogs = {
                            coroutineScope
                                .launch {
                                    modalSheetState.hide()
                                }
                        },
                        shareText = shareText
                    )
                }
            }
        },
        sheetState = modalSheetState,
    ) {
        Scaffold(
            modifier = modifier
                .fillMaxSize(),
            topBar = {
                TopAppBar(
                    backgroundColor = MaterialTheme.colors.surface,
                    title = { Text(text = "Detailed Logs") },
                    navigationIcon = {
                        IconButton(onClick = {
                            showToast("Going back from top back button")
                            goBack()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = Icons.AutoMirrored.Default.ArrowBack.name
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { shareText(logs.itemSnapshotList.items) }) {
                            Icon(
                                modifier = Modifier,
                                imageVector = Icons.Default.Share,
                                contentDescription = Icons.Default.Star.name
                            )
                        }
                        IconButton(onClick = flipSorting) {
                            Icon(
                                modifier = Modifier,
                                imageVector = if (isSortingAscending) Icons.Default.MoveUp else Icons.Default.MoveDown,
                                contentDescription = if (isSortingAscending) Icons.Default.MoveUp.name else Icons.Default.MoveDown.name
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.Top,
            ) {
                items(logs.itemCount) { index ->
                    logs[index]?.let { log ->
                        LogItem(
                            modifier = Modifier.animateItemPlacement(),
                            log = log,
                            shareText = shareText,
                            isLastItem = index == logs.itemCount - 1,
                            viewLogInDetail = {
                                logOpenedInDetails = log
                                coroutineScope.launch {
                                    modalSheetState.show()
                                }
                            }
                        )
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LogBlock(
    modifier: Modifier,
    log: Log,
    closeDetailedLogs: () -> Unit,
    shareText: (log: List<Log>) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        stickyHeader {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = log.callState.toColor(),
                    shape = RoundedCornerShape(16)
                ) {
                    Text(
                        text = log.callState.name.camelCase(),
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onSecondary,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    modifier = Modifier.background(
                        color = MaterialTheme.colors.ActionButtonBackground,
                        shape = CircleShape
                    ), onClick = { shareText(listOf(log)) }
                ) {
                    Icon(Icons.Default.Share, Icons.Default.Share.name, tint = Color.Black)
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    modifier = Modifier
                        .background(color = MaterialTheme.colors.error, shape = CircleShape),
                    onClick = { closeDetailedLogs() },
                ) {
                    Icon(
                        tint = MaterialTheme.colors.onError,
                        imageVector = Icons.Default.Close,
                        contentDescription = Icons.Default.Close.name
                    )
                }
            }
        }
        item {
            SelectionContainer {
                Text(
                    text = log.data,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = log.callState.toColor(),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun LogItem(
    modifier: Modifier,
    log: Log,
    shareText: (log: List<Log>) -> Unit,
    isLastItem: Boolean,
    viewLogInDetail: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .clickable { viewLogInDetail() }
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
            }
        }
    }
}

@Composable
@Preview(
    name = "Detailed screen",
    showSystemUi = false, showBackground = true, device = Devices.PIXEL_7,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
private fun PreviewDetailedScreen() {
    val viewModel = FakeGrpcLoggingViewModel()
    GrpcLoggerTheme {
        val detailedLogs = viewModel.detailedLogs.collectAsLazyPagingItems()
        DetailScreen(
            modifier = Modifier.fillMaxSize(),
            logs = detailedLogs,
            shareText = { logsToShare -> viewModel.shareText(logsToShare) },
            flipSorting = viewModel::flipSorting,
            isSortingAscending = viewModel.logSortedByAscendingOrder.collectAsState().value,
            goBack = { },
            showToast = {},
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