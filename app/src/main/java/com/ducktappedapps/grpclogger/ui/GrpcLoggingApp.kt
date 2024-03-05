package com.ducktappedapps.grpclogger.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.ducktappedapps.grpclogger.shareText


@Composable
internal fun GrpcLoggingApp(viewModel: GrpcLoggingViewModel) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.surface
    ) {
        val context = LocalContext.current
        viewModel.sharingTextFlow.CollectAsEffect {
            context.shareText(it)
        }

        val logs = viewModel.logs.collectAsState().value
        val detailedLogs = viewModel.detailedLogs.collectAsState().value

        if (detailedLogs.isEmpty()) {
            AllLogsScreen(
                modifier = Modifier.fillMaxSize(),
                allLogs = logs,
                clearLogs = viewModel::clearLogs,
                showDetailedLogs = viewModel::showDetailedLogsFor,
            )
        } else {
            DetailScreen(
                modifier = Modifier.fillMaxSize(),
                onClickBack = { viewModel.showDetailedLogsFor("") },
                logs = detailedLogs,
                shareText = { logsToShare -> viewModel.shareText(logsToShare) },
            )
        }
    }
}