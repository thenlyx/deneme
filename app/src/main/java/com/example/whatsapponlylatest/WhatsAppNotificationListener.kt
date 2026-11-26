package com.example.whatsapponlylatest

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

/**
 * Kural (kayıtlı durum yok, kilitlenme yok):
 *  - Grup özeti bildirimleri silinir.
 *  - Mesaj zamanı (Notification.when) yayın zamanından çok eskiyse, bu WhatsApp'ın
 *    eski bir sohbeti yeniden yayınlamasıdır -> silinir.
 *  - Taze bir bildirim gelince diğer tüm WhatsApp bildirimleri silinir, yalnız o kalır.
 */
class WhatsAppNotificationListener : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        val active = activeNotifications
            ?.filter { it.packageName == WHATSAPP_PACKAGE }
            ?: return

        active.filter { isGroupSummary(it) || isStale(it) }.forEach { safelyCancel(it.key) }

        val newest = active
            .filter { !isGroupSummary(it) && !isStale(it) }
            .maxByOrNull { messageTime(it) }
        if (newest != null) removeOtherWhatsAppNotifications(newest.key)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName != WHATSAPP_PACKAGE) return

        if (isGroupSummary(sbn) || isStale(sbn)) {
            safelyCancel(sbn.key)
            return
        }
        removeOtherWhatsAppNotifications(sbn.key)
    }

    private fun removeOtherWhatsAppNotifications(keepKey: String) {
        activeNotifications
            ?.filter { it.packageName == WHATSAPP_PACKAGE && it.key != keepKey }
            ?.forEach { safelyCancel(it.key) }
    }

    private fun messageTime(sbn: StatusBarNotification): Long {
        val w = sbn.notification.`when`
        return if (w > 0L) w else sbn.postTime
    }

    // Mesaj zamanı yayın zamanından 5 dakikadan fazla eskiyse "eski mesaj" say.
    private fun isStale(sbn: StatusBarNotification): Boolean {
        val w = sbn.notification.`when`
        if (w <= 0L) return false
        return sbn.postTime - w > STALE_MS
    }

    private fun isGroupSummary(sbn: StatusBarNotification): Boolean {
        return (sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY) != 0
    }

    private fun safelyCancel(key: String) {
        try {
            cancelNotification(key)
        } catch (_: SecurityException) {
        } catch (_: IllegalStateException) {
        }
    }

    companion object {
        private const val WHATSAPP_PACKAGE = "com.whatsapp"
        private const val STALE_MS = 5 * 60 * 1000L
    }
}
