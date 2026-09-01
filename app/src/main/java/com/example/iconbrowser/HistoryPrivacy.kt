package com.example.iconbrowser

import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView

/**
 * Controls the "clear history automatically" preference and performs the
 * actual silent wipe — no dialog, no toast, nothing shown to the user.
 */
object HistoryPrivacy {

    private const val PREFS = "history_privacy_prefs"
    private const val KEY_AUTO_CLEAR = "auto_clear_enabled"

    fun isAutoClearEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_AUTO_CLEAR, false)
    }

    fun setAutoClearEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_AUTO_CLEAR, enabled)
            .apply()
    }

    /**
     * Wipes browsing history/cache/cookies/site data for [webView] with no
     * user-facing feedback of any kind. Only call this when the caller has
     * already confirmed the preference is enabled.
     */
    fun clearSilently(webView: WebView) {
        webView.clearHistory()
        webView.clearFormData()
        webView.clearCache(true)

        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies(null)
        cookieManager.flush()

        WebStorage.getInstance().deleteAllData()
    }
}
