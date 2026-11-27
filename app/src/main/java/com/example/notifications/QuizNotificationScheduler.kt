package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar
import java.util.TimeZone

enum class QuizReminderKind(
    val requestCode: Int,
    val notificationId: Int,
    val title: String,
    val message: String
) {
    DAILY(
        requestCode = 1001,
        notificationId = 2001,
        title = "Daily GK Test",
        message = "Your daily test is ready"
    ),
    WEEKLY(
        requestCode = 1002,
        notificationId = 2002,
        title = "Weekly Challenge",
        message = "Your weekly challenge is ready"
    ),
    MONTHLY(
        requestCode = 1003,
        notificationId = 2003,
        title = "Monthly Grand Test",
        message = "Your monthly grand test is ready"
    );

    companion object {
        fun fromRaw(raw: String?): QuizReminderKind? =
            entries.firstOrNull { it.name == raw }
    }
}

object QuizNotificationScheduler {

    private const val TAG = "QuizNotifScheduler"
    const val CHANNEL_ID = "brightquiz_test_reminders"
    private const val CHANNEL_NAME = "Quiz Schedule Reminders"
    private const val CHANNEL_DESC = "Daily, Sunday Weekly, and Month-End Grand Test alerts at 7:30 PM"
    const val EXTRA_REMINDER_KIND = "extra_reminder_kind"

    private const val REMINDER_HOUR = 19
    private const val REMINDER_MINUTE = 30

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return
            val existing = manager.getNotificationChannel(CHANNEL_ID)
            if (existing == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = CHANNEL_DESC
                }
                manager.createNotificationChannel(channel)
            }
        }
    }

    fun ensureChannelAndScheduleAll(context: Context) {
        ensureChannel(context)
        val now = System.currentTimeMillis()
        QuizReminderKind.entries.forEach { kind ->
            scheduleReminder(context, kind, now)
        }
    }

    fun scheduleReminder(
        context: Context,
        kind: QuizReminderKind,
        nowMillis: Long = System.currentTimeMillis(),
        timeZone: TimeZone = TimeZone.getDefault()
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val triggerAtMillis = computeNextTriggerMillis(kind, nowMillis, timeZone)
        val intent = Intent(context, QuizReminderReceiver::class.java).apply {
            action = QuizReminderReceiver.ACTION_SHOW_REMINDER
            putExtra(EXTRA_REMINDER_KIND, kind.name)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            kind.requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
            Log.d(TAG, "Scheduled ${kind.name} reminder for $triggerAtMillis")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule ${kind.name} reminder", e)
        }
    }

    fun computeNextTriggerMillis(
        kind: QuizReminderKind,
        nowMillis: Long,
        timeZone: TimeZone = TimeZone.getDefault()
    ): Long = when (kind) {
        QuizReminderKind.DAILY -> computeNextDailyTriggerMillis(nowMillis, timeZone)
        QuizReminderKind.WEEKLY -> computeNextWeeklySundayTriggerMillis(nowMillis, timeZone)
        QuizReminderKind.MONTHLY -> computeNextMonthEndTriggerMillis(nowMillis, timeZone)
    }

    internal fun computeNextDailyTriggerMillis(
        nowMillis: Long,
        timeZone: TimeZone = TimeZone.getDefault()
    ): Long {
        val cal = Calendar.getInstance(timeZone).apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, REMINDER_HOUR)
            set(Calendar.MINUTE, REMINDER_MINUTE)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (cal.timeInMillis <= nowMillis) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }

    internal fun computeNextWeeklySundayTriggerMillis(
        nowMillis: Long,
        timeZone: TimeZone = TimeZone.getDefault()
    ): Long {
        val cal = Calendar.getInstance(timeZone).apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, REMINDER_HOUR)
            set(Calendar.MINUTE, REMINDER_MINUTE)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY || cal.timeInMillis <= nowMillis) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }

    internal fun computeNextMonthEndTriggerMillis(
        nowMillis: Long,
        timeZone: TimeZone = TimeZone.getDefault()
    ): Long {
        val cal = Calendar.getInstance(timeZone).apply {
            timeInMillis = nowMillis
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, REMINDER_HOUR)
            set(Calendar.MINUTE, REMINDER_MINUTE)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (cal.timeInMillis <= nowMillis) {
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.add(Calendar.MONTH, 1)
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
            cal.set(Calendar.HOUR_OF_DAY, REMINDER_HOUR)
            cal.set(Calendar.MINUTE, REMINDER_MINUTE)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}
