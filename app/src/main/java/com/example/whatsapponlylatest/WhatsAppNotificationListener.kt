package com.example.whatsapponlylatest

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

/**
 * Güvenli kural:
 *  - Grup özetlerine DOKUNULMAZ (özeti silmek yeni mesajı da götürebiliyor).
 *  - Mesaj zamanı yayın zamanından 6 saatten fazla eskiyse (eski sohbetin yeniden
 *    yayınlanması) yalnız o bildirim silinir.
 *  - Taze bildirim gelince diğer tekil WhatsApp bildirimleri silinir.
 *  - Uygulamadaki "Filtre" kapalıysa hiçbir şey silinmez, yalnız kayıt tutulur.
 */
class WhatsAppNotificationListener : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        AppLog.add(this, "servis bağlandı")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName != WHATSAPP_PACKAGE) return

        val summary = isGroupSummary(sbn)
        val ageMin = ageMinutes(sbn)
        val on = AppLog.isFilterEnabled(this)

        if (summary) {
            AppLog.add(this, "özet bildirimi (dokunulmadı)")
            return
        }

        if (isStale(sbn)) {
            if (on) safelyCancel(sbn.key)
            AppLog.add(this, "ESKİ yaş=${ageMin}dk -> ${if (on) "silindi" else "filtre kapalı, bırakıldı"}")
            return
        }

        var removed = 0
        if (on) {
            activeNotifications
                ?.filter { it.packageName == WHATSAPP_PACKAGE && it.key != sbn.key && !isGroupSummary(it) }
                ?.forEach { safelyCancel(it.key); removed++ }
        }
        AppLog.add(this, "YENİ yaş=${ageMin}dk -> kaldı, ${removed} diğer silindi")
    }

    private fun ageMinutes(sbn: StatusBarNotification): Long {
        val w = sbn.notification.`when`
        return if (w <= 0L) 0L else (sbn.postTime - w) / 60000L
    }

    private fun isStale(sbn: StatusBarNotification): Boolean {
        val w = sbn.notification.`when`
        if (w <= 0L) return false
        return sbn.postTime - w > STALE_MS
    }

    private fun isGroupSummary(sbn: StatusBarNotification): Boolean =
        (sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY) != 0

    private fun safelyCancel(key: String) {
        try {
            cancelNotification(key)
        } catch (_: SecurityException) {
        } catch (_: IllegalStateException) {
        }
    }

    companion object {
        private const val WHATSAPP_PACKAGE = "com.whatsapp"
        private const val STALE_MS = 6 * 60 * 60 * 1000L
    }
}
