package com.example.whatsapponlylatest

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Küçük tanı kaydı: yalnızca zaman/sayı/karar yazar, mesaj içeriği yazmaz. */
object AppLog {
    private const val PREFS = "app_log"
    private const val KEY_LOG = "log"
    private const val KEY_ENABLED = "filter_enabled"
    private const val MAX = 14

    fun isFilterEnabled(c: Context) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, true)

    fun setFilterEnabled(c: Context, v: Boolean) {
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_ENABLED, v).apply()
    }

    @Synchronized
    fun add(c: Context, line: String) {
        val p = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val t = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
        val lines = (p.getString(KEY_LOG, "") ?: "").split("\n").filter { it.isNotBlank() }
        val out = (lines + "$t $line").takeLast(MAX)
        p.edit().putString(KEY_LOG, out.joinToString("\n")).apply()
    }

    fun read(c: Context): String =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_LOG, "") ?: ""
}
