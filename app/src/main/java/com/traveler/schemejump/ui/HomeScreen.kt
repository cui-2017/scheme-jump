package com.traveler.schemejump.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.traveler.schemejump.AppViewModel
import com.traveler.schemejump.engine.SchemeLauncher
import com.traveler.schemejump.model.Platform
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: AppViewModel,
    keyword: String,
    onKeywordChange: (String) -> Unit,
    onManage: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var query by remember { mutableStateOf("") }

    // 平台列表或包名变化时，重新探测安装状态
    val installedMap = remember(vm.platforms) {
        vm.platforms.associate { it.id to SchemeLauncher.isInstalled(context, it.packageName) }
    }

    val visible = remember(vm.platforms, query) {
        vm.platforms.filter {
            it.enabled && (query.isBlank() || it.name.contains(query, ignoreCase = true))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scheme 跳转") },
                actions = {
                    IconButton(onClick = onManage) {
                        Icon(Icons.Filled.Settings, contentDescription = "管理平台")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = keyword,
                onValueChange = onKeywordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("关键词") },
                singleLine = true,
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = {
                            val t = clipboard.getText()?.text
                            if (!t.isNullOrBlank()) onKeywordChange(t)
                        }) {
                            Text("粘贴")
                        }
                        if (keyword.isNotEmpty()) {
                            IconButton(onClick = { onKeywordChange("") }) {
                                Icon(Icons.Filled.Close, contentDescription = "清空")
                            }
                        }
                    }
                }
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("筛选平台") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) }
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "共 ${visible.size} 个平台 · 点击目标按钮直接跳转",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(visible, key = { it.id }) { platform ->
                    PlatformCard(
                        platform = platform,
                        installed = installedMap[platform.id] == true,
                        onJump = { template ->
                            if (SchemeLauncher.needsKeyword(template) && keyword.isBlank()) {
                                scope.launch { snackbarHostState.showSnackbar("请先输入关键词，或点「粘贴」取剪贴板") }
                            } else {
                                val clip = clipboard.getText()?.text.orEmpty()
                                val url = SchemeLauncher.resolve(template, keyword, clip)
                                SchemeLauncher.jump(context, url, platform.packageName)
                            }
                        },
                        onInstall = {
                            SchemeLauncher.openStore(context, platform.packageName, platform.name)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlatformCard(
    platform: Platform,
    installed: Boolean,
    onJump: (String) -> Unit,
    onInstall: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (installed) Color(0xFF1D9E75)
                            else MaterialTheme.colorScheme.outline
                        )
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = platform.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = if (installed) "已安装" else "未安装",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                platform.targets.forEach { target ->
                    OutlinedButton(onClick = { onJump(target.template) }) {
                        Text(target.label)
                    }
                }
                if (!installed && platform.packageName.isNotBlank()) {
                    TextButton(onClick = onInstall) { Text("去安装") }
                }
            }
        }
    }
}
