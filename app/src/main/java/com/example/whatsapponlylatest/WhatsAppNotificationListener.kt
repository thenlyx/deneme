package com.example.whatsapponlylatest

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class WhatsAppNotificationListener : NotificationListenerService() {

    private val prefs by lazy {
        getSharedPreferences("latest_whatsapp_notification", MODE_PRIVATE)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        // Servis yeniden bağlandığında mevcut bildirimler arasından en yenisini koru.
        val active = activeNotifications
            ?.filter { it.packageName == WHATSAPP_PACKAGE }
            ?: return

        val newestChild = active
            .filter { !isGroupSummary(it) }
            .maxByOrNull { it.postTime }

        if (newestChild != null) {
            prefs.edit()
                .putLong(KEY_LAST_POST_TIME, newestChild.postTime)
                .putString(KEY_LAST_KEY, newestChild.key)
                .apply()
            removeOtherWhatsAppNotifications(newestChild.key)
        } else {
            active.filter { isGroupSummary(it) }.forEach { safelyCancel(it.key) }
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName != WHATSAPP_PACKAGE) return

        // WhatsApp'ın grup özeti eski sohbetleri tekrar alt alta gösterebilir.
        // Özeti kaldır; tekil, en yeni sohbet bildirimini koru.
        if (isGroupSummary(sbn)) {
            safelyCancel(sbn.key)
            return
        }

        val lastTime = prefs.getLong(KEY_LAST_POST_TIME, 0L)
        val lastKey = prefs.getString(KEY_LAST_KEY, null)

        // WhatsApp eski bir sohbet bildirimini yeniden yayınlarsa,
        // daha yeni bildirim yerine onu bırakma.
        if (sbn.postTime < lastTime ||
            (sbn.postTime == lastTime && lastKey != null && sbn.key != lastKey)) {
            safelyCancel(sbn.key)
            return
        }

        prefs.edit()
            .putLong(KEY_LAST_POST_TIME, sbn.postTime)
            .putString(KEY_LAST_KEY, sbn.key)
            .apply()

        removeOtherWhatsAppNotifications(sbn.key)
    }

    private fun removeOtherWhatsAppNotifications(keepKey: String) {
        activeNotifications
            ?.filter {
                it.packageName == WHATSAPP_PACKAGE && it.key != keepKey
            }
            ?.forEach { safelyCancel(it.key) }
    }

    private fun isGroupSummary(sbn: StatusBarNotification): Boolean {
        return (sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY) != 0
    }

    private fun safelyCancel(key: String) {
        try {
            cancelNotification(key)
        } catch (_: SecurityException) {
            // Bildirim erişimi kapatılmışsa işlem yapma.
        } catch (_: IllegalStateException) {
            // Servis henüz bağlanmadıysa işlem yapma.
        }
    }

    companion object {
        private const val WHATSAPP_PACKAGE = "com.whatsapp"
        private const val KEY_LAST_POST_TIME = "last_post_time"
        private const val KEY_LAST_KEY = "last_notification_key"
    }
}
