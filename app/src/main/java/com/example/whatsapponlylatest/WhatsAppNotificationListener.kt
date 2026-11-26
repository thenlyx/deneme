package com.example.whatsapponlylatest

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class WhatsAppNotificationListener : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        keepOnlyNewest()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName == WHATSAPP_PACKAGE && !sbn.isGroupSummary()) keepOnlyNewest()
    }

    private fun keepOnlyNewest() {
        val messages = activeNotifications.orEmpty()
            .filter { it.packageName == WHATSAPP_PACKAGE && !it.isGroupSummary() }
        val newest = messages.maxByOrNull { it.postTime } ?: return
        messages
            .filter { it.key != newest.key }
            .forEach { runCatching { cancelNotification(it.key) } }
    }

    private fun StatusBarNotification.isGroupSummary() =
        notification.flags and Notification.FLAG_GROUP_SUMMARY != 0

    private companion object {
        const val WHATSAPP_PACKAGE = "com.whatsapp"
    }
}
