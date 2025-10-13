package com.zerosword.feature_main.ui

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.zerosword.feature_main.viewmodel.MainViewModel
import com.zerosword.resources.ui.theme.DarkColorScheme
import com.zerosword.resources.ui.theme.LightColorScheme
import com.zerosword.resources.ui.theme.Typography

@Composable
fun MainView(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
) {
    val viewModel: MainViewModel = hiltViewModel()
    val message by viewModel.mainMessage.collectAsState()

    TemplateTheme(
        darkTheme, dynamicColor
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Greeting(message)
            }
        }

    }

}

@Composable
fun TemplateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


@Composable
fun Greeting(message: String, modifier: Modifier = Modifier) {
    Text(
        text = "$message!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    TemplateTheme() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Greeting("테스트 화면입니다.")
            }
        }
    }
}