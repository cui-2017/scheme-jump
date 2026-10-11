package com.traveler.schemejump.model

/**
 * 一个跳转目标，例如「搜索」「我的收藏」。
 *
 * template 是 URL Scheme 模板，支持变量占位：
 *   {kw}   -> 当前输入框里的关键词
 *   {clip} -> 当前剪贴板文本
 *   {date} -> 当天日期 (yyyy-MM-dd)
 */
data class JumpTarget(
    val label: String,
    val template: String
)

/**
 * 一个平台（App）。
 *
 * @param packageName 目标 App 包名，用于安装检测、以及在跳转时限定包名。
 *                    留空时不做包名限定，直接按 scheme 让系统分发。
 */
data class Platform(
    val id: String,
    val name: String,
    val packageName: String = "",
    val targets: List<JumpTarget> = emptyList(),
    val builtin: Boolean = false,
    val enabled: Boolean = true
)
