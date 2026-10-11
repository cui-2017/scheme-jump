package com.traveler.schemejump.data

import com.traveler.schemejump.model.JumpTarget
import com.traveler.schemejump.model.Platform

/**
 * 内置平台库。
 *
 * 注意：第三方 App 的 URL Scheme 随时可能被官方调整或回收，
 * 这里收集的是撰写时的公开可用值，**务必在真机上逐条实测**。
 * 若某条失效，直接在 App 内编辑该平台的模板即可，无需重新编译。
 */
object BuiltinPlatforms {

    val list: List<Platform> = listOf(

        Platform(
            id = "xhs",
            name = "小红书",
            packageName = "com.xingin.xhs",
            builtin = true,
            targets = listOf(
                JumpTarget("搜索", "xhsdiscover://search/result?keyword={kw}"),
                JumpTarget("我的", "xhsdiscover://profile"),
                JumpTarget("收到的赞藏", "xhsdiscover://message/collections")
            )
        ),

        Platform(
            id = "douyin",
            name = "抖音",
            packageName = "com.ss.android.ugc.aweme",
            builtin = true,
            targets = listOf(
                JumpTarget("搜索", "snssdk1128://search?keyword={kw}"),
                JumpTarget("我的", "snssdk1128://user/profile/")
            )
        ),

        Platform(
            id = "bili",
            name = "哔哩哔哩",
            packageName = "tv.danmaku.bili",
            builtin = true,
            targets = listOf(
                JumpTarget("搜索", "bilibili://search?keyword={kw}"),
                JumpTarget("我的收藏", "bilibili://user_center/favourite"),
                JumpTarget("观看历史", "bilibili://user_center/history")
            )
        ),

        Platform(
            id = "taobao",
            name = "淘宝",
            packageName = "com.taobao.taobao",
            builtin = true,
            targets = listOf(
                JumpTarget("搜索", "taobao://s.taobao.com/search?q={kw}")
            )
        ),

        Platform(
            id = "zhihu",
            name = "知乎",
            packageName = "com.zhihu.android",
            builtin = true,
            targets = listOf(
                JumpTarget("搜索", "zhihu://search?q={kw}")
            )
        ),

        Platform(
            id = "weibo",
            name = "微博",
            packageName = "com.sina.weibo",
            builtin = true,
            targets = listOf(
                JumpTarget("搜索", "sinaweibo://searchall?q={kw}")
            )
        ),

        Platform(
            id = "kuaishou",
            name = "快手",
            packageName = "com.smile.gifmaker",
            builtin = true,
            targets = listOf(
                JumpTarget("搜索", "kwai://search?keyword={kw}")
            )
        ),

        Platform(
            id = "douban",
            name = "豆瓣",
            packageName = "com.douban.frodo",
            builtin = true,
            targets = listOf(
                JumpTarget("搜索", "douban:///search?q={kw}")
            )
        )
    )
}
