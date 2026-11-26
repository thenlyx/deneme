package com.example.whatsapponlylatest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }
        val title = TextView(this).apply {
            text = "WhatsApp Tek Bildirim"
            textSize = 24f
            gravity = Gravity.CENTER
        }
        val info = TextView(this).apply {
            text = "Yeni WhatsApp mesajı geldiğinde önceki WhatsApp bildirimlerini kaldırır ve en yeni bildirimi bırakır. Sohbetleri açmaz, mesajları okumaz veya okundu yapmaz."
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 24)
        }
        status = TextView(this).apply {
            textSize = 15f
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 24)
        }
        val button = Button(this).apply {
            text = "Bildirim erişimini aç"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
        }
        layout.addView(title)
        layout.addView(info)
        layout.addView(status)
        layout.addView(button)
        setContentView(layout)
        updateStatus()
    }

    override fun onResume() {
        super.onResume()
        if (::status.isInitialized) updateStatus()
    }

    private fun updateStatus() {
        status.text = if (NotificationAccess.isEnabled(this)) {
            "Durum: Etkin"
        } else {
            "Durum: Kapalı — bildirim erişimini aç."
        }
    }
}
