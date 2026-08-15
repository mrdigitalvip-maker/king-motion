package com.kingmotion.editor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kingmotion.editor.ui.KingMotionApp
import com.kingmotion.editor.ui.theme.KingMotionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { KingMotionTheme { KingMotionApp() } }
    }
}
