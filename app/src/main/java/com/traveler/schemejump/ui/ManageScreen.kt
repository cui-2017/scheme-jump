package com.traveler.schemejump.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.traveler.schemejump.AppViewModel
import com.traveler.schemejump.model.Platform

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageScreen(
    vm: AppViewModel,
    onBack: () -> Unit
) {
    var editing by remember { mutableStateOf<Platform?>(null) }
    var creating by remember { mutableStateOf(false) }
    var confirmReset by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("管理平台") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { creating = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "新建平台")
                    }
                    IconButton(onClick = { confirmReset = true }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "恢复内置平台")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(vm.platforms, key = { it.id }) { platform ->
                ManageRow(
                    platform = platform,
                    onEdit = { editing = platform },
                    onToggle = { vm.setEnabled(platform.id, it) },
                    onDelete = { vm.remove(platform.id) }
                )
            }
        }
    }

    if (creating) {
        EditDialog(
            initial = null,
            onDismiss = { creating = false },
            onConfirm = {
                vm.upsert(it)
                creating = false
            }
        )
    }

    editing?.let { platform ->
        EditDialog(
            initial = platform,
            onDismiss = { editing = null },
            onConfirm = {
                vm.upsert(it)
                editing = null
            }
        )
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("恢复内置平台") },
            text = { Text("将丢弃全部自定义修改，恢复为初始内置平台列表。确定吗？") },
            confirmButton = {
                TextButton(onClick = {
                    vm.resetToBuiltin()
                    confirmReset = false
                }) { Text("恢复") }
            },
            dismissButton = {
                TextButton(onClick = { confirmReset = false }) { Text("取消") }
            }
        )
    }
}

@Composable
private fun ManageRow(
    platform: Platform,
    onEdit: () -> Unit,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = platform.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = buildString {
                        append(if (platform.builtin) "内置" else "自定义")
                        if (platform.packageName.isNotBlank()) {
                            append(" · ")
                            append(platform.packageName)
                        }
                        append(" · ")
                        append(platform.targets.size)
                        append(" 个入口")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(checked = platform.enabled, onCheckedChange = onToggle)

            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = "编辑")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "删除")
            }
        }
    }
}
