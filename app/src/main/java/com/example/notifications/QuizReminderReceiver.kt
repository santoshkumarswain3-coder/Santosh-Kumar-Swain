package com.example.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

class QuizReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        if (action == ACTION_SHOW_REMINDER) {
            val kind = QuizReminderKind.fromRaw(
                intent.getStringExtra(QuizNotificationScheduler.EXTRA_REMINDER_KIND)
            )
            if (kind != null) {
                showNotificationIfAllowed(context, kind)
                // Schedule next occurrence strictly in the future (+1s guard against clock jitter)
                QuizNotificationScheduler.scheduleReminder(
                    context = context,
                    kind = kind,
                    nowMillis = System.currentTimeMillis() + 1_000L
                )
            } else {
                QuizNotificationScheduler.ensureChannelAndScheduleAll(context)
            }
            return
        }

        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            Log.d(TAG, "Rescheduling 7:30 PM quiz reminders after $action")
            QuizNotificationScheduler.ensureChannelAndScheduleAll(context)
        }
    }

    private fun showNotificationIfAllowed(context: Context, kind: QuizReminderKind) {
        QuizNotificationScheduler.ensureChannel(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                Log.w(TAG, "Skipping ${kind.name} notification: POST_NOTIFICATIONS not granted.")
                return
            }
        }

        val notifManager = NotificationManagerCompat.from(context)
        if (!notifManager.areNotificationsEnabled()) {
            Log.w(TAG, "Skipping ${kind.name} notification: app notifications disabled.")
            return
        }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(QuizNotificationScheduler.EXTRA_REMINDER_KIND, kind.name)
        }
        val contentIntent = PendingIntent.getActivity(
            context,
            kind.notificationId,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, QuizNotificationScheduler.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_quiz)
            .setContentTitle(kind.title)
            .setContentText(kind.message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(kind.message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        try {
            notifManager.notify(kind.notificationId, notification)
        } catch (e: SecurityException) {
            Log.e(TAG, "Notification permission denied when posting ${kind.name}", e)
        }
    }

    companion object {
        private const val TAG = "QuizReminderReceiver"
        const val ACTION_SHOW_REMINDER = "com.example.notifications.ACTION_SHOW_REMINDER"
    }
}
