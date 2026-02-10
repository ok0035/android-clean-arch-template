package com.zerosword.feature_main.ui

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.zerosword.feature_main.contract.MainIntent
import com.zerosword.feature_main.contract.MainSideEffect
import com.zerosword.feature_main.contract.MainState
import com.zerosword.feature_main.viewmodel.MainViewModel
import com.zerosword.resources.ui.theme.DarkColorScheme
import com.zerosword.resources.ui.theme.LightColorScheme
import com.zerosword.resources.ui.theme.Typography

@Composable
fun MainView(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
) {
    val viewModel: MainViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is MainSideEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    TemplateTheme(darkTheme, dynamicColor) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            MainContent(
                state = state,
                onRetry = { viewModel.handleIntent(MainIntent.Retry) }
            )
        }
    }
}

@Composable
fun MainContent(
    state: MainState,
    onRetry: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator()
            }
            state.isError -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = state.message)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRetry) {
                        Text(text = "Retry")
                    }
                }
            }
            else -> {
                Greeting(state.message)
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
    TemplateTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            MainContent(
                state = MainState(isLoading = false, message = "테스트 화면입니다.")
            )
        }
    }
}
