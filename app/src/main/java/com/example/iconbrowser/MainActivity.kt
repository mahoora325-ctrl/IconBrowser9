package com.example.iconbrowser

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.iconbrowser.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val homeUrl = "https://www.google.com"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.databaseEnabled = true
        binding.webView.settings.useWideViewPort = true
        binding.webView.settings.loadWithOverviewMode = true

        // WebView's default User-Agent contains ";wv" (a WebView marker),
        // which Google's abuse detection treats as suspicious/automated
        // traffic and responds to with the "unusual traffic" reCAPTCHA page.
        // Presenting as a normal Chrome-for-Android UA avoids that.
        binding.webView.settings.userAgentString =
            "Mozilla/5.0 (Linux; Android 14; Pixel 7) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36"

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                binding.progressBar.visibility = View.VISIBLE
                url?.let { binding.urlInput.setText(it) }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                binding.progressBar.visibility = View.GONE
            }
        }

        // Silent, no-dialog wipe on open if the user turned this on in settings.
        if (HistoryPrivacy.isAutoClearEnabled(this)) {
            HistoryPrivacy.clearSilently(binding.webView)
        }

        binding.webView.loadUrl(homeUrl)

        binding.urlInput.setOnEditorActionListener { _, _, _ ->
            loadFromInput()
            true
        }

        binding.settingsButton.setOnClickListener {
            startActivity(android.content.Intent(this, SettingsActivity::class.java))
        }
    }

    private fun loadFromInput() {
        val raw = binding.urlInput.text.toString().trim()
        if (raw.isEmpty()) return

        val looksLikeUrl = raw.contains(".") && !raw.contains(" ")
        val target = when {
            raw.startsWith("http://") || raw.startsWith("https://") -> raw
            looksLikeUrl -> "https://$raw"
            else -> "https://www.google.com/search?q=${android.net.Uri.encode(raw)}"
        }
        binding.webView.loadUrl(target)
    }

    override fun onStop() {
        super.onStop()
        // App is closing / going to background — wipe silently if enabled.
        if (HistoryPrivacy.isAutoClearEnabled(this)) {
            HistoryPrivacy.clearSilently(binding.webView)
        }
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
