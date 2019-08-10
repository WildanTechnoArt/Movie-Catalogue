package com.wildan.moviecatalogue.service

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.activity.MainActivity
import java.util.*

class DailyAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val appName = context.getString(R.string.app_name)
        val message = context.getString(R.string.msg_daily_reminder)
        showAlarmNotification(context, appName, message, NOTIFICATION_ID)
    }

    private fun showAlarmNotification(context: Context, title: String, message: String, notifyId: Int) {
        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, notifyId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_movie_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(alarmRingtone)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "NOTIFICATION_CHANNEL_MOVIE",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            notificationManager.createNotificationChannel(channel)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel =
                NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_MOVIE", importance).apply {
                    enableLights(true)
                    lightColor = Color.RED
                    enableVibration(true)
                    vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                }

            builder.setChannelId(NOTIFICATION_CHANNEL_ID)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(notifyId, builder.build())
    }

    fun setRepeatingAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 7)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                getPendingIntent(context)
            )

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis, getPendingIntent(context)
            )
        }

        Toast.makeText(context, context.getString(R.string.msg_daily_notification), Toast.LENGTH_SHORT).show()
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(getPendingIntent(context))
        Toast.makeText(context, context.getString(R.string.msg_daily_notification_off), Toast.LENGTH_SHORT).show()
    }

    companion object {

        private const val NOTIFICATION_ID = 101

        const val NOTIFICATION_CHANNEL_ID = "10001"

        private fun getPendingIntent(context: Context): PendingIntent {
            val alarmIntent = Intent(context, DailyAlarmReceiver::class.java)

            return PendingIntent.getBroadcast(
                context, NOTIFICATION_ID, alarmIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        }
    }

}