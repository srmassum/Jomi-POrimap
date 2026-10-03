package com.example

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.MainApp
import com.example.ui.screens.SplashScreen
import com.example.viewmodel.LandViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: LandViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instantly dismiss system splash screen so custom full-screen app_splash is displayed immediately
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                splashScreenView.remove()
            }
        }

        enableEdgeToEdge()
        setContent {
            var showSplash by remember { mutableStateOf(true) }

            if (showSplash) {
                SplashScreen(onDismiss = {
                    window.setBackgroundDrawable(null)
                    showSplash = false
                })
            } else {
                MainApp(viewModel = viewModel)
            }
        }
    }
}
