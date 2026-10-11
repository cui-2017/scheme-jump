package com.traveler.schemejump

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.traveler.schemejump.data.BuiltinPlatforms
import com.traveler.schemejump.data.PlatformStore
import com.traveler.schemejump.model.Platform

class AppViewModel(app: Application) : AndroidViewModel(app) {

    /** 当前生效的平台列表（含内置 + 自定义）。 */
    var platforms by mutableStateOf(PlatformStore.load(app))
        private set

    /** 新增或更新一个平台。 */
    fun upsert(platform: Platform) {
        val current = platforms.toMutableList()
        val idx = current.indexOfFirst { it.id == platform.id }
        if (idx >= 0) current[idx] = platform else current.add(platform)
        commit(current)
    }

    /** 删除一个平台。 */
    fun remove(id: String) {
        commit(platforms.filterNot { it.id == id })
    }

    /** 启用 / 停用一个平台。 */
    fun setEnabled(id: String, enabled: Boolean) {
        commit(platforms.map { if (it.id == id) it.copy(enabled = enabled) else it })
    }

    /** 丢弃所有自定义配置，恢复为内置平台库。 */
    fun resetToBuiltin() {
        PlatformStore.reset(getApplication())
        platforms = BuiltinPlatforms.list
    }

    private fun commit(list: List<Platform>) {
        platforms = list
        PlatformStore.save(getApplication(), list)
    }
}
