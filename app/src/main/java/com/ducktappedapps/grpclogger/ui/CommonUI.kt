package com.ducktappedapps.grpclogger.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ducktappedapps.grpclogger.ui.theme.GrpcLoggerTheme
import com.ducktappedapps.grpclogger.ui.theme.TimeTagColor
import kotlinx.coroutines.delay

@Composable
internal fun TimeTag(timeTag: String, colorBackground: Color = MaterialTheme.colors.TimeTagColor) {
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
    name = "TimeTag",
    showBackground = false,
    showSystemUi = false
)
internal fun TimeTagTest() {
    GrpcLoggerTheme {
        val timeTag = "11:32:12 AM"
        Box(modifier = Modifier.fillMaxSize()) {
            TimeTag(timeTag = timeTag)
        }
    }
}

private fun splitInput(input: String): Triple<String?, Number?, String?> {
    val regex = """(\D*)(\d+(?:\.\d+)?)(\D*)""".toRegex()
    val matchResult = regex.find(input)
    if (matchResult != null) {
        val (prefix, numberStr, suffix) = matchResult.destructured
        val number: Number =
            if (numberStr.contains(".")) numberStr.toDouble() else numberStr.toInt()
        return Triple(prefix.ifBlank { null }, number, suffix.ifBlank { null })
    }
    return Triple(null, null, null)
}

@Composable
fun TickerText(
    text: String?,
    fontSize: TextUnit = 10.sp,
    fontColor: Color,
    style: TextStyle,
) {
    val inputs = splitInput(text ?: "")
    val count by remember(inputs.second) { mutableStateOf(inputs.second) }
    Row(
        modifier = Modifier.wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!inputs.first.isNullOrBlank()) {
            Text(
                style = style,
                text = inputs.first ?: "",
                fontSize = fontSize,
                color = fontColor,
                textAlign = TextAlign.Center,
            )
        }


        if (count != null) {
            AnimatedContent(
                targetState = count,
                transitionSpec = {
                    slideInVertically { it } togetherWith slideOutVertically { -it }
                }, label = ""
            ) { count ->
                Text(
                    text = count?.toString() ?: "",
                    fontSize = fontSize,
                    color = fontColor,
                    textAlign = TextAlign.Center,
                )
            }
        }
        if (!inputs.third.isNullOrBlank()) {
            Text(
                text = inputs.third ?: "",
                fontSize = fontSize,
                color = fontColor,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
@Preview(showBackground = false)
fun TestTickerText() {
    var count by remember {
        mutableIntStateOf(0)
    }
    LaunchedEffect(key1 = Unit) {
        repeat(500) {
            delay(1000)
            count += 1
        }
    }
    GrpcLoggerTheme {
        Surface {
            TickerText(
                text = "$count Requests",
                fontColor = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.body1
            )
        }
    }
}