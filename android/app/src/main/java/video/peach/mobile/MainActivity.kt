package video.peach.mobile

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView = findViewById(R.id.webView)
        configureWebView()

        if (savedInstanceState == null) {
            loadConfiguredUrl()
        } else {
            webView.restoreState(savedInstanceState)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            builtInZoomControls = false
            displayZoomControls = false
            mediaPlaybackRequiresUserGesture = true
        }
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()
    }

    private fun loadConfiguredUrl() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val url = prefs.getString(PREF_SERVER_URL, DEFAULT_SERVER_URL).orEmpty()
        if (isHttpUrl(url)) {
            webView.loadUrl(url)
        } else {
            promptServerUrl()
        }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.main_actions, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_set_server -> {
                promptServerUrl()
                true
            }
            R.id.action_reload -> {
                webView.reload()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
            return
        }
        super.onBackPressed()
    }

    private fun promptServerUrl() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val currentUrl = prefs.getString(PREF_SERVER_URL, DEFAULT_SERVER_URL).orEmpty()
        val input = EditText(this).apply {
            setText(currentUrl)
            hint = getString(R.string.server_dialog_hint)
            setSingleLine()
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.server_dialog_title)
            .setView(input)
            .setPositiveButton(R.string.server_dialog_save) { _, _ ->
                val candidate = input.text?.toString().orEmpty().trim()
                if (!isHttpUrl(candidate)) {
                    Toast.makeText(this, R.string.invalid_server_url, Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                prefs.edit().putString(PREF_SERVER_URL, candidate).apply()
                webView.loadUrl(candidate)
            }
            .setNegativeButton(R.string.server_dialog_cancel, null)
            .show()
    }

    private fun isHttpUrl(url: String): Boolean {
        val uri = runCatching { url.toUri() }.getOrNull() ?: return false
        val scheme = uri.scheme?.lowercase() ?: return false
        return (scheme == "http" || scheme == "https") && !uri.host.isNullOrBlank()
    }

    companion object {
        private const val PREFS_NAME = "peach_mobile"
        private const val PREF_SERVER_URL = "server_url"
        private const val DEFAULT_SERVER_URL = "http://127.0.0.1:18888"
    }
}
