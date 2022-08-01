package com.wolandpl.sliidecodingtask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.wolandpl.sliidecodingtask.ui.compose.MainScreen
import com.wolandpl.sliidecodingtask.ui.theme.SliideCodingTaskTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SliideCodingTaskTheme {
                MainScreen()
            }
        }
    }
}
