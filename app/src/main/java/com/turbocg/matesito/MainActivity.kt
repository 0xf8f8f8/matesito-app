package com.turbocg.matesito

import android.os.Bundle
// import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler // IMPORTANTE
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var webViewInstance by remember { mutableStateOf<WebView?>(null) }
            var canBack by remember { mutableStateOf(false) }
            BackHandler(enabled = canBack) {
                webViewInstance?.goBack()
            }
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                canBack = view?.canGoBack() ?: false
                            }
                        }
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            cacheMode = WebSettings.LOAD_NO_CACHE
                        }
                        clearCache(true)
                        loadUrl("https://matesito.com.ar")

                        webViewInstance = this
                    }
                }
            )
        }
    }
}
