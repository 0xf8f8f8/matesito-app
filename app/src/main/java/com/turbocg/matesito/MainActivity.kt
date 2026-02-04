package com.turbocg.matesito

// import android.webkit.CookieManager
import android.graphics.Color
import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.turbocg.matesito.ui.theme.MatesitoTheme

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                Color.parseColor("#252525")
            )
        )
        setContent {
            MatesitoTheme {
                var webViewInstance by remember { mutableStateOf<WebView?>(null) }
                var canBack by remember { mutableStateOf(false) }
                var filePathCallback by remember {
                    mutableStateOf<android.webkit.ValueCallback<Array<android.net.Uri>>?>(
                        null
                    )
                }
                var hasError by remember { mutableStateOf(false) }
                val fileChooserLauncher =
                    androidx.activity.compose.rememberLauncherForActivityResult(
                        contract = androidx.activity.result.contract.ActivityResultContracts.OpenMultipleDocuments()
                    ) { uris ->
                        if (uris.isNotEmpty()) {
                            filePathCallback?.onReceiveValue(uris.toTypedArray())
                        } else {
                            filePathCallback?.onReceiveValue(null)
                        }
                        filePathCallback = null
                    }
                BackHandler(enabled = canBack || hasError) {
                    if (hasError) {
                        hasError = false
                        webViewInstance?.reload()
                    } else {
                        webViewInstance?.goBack()
                    }
                }
                if (hasError) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(androidx.compose.ui.graphics.Color(0xFF252525)), // Y acá el de Compose,
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.mate_caido),
                            modifier = Modifier.size(120.dp),
                            contentDescription = "Sin conexión"
                        )
                    }
                } else {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding(),
                        factory = { context ->
                            WebView(context).apply {
                                webChromeClient = object : android.webkit.WebChromeClient() {
                                    override fun onShowFileChooser(
                                        webView: WebView?,
                                        callback: android.webkit.ValueCallback<Array<android.net.Uri>>?,
                                        params: FileChooserParams?
                                    ): Boolean {
                                        filePathCallback?.onReceiveValue(null)
                                        filePathCallback = callback
                                        fileChooserLauncher.launch(arrayOf("*/*"))
                                        return true
                                    }
                                }
                                webViewClient = object : WebViewClient() {
                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        view?.canGoBack() ?: false
                                    }
                                    override fun onReceivedError(
                                        view: WebView?,
                                        request: WebResourceRequest?,
                                        error: WebResourceError?
                                    ) {
                                        if (request?.isForMainFrame == true) {
                                            view?.loadUrl("about:blank")
                                            hasError = true
                                        }
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
    }
}