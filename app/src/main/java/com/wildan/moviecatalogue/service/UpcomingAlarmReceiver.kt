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
import com.wildan.moviecatalogue.model.movie.MovieResult
import com.wildan.moviecatalogue.network.ConnectivityStatus
import com.wildan.moviecatalogue.network.NetworkError
import com.wildan.moviecatalogue.presenter.SettingsPresenter
import com.wildan.moviecatalogue.view.SettingsView
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UpcomingAlarmReceiver : BroadcastReceiver(), SettingsView.View {

    private val movieList = arrayListOf<MovieResult>()

    override fun onReceive(context: Context, intent: Intent) {
        val appName = context.getString(R.string.app_name)
        val presenter: SettingsView.Presenter = SettingsPresenter(this)
        presenter.setRepeatingAlarm(context, appName)
    }

    override fun setAlarm(movie: ArrayList<MovieResult>) {
        movieList.clear()
        movieList.addAll(movie)
    }

    override fun onSuccess(context: Context, appName: String?) {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = Date()
        val currentDate = dateFormat.format(date)

        var notifyId = 0

        if(movieList.size > 0){
            val thread = object : Thread() {
                override fun run() {
                    try {
                        for (movie in movieList) {
                            if (movie.releaseDate.equals(currentDate)) {
                                showAlarmNotification(context, appName, notifyId, movie.title.toString())
                                notifyId += 1
                            }
                            sleep(2000)
                        }
                    } catch (ex: InterruptedException) {
                        ex.printStackTrace()
                    }
                }
            }
            thread.start()
        }
    }

    override fun handleError(e: Throwable, context: Context) {
        if (ConnectivityStatus.isConnected(context)) {
            when (e) {
                is HttpException -> // non 200 error codes
                    NetworkError.handleError(e, context)
                is SocketTimeoutException -> // connection errors
                    Toast.makeText(context, context.resources.getString(R.string.timeout), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, context.resources.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAlarmNotification(context: Context, appName: String?, notifyId: Int, movieTitle: String?) {
        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val alarmRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, notifyId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_movie_notification)
            .setContentTitle(appName)
            .setContentText(String.format(context.getString(R.string.upcoming_reminder_msg), movieTitle))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(alarmRingtone)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "NOTIFICATION_CHANNEL_UPCOMING",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            notificationManager.createNotificationChannel(channel)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel =
                NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_UPCOMING", importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)

            builder.setChannelId(NOTIFICATION_CHANNEL_ID)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(notifyId, builder.build())
    }

    fun setRepeatingAlarm(context: Context) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 18)
        calendar.set(Calendar.MINUTE, 28)
        calendar.set(Calendar.SECOND, 0)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                getReminderPendingIntent(context)
            )

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis, getReminderPendingIntent(context)
            )
        }

        Toast.makeText(context, context.getString(R.string.msg_upcoming_actived), Toast.LENGTH_SHORT).show()
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(getReminderPendingIntent(context))
        Toast.makeText(context, context.getString(R.string.msg_upcoming_dissable), Toast.LENGTH_SHORT).show()
    }

    companion object {

        const val NOTIFICATION_CHANNEL_ID = "10001"

        private fun getReminderPendingIntent(context: Context): PendingIntent {
            val alarmIntent = Intent(context, UpcomingAlarmReceiver::class.java)

            return PendingIntent.getBroadcast(
                context, 101, alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

}