package com.ducktappedapps.grpclogger.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ducktappedapps.grpclogger.di.GrpcLoggerComponentHolder
import com.ducktappedapps.grpclogger.shareText
import com.ducktappedapps.grpclogger.ui.theme.GrpcLoggerTheme
import javax.inject.Inject

class GrpcLoggerActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GrpcLoggerComponentHolder.getGrpcLoggerComponent(application).inject(this)
        checkNotificationPermission()
        setContent {
            GrpcLoggerTheme {
                val viewModel by viewModels<GrpcLoggingViewModelImpl>(
                    factoryProducer = { viewModelFactory }
                )
                GrpcLoggingApp(
                    viewModel = viewModel,
                    shareText = ::openShareSheet
                )
            }
        }
    }

    private fun openShareSheet(text: String) {
        shareText(text)
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED -> {
                    requestNotificationPermission.launch(permission)
                }
            }
        }
    }
}
