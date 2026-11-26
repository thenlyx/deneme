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
            .maxByOrNull { messageTime(it) }

        if (newestChild != null) {
            prefs.edit()
                .putLong(KEY_LAST_POST_TIME, maxOf(messageTime(newestChild), prefs.getLong(KEY_LAST_POST_TIME, 0L)))
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
        val time = messageTime(sbn)

        // Mesajın kendi zamanına (Notification.when) bakılır: WhatsApp eski sohbetleri
        // yeniden yayınlasa bile mesaj zamanları eski kalır. Daha eski mesajı hemen kaldır.
        if (time < lastTime ||
            (time == lastTime && lastKey != null && sbn.key != lastKey)) {
            safelyCancel(sbn.key)
            return
        }

        prefs.edit()
            .putLong(KEY_LAST_POST_TIME, time)
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

    // Mesajın gerçek zamanı; yoksa bildirimin yayın zamanı.
    private fun messageTime(sbn: StatusBarNotification): Long {
        val w = sbn.notification.`when`
        return if (w > 0L) w else sbn.postTime
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
        private const val KEY_LAST_POST_TIME = "last_message_time"
        private const val KEY_LAST_KEY = "last_message_key"
    }
}
