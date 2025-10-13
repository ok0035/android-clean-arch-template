package com.zerosword.template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.zerosword.feature_main.ui.Greeting
import com.zerosword.feature_main.ui.MainView
import com.zerosword.feature_main.ui.TemplateTheme
import com.zerosword.feature_main.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // ✅ 시스템 바는 Activity에서 1회 설정
        // 투명 처리 + 아이콘 밝기 자동 전환
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                /* lightScrim = */ android.graphics.Color.TRANSPARENT,
                /* darkScrim  = */ android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                /* lightScrim = */ android.graphics.Color.TRANSPARENT,
                /* darkScrim  = */ android.graphics.Color.TRANSPARENT
            )
        )

        super.onCreate(savedInstanceState)
        setContent {
            MainView()
        }
    }

}