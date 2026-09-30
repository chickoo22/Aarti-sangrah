package com.example.notifications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import java.util.Calendar

/**
 * BroadcastReceiver triggered by AlarmManager or System Boot to display
 * local prayer notifications and re-arm daily recurring alarms.
 */
class PrayerReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        // If system reboots, re-schedule all configured alarms locally
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
            action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            NotificationHelper.createNotificationChannels(context)
            NotificationHelper.syncAllRemindersFromPreferences(context)
            return
        }

        // Handle alarm trigger
        NotificationHelper.createNotificationChannels(context)

        val reminderId = intent.getIntExtra(NotificationHelper.EXTRA_REMINDER_ID, NotificationHelper.ID_MORNING_REMINDER)
        val title = intent.getStringExtra(NotificationHelper.EXTRA_NOTIFICATION_TITLE)
            ?: "🕉️ नित्य पूजा व आरती वेळ (Daily Prayer Reminder)"
        val message = intent.getStringExtra(NotificationHelper.EXTRA_NOTIFICATION_MESSAGE)
            ?: "दिवसाची सुरुवात ईश्वराच्या नामस्मरणाने करा. श्री गणेश व हनुमान चालीसा पठण करा."
        val targetAartiId = intent.getIntExtra(NotificationHelper.EXTRA_TARGET_AARTI_ID, 4)

        // For Tuesday / Saturday special check if enabled
        val prefs = ReminderPreferences(context)
        if (reminderId == NotificationHelper.ID_SPECIAL_REMINDER) {
            val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            if (dayOfWeek != Calendar.TUESDAY && dayOfWeek != Calendar.SATURDAY) {
                // Not Tuesday or Saturday, skip notification but keep cycle
                NotificationHelper.syncAllRemindersFromPreferences(context)
                return
            }
        }

        // Tap opens MainActivity directly with optional target aarti
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(NotificationHelper.EXTRA_TARGET_AARTI_ID, targetAartiId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID_PRAYER)
            .setSmallIcon(R.drawable.ic_notification_prayer)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(0xFFFF9933.toInt()) // Deep Saffron / Bhagwa (#FF9933)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(reminderId, notification)
        } catch (_: SecurityException) {
            // Permission restricted
        }

        // Re-arm next occurrence locally
        NotificationHelper.syncAllRemindersFromPreferences(context)
    }
}
