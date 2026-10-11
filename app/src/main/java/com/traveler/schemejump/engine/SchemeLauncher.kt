package com.traveler.schemejump.engine

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 跳转引擎：模板变量替换 + Intent 分发 + 失败兜底。
 *
 * 关键点：Android 11+ 的「包可见性」只限制"查询"，不限制"启动"。
 * 因此即使某个自定义平台的 scheme 没有预先写进 AndroidManifest，
 * 只要目标 App 真的注册了该 scheme，隐式 Intent 依然能成功唤起；
 * 无非是没法提前知道它"装没装"。装没装这件事我们靠 try/catch 兜底即可。
 */
object SchemeLauncher {

    /**
     * 解析模板里的变量：
     *   {kw}   -> 关键词（URL 编码）
     *   {clip} -> 剪贴板内容（URL 编码）
     *   {date} -> 当天日期 yyyy-MM-dd
     */
    fun resolve(template: String, keyword: String, clipboard: String = ""): String {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return template
            .replace("{kw}", Uri.encode(keyword))
            .replace("{clip}", Uri.encode(clipboard))
            .replace("{date}", date)
    }

    /** 该模板是否用到了关键词变量（用于提示用户先输入关键词）。 */
    fun needsKeyword(template: String): Boolean = template.contains("{kw}")

    /**
     * 执行跳转。
     * @return true 表示系统已接受该 Intent（不代表目标页面一定存在）。
     */
    fun jump(context: Context, url: String, packageName: String? = null): Boolean {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (!packageName.isNullOrBlank()) setPackage(packageName)
        }
        return try {
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            toast(context, "跳转失败：目标应用未安装，或该协议已失效")
            false
        } catch (e: Exception) {
            toast(context, "跳转失败：${e.message ?: "未知错误"}")
            false
        }
    }

    /** 检测目标包是否已安装（依赖 AndroidManifest 中的包可见性声明）。 */
    fun isInstalled(context: Context, packageName: String): Boolean {
        if (packageName.isBlank()) return false
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    /** 打开应用商店详情页；商店不可用时退回浏览器搜索。 */
    fun openStore(context: Context, packageName: String, appName: String) {
        val candidates = ArrayList<String>()
        if (packageName.isNotBlank()) {
            candidates.add("market://details?id=$packageName")
            candidates.add("https://www.coolapk.com/apk/$packageName")
        }
        candidates.add("https://www.baidu.com/s?wd=" + Uri.encode("$appName 下载"))

        for (u in candidates) {
            try {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(u)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                )
                return
            } catch (e: Exception) {
                // 换下一个候选地址
            }
        }
        toast(context, "无法打开应用商店或浏览器")
    }

    private fun toast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}
