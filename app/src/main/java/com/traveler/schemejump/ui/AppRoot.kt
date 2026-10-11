package com.traveler.schemejump.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.traveler.schemejump.AppViewModel

enum class AppScreen { Home, Manage }

@Composable
fun AppRoot(
    sharedKeyword: String? = null,
    vm: AppViewModel = viewModel()
) {
    var screen by remember { mutableStateOf(AppScreen.Home) }
    var keyword by remember { mutableStateOf("") }

    // 从其它 App 分享文本进来时，自动回填关键词并回到主页
    LaunchedEffect(sharedKeyword) {
        if (!sharedKeyword.isNullOrBlank()) {
            keyword = sharedKeyword
            screen = AppScreen.Home
        }
    }

    when (screen) {
        AppScreen.Home -> HomeScreen(
            vm = vm,
            keyword = keyword,
            onKeywordChange = { keyword = it },
            onManage = { screen = AppScreen.Manage }
        )

        AppScreen.Manage -> ManageScreen(
            vm = vm,
            onBack = { screen = AppScreen.Home }
        )
    }
}
