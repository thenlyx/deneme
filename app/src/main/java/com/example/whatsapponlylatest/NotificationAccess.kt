package com.example.whatsapponlylatest

import android.content.ComponentName
import android.content.Context
import android.provider.Settings

object NotificationAccess {
    fun isEnabled(context: Context): Boolean {
        val enabled = Settings.Secure.getString(
            context.contentResolver, "enabled_notification_listeners"
        ) ?: return false
        val expected = ComponentName(context, WhatsAppNotificationListener::class.java)
        return enabled.split(':').any {
            ComponentName.unflattenFromString(it) == expected
        }
    }
}
