package com.traveler.schemejump.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.traveler.schemejump.model.JumpTarget
import com.traveler.schemejump.model.Platform

/**
 * 新建 / 编辑平台的弹窗。
 * initial 为 null 表示新建。
 */
@Composable
fun EditDialog(
    initial: Platform?,
    onDismiss: () -> Unit,
    onConfirm: (Platform) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name.orEmpty()) }
    var packageName by remember { mutableStateOf(initial?.packageName.orEmpty()) }

    val targets = remember {
        mutableStateListOf<JumpTarget>().apply {
            val seed = initial?.targets
            if (!seed.isNullOrEmpty()) addAll(seed)
            else add(JumpTarget("搜索", "myscheme://search?keyword={kw}"))
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "新建平台" else "编辑平台") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("平台名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text("包名（可留空）") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "跳转入口",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                targets.forEachIndexed { index, target ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = target.label,
                                onValueChange = { targets[index] = target.copy(label = it) },
                                label = { Text("按钮名") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { targets.removeAt(index) }) {
                                Icon(Icons.Filled.Close, contentDescription = "删除该入口")
                            }
                        }
                        OutlinedTextField(
                            value = target.template,
                            onValueChange = { targets[index] = target.copy(template = it) },
                            label = { Text("Scheme 模板") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                TextButton(onClick = { targets.add(JumpTarget("新入口", "")) }) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("添加入口")
                }

                Text(
                    text = "可用变量：{kw} 关键词 · {clip} 剪贴板 · {date} 日期",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank() && targets.any { it.template.isNotBlank() },
                onClick = {
                    val validTargets = targets
                        .filter { it.template.isNotBlank() }
                        .map { it.copy(label = it.label.ifBlank { "跳转" }) }
                    onConfirm(
                        Platform(
                            id = initial?.id ?: "custom_${System.currentTimeMillis()}",
                            name = name.trim(),
                            packageName = packageName.trim(),
                            targets = validTargets,
                            builtin = initial?.builtin ?: false,
                            enabled = initial?.enabled ?: true
                        )
                    )
                }
            ) { Text("保存") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
