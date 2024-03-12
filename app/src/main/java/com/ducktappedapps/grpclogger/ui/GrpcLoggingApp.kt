package com.ducktappedapps.grpclogger.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems


@Composable
internal fun GrpcLoggingApp(
    viewModel: GrpcLoggingViewModel,
    showToast : (String) -> Unit,
    shareText :(String) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.surface
    ) {
        viewModel.sharingTextFlow.CollectAsEffect {
            shareText(it)
        }
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "All") {
            composable("All") {
                val logs = viewModel.logs.collectAsLazyPagingItems()
                AllLogsScreen(
                    modifier = Modifier.fillMaxSize(),
                    allLogs = logs,
                    clearLogs = viewModel::clearLogs,
                    showDetailedLogs = {
                        viewModel.showDetailedLogsFor(it)
                        navController.navigate("Detailed")
                    },
                    goBack = { navController.navigateUp() }
                )
            }
            composable("Detailed") {
                val detailedLogs = viewModel.detailedLogs.collectAsLazyPagingItems()
                DetailScreen(
                    modifier = Modifier.fillMaxSize(),
                    logs = detailedLogs,
                    shareText = { logsToShare -> viewModel.shareText(logsToShare) },
                    flipSorting = viewModel::flipSorting,
                    isSortingAscending = viewModel.logSortedByAscendingOrder.collectAsState().value,
                    goBack = { navController.navigateUp() },
                    showToast = showToast
                )
            }
        }
    }
}