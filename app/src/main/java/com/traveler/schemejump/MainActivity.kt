package com.traveler.schemejump

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.traveler.schemejump.ui.AppRoot
import com.traveler.schemejump.ui.theme.SchemeJumpTheme

class MainActivity : ComponentActivity() {

    /**
     * 从别的 App「分享文本」进来时，把文本当作初始关键词。
     * 这样在任意 App 里选中文字 -> 分享 -> 选中本应用，就能免打字直接跳转。
     */
    private var sharedKeyword by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sharedKeyword = extractSharedText(intent)
        setContent {
            SchemeJumpTheme {
                AppRoot(sharedKeyword = sharedKeyword)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        sharedKeyword = extractSharedText(intent)
    }

    private fun extractSharedText(intent: Intent?): String? {
        if (intent == null) return null
        if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            return intent.getStringExtra(Intent.EXTRA_TEXT)
        }
        return null
    }
}
