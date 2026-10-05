package com.turbocg.matesito

// import android.webkit.CookieManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView
import androidx.core.graphics.toColorInt


@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
class MainActivity : ComponentActivity() {
    private lateinit var runtime: GeckoRuntime
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                "#0a0a0a".toColorInt()
            ),
            navigationBarStyle = SystemBarStyle.dark(
                "#0a0a0a".toColorInt()
            )
        )
        val container = FrameLayout(this)
        runtime = GeckoRuntime.create(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        val geckoView = GeckoView(this)
        val session = GeckoSession()
        session.open(runtime)
        geckoView.setSession(session)
        container.addView(geckoView, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))
        setContentView(container)
        session.loadUri("https://matesito.com.ar")
        ViewCompat.setOnApplyWindowInsetsListener(container) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, bars.top, 0, bars.bottom)
            insets
        }

    }
    override fun onDestroy() {
        super.onDestroy()
    }
}