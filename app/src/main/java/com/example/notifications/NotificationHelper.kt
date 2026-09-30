package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import java.util.Calendar
import java.util.Locale

/**
 * Manages all local notifications for daily aarti and prayer reminders.
 * Runs 100% locally on the device — no backend or server required.
 */
object NotificationHelper {

    const val CHANNEL_ID_PRAYER = "mantramaya_daily_prayers"
    const val CHANNEL_NAME = "Daily Pooja & Aarti Reminders"
    const val CHANNEL_DESC = "Notifications for morning prayer, evening aarti, and devotional chants"

    const val ID_INSTANT_TEST = 100
    const val ID_MORNING_REMINDER = 101
    const val ID_EVENING_REMINDER = 102
    const val ID_SPECIAL_REMINDER = 103

    const val EXTRA_NOTIFICATION_TITLE = "extra_notification_title"
    const val EXTRA_NOTIFICATION_MESSAGE = "extra_notification_message"
    const val EXTRA_REMINDER_ID = "extra_reminder_id"
    const val EXTRA_TARGET_AARTI_ID = "extra_target_aarti_id"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_PRAYER,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    /**
     * Sends an immediate local test notification to demonstrate local alerts.
     */
    fun sendInstantNotification(
        context: Context,
        title: String = "🕉️ मंत्रमया: शुभ प्रभात व नित्य स्मरण",
        message: String = "आजचे शुभ स्मरण: श्री गणेश आरती, श्री हनुमान चालीसा व विठ्ठल आरती पठण करा. दिवस मंगलमय जावो!",
        targetAartiId: Int? = 4 // Hanuman Chalisa by default
    ) {
        createNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            targetAartiId?.let { putExtra(EXTRA_TARGET_AARTI_ID, it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            ID_INSTANT_TEST,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_PRAYER)
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
            NotificationManagerCompat.from(context).notify(ID_INSTANT_TEST, notification)
        } catch (_: SecurityException) {
            // Permission denied on Android 13+
        }
    }

    /**
     * Schedules a daily recurring alarm via AlarmManager.
     */
    fun scheduleDailyReminder(
        context: Context,
        reminderId: Int,
        hour: Int,
        minute: Int,
        title: String,
        message: String,
        targetAartiId: Int? = null
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, PrayerReminderReceiver::class.java).apply {
            action = "com.example.ACTION_PRAYER_REMINDER"
            putExtra(EXTRA_REMINDER_ID, reminderId)
            putExtra(EXTRA_NOTIFICATION_TITLE, title)
            putExtra(EXTRA_NOTIFICATION_MESSAGE, message)
            targetAartiId?.let { putExtra(EXTRA_TARGET_AARTI_ID, it) }
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            // Inexact fallback if exact alarm permission is restricted
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    /**
     * Cancels an existing scheduled reminder alarm.
     */
    fun cancelReminder(context: Context, reminderId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, PrayerReminderReceiver::class.java).apply {
            action = "com.example.ACTION_PRAYER_REMINDER"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Reads preferences and schedules or cancels all active reminders.
     */
    fun syncAllRemindersFromPreferences(context: Context) {
        val prefs = ReminderPreferences(context)

        // Morning Pooja reminder
        if (prefs.isMorningEnabled) {
            scheduleDailyReminder(
                context = context,
                reminderId = ID_MORNING_REMINDER,
                hour = prefs.morningHour,
                minute = prefs.morningMinute,
                title = "🌅 शुभ प्रभात! नित्य पूजा व आरती वेळ",
                message = "पवित्र दिवसाची सुरुवात करा: श्री गणेश आरती व संकटमोचन हनुमान चालीसा पठण करा.",
                targetAartiId = 4
            )
        } else {
            cancelReminder(context, ID_MORNING_REMINDER)
        }

        // Evening Aarti reminder
        if (prefs.isEveningEnabled) {
            scheduleDailyReminder(
                context = context,
                reminderId = ID_EVENING_REMINDER,
                hour = prefs.eveningHour,
                minute = prefs.eveningMinute,
                title = "🪔 शुभ संध्या! संध्या आरती व दीपपूजन",
                message = "घरात दिवा लावा आणि अंबे मातेची दुर्गे दुर्घट भारी व विठ्ठलाची आरती सादर करा.",
                targetAartiId = 1
            )
        } else {
            cancelReminder(context, ID_EVENING_REMINDER)
        }
    }

    fun formatTime(hour: Int, minute: Int): String {
        val amPm = if (hour < 12) "AM" else "PM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        return String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minute, amPm)
    }
}
