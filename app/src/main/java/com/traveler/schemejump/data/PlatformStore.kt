package com.traveler.schemejump.data

import android.content.Context
import com.traveler.schemejump.model.JumpTarget
import com.traveler.schemejump.model.Platform
import org.json.JSONArray
import org.json.JSONObject

/**
 * 平台配置的本地持久化。
 *
 * 直接用 SharedPreferences 存一段 JSON，刻意不引入 Room / DataStore，
 * 让整个工程的第三方依赖保持最少，降低构建失败概率。
 */
object PlatformStore {

    private const val PREF_NAME = "scheme_jump"
    private const val KEY_PLATFORMS = "platforms_json"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /** 读取配置；从未保存过则回落到内置平台库。 */
    fun load(context: Context): List<Platform> {
        val raw = prefs(context).getString(KEY_PLATFORMS, null)
        if (raw.isNullOrBlank()) return BuiltinPlatforms.list
        return try {
            decode(raw)
        } catch (e: Exception) {
            BuiltinPlatforms.list
        }
    }

    /** 保存配置。 */
    fun save(context: Context, platforms: List<Platform>) {
        prefs(context).edit()
            .putString(KEY_PLATFORMS, encode(platforms))
            .apply()
    }

    /** 清空自定义配置，回到内置状态。 */
    fun reset(context: Context) {
        prefs(context).edit().remove(KEY_PLATFORMS).apply()
    }

    // ---------------- JSON 编解码 ----------------

    private fun encode(platforms: List<Platform>): String {
        val arr = JSONArray()
        platforms.forEach { p ->
            val targets = JSONArray()
            p.targets.forEach { t ->
                targets.put(
                    JSONObject()
                        .put("label", t.label)
                        .put("template", t.template)
                )
            }
            arr.put(
                JSONObject()
                    .put("id", p.id)
                    .put("name", p.name)
                    .put("packageName", p.packageName)
                    .put("builtin", p.builtin)
                    .put("enabled", p.enabled)
                    .put("targets", targets)
            )
        }
        return arr.toString()
    }

    private fun decode(raw: String): List<Platform> {
        val arr = JSONArray(raw)
        val out = ArrayList<Platform>(arr.length())
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            val targetsArr = o.optJSONArray("targets")
            val targets = ArrayList<JumpTarget>()
            if (targetsArr != null) {
                for (j in 0 until targetsArr.length()) {
                    val t = targetsArr.getJSONObject(j)
                    targets.add(
                        JumpTarget(
                            label = t.optString("label", "跳转"),
                            template = t.optString("template", "")
                        )
                    )
                }
            }
            out.add(
                Platform(
                    id = o.optString("id", "p_$i"),
                    name = o.optString("name", "未命名"),
                    packageName = o.optString("packageName", ""),
                    targets = targets,
                    builtin = o.optBoolean("builtin", false),
                    enabled = o.optBoolean("enabled", true)
                )
            )
        }
        return out
    }
}
