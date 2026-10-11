# Scheme 跳转

一个极简的 Android 工具：输入关键词，一键跳到小红书 / 抖音 / B 站 / 淘宝等 App 的搜索页或收藏页。

核心逻辑只有一个：**把关键词按模板拼成 URL Scheme，交给系统去唤起目标 App。**

---

## 一、功能

| 能力 | 说明 |
|---|---|
| 关键词跳转 | 输入关键词 → 点平台下的目标按钮 → 直接进目标 App 对应页面 |
| 多入口 | 每个平台可配多个入口，如「搜索 / 我的收藏 / 观看历史」 |
| 自定义平台 | App 内可增删改平台，无需重新编译 |
| 动态变量 | 模板支持 `{kw}` 关键词、`{clip}` 剪贴板、`{date}` 日期 |
| 剪贴板提速 | 一键把剪贴板内容灌进关键词框 |
| 分享入口 | 在别的 App 里选中文字 → 分享 → 本应用，自动带入关键词 |
| 安装状态 | 自动检测目标 App 是否已安装，未安装可一键去应用商店 |
| 失败兜底 | 跳转失败弹提示，不会静默无反应 |

---

## 二、云端构建（不需要本地装 Android Studio）

### 1. 推到 GitHub

```bash
cd schemejump
git init
git add .
git commit -m "init: scheme jump app"
git branch -M main
git remote add origin https://github.com/<你的用户名>/<仓库名>.git
git push -u origin main
```

### 2. 等自动构建

推送后，GitHub 仓库页 → **Actions** 标签 → 会看到 `Build APK` 正在跑，约 3~6 分钟。

也可以手动触发：Actions → Build APK → Run workflow。

### 3. 下载 APK

构建成功后，点进那次运行 → 页面底部 **Artifacts** → 下载 `SchemeJump-debug-apk.zip`，解压得到 `app-debug.apk`。

### 4. 安装

把 APK 传到手机安装，需要允许「安装未知来源应用」。

---

## 三、使用

1. 打开 App，输入关键词（或点「粘贴」取剪贴板）。
2. 在目标平台那一行，点「搜索」/「我的收藏」直接跳。
3. 右上角齿轮进入**管理平台**：新建、编辑、启停、删除。

### 添加自己的平台

管理平台 → 右上角 `+`：

- **平台名称**：随便写，如「网易云音乐」
- **包名**：可留空。填了才能做安装检测与商店跳转
- **按钮名**：显示在按钮上的字，如「搜索」
- **Scheme 模板**：`orpheus://search?keyword={kw}`

可用变量：

| 变量 | 含义 |
|---|---|
| `{kw}` | 当前关键词（自动 URL 编码） |
| `{clip}` | 当前剪贴板内容 |
| `{date}` | 当天日期 `yyyy-MM-dd` |

`{kw}` 可以出现多次，也可以拼接固定词，例如 `xhsdiscover://search/result?keyword={kw} 测评`。

---

## 四、内置平台

| 平台 | 搜索 | 其它入口 |
|---|---|---|
| 小红书 | ✅ | 我的、收到的赞藏 |
| 抖音 | ✅ | 我的 |
| 哔哩哔哩 | ✅ | 我的收藏、观看历史 |
| 淘宝 | ✅ | — |
| 知乎 | ✅ | — |
| 微博 | ✅ | — |
| 快手 | ✅ | — |
| 豆瓣 | ✅ | — |

> ⚠️ **这些 Scheme 是从公开资料收集的，第三方 App 随时可能调整或回收。**
> 上手第一件事：逐个点一遍，把失效的在「管理平台」里改掉即可，不需要重新构建。

---

## 五、排障

| 现象 | 原因 | 处理 |
|---|---|---|
| 点了没反应 | 目标 App 没装，或协议已失效 | 看是否有 Toast；未安装点「去安装」 |
| 跳转被拦截 | 小米/华为/OPPO/vivo 的关联启动限制 | 系统设置 → 应用 → 本应用 → 允许「后台弹出界面 / 关联启动 / 自启动」 |
| 显示未安装但其实装了 | 包可见性未生效 | 确认已授予「查询所有应用」权限（本工程已在清单声明） |
| 关键词没带进去 | 该 App 不支持直接搜索 Scheme | 换用该 App 支持的其他入口，或只用它做首页跳转 |

---

## 六、工程结构

```
schemejump/
├── .github/workflows/build.yml   # 云端构建
├── app/src/main/
│   ├── AndroidManifest.xml       # 权限 + queries 声明
│   ├── java/com/traveler/schemejump/
│   │   ├── MainActivity.kt       # 入口，处理分享进来的文本
│   │   ├── AppViewModel.kt       # 状态管理
│   │   ├── model/Platform.kt     # 数据模型
│   │   ├── data/BuiltinPlatforms.kt  # 内置 8 个平台
│   │   ├── data/PlatformStore.kt     # 配置持久化
│   │   ├── engine/SchemeLauncher.kt  # 跳转引擎
│   │   └── ui/                   # Compose 界面
│   └── res/                      # 图标、主题、文案
└── app/build.gradle.kts
```

**技术栈**：Kotlin 2.0.21 + Jetpack Compose (BOM 2024.12.01) + AGP 8.7.3 + Gradle 8.11.1，`minSdk 24 / targetSdk 35`，零数据库、零第三方 SDK。
