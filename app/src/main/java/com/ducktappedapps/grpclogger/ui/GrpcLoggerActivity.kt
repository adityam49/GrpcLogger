package com.ducktappedapps.grpclogger.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ducktappedapps.grpclogger.ui.theme.GrpcLoggerTheme
import com.ducktappedapps.grpclogger.di.GrpcLoggerComponentHolder
import com.ducktappedapps.grpclogger.shareText
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
                    showToast = ::showToast,
                    shareText = ::openShareSheet
                )
            }
        }
    }

    private fun openShareSheet(text: String) {
        shareText(text)
    }

    private fun showToast(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
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
