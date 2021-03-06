package com.ardy.consumerfavapp.alarm

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.ardy.consumerfavapp.MainActivity
import com.ardy.consumerfavapp.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val EXTRA_TYPE = "type"
        const val EXTRA_MESSAGE = "message"
        const val ID_REPEAT = 101

        private const val ALARM_TIME = "09:00"
        private const val TIME_FORMAT = "HH:mm"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra(EXTRA_MESSAGE)
        showAlarmNotification(context, message)

    }

    private fun showAlarmNotification(context: Context, message: String?) {
        val channelId = "Channel_1"
        val channelName = "AlarmManager channel"
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val notificationManagerCompat =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, channelId)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle("Github App")
                .setContentText(message)
                .setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            builder.setChannelId(channelId)
            notificationManagerCompat.createNotificationChannel(channel)
        }
        val notification = builder.build()
        notificationManagerCompat.notify(ID_REPEAT, notification)
    }
    fun setRepeatingAlarm(context: Context, type: String, message: String) {

        if (isDateInvalid(ALARM_TIME,TIME_FORMAT)) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra(EXTRA_TYPE, type)

        val timeArray = ALARM_TIME.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEAT, intent, 0)
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY,pendingIntent)
        Toast.makeText(context, "Reminder Activated", Toast.LENGTH_SHORT).show()
    }

    private fun isDateInvalid(date: String, format: String): Boolean {
        return try {
            val df = SimpleDateFormat(format,Locale.getDefault())
            df.isLenient = false
            df.parse(date)
            false
        }catch (e: ParseException){
            true
        }

    }



    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val requestCode = ID_REPEAT
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)
        pendingIntent.cancel()

        alarmManager.cancel(pendingIntent)
        Toast.makeText(context, "Reminder Deactivated", Toast.LENGTH_SHORT).show()
    }


}