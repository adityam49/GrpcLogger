package com.ducktappedapps.grpclogger.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ducktappedapps.grpclogger.ui.theme.GrpcLoggerTheme
import com.ducktappedapps.grpclogger.ui.theme.TimeTagColor

@Composable
internal fun TimeTag(timeTag: String, colorBackground: Color= MaterialTheme.colors.TimeTagColor) {
    Surface(
        color = colorBackground,
        shape = RoundedCornerShape(50),
        contentColor = MaterialTheme.colors.onPrimary,
        modifier = Modifier
            .wrapContentSize()
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = timeTag,
            style = MaterialTheme.typography.overline,
            fontSize = 8.sp,
        )
    }
}

@Composable
@Preview(
    name = "TimeTag", showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.NONE,
    device = "spec:id=reference_phone,shape=Normal,width=411,height=891,unit=dp,dpi=420"
)
internal fun TimeTagTest() {
    GrpcLoggerTheme {
        val timeTag = "11:32:12 AM"
        Box(modifier = Modifier.fillMaxSize()) {
            TimeTag(timeTag = timeTag)
        }
    }

}