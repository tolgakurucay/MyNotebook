package com.tolgakurucay.mynotebook.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.views.main.FeedFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


const val notificationID=1
const val channelID="channel1"
var titleExtra="Alarm"
var messageExtra="You can add an alarm for this time"
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
            titleExtra=p1!!.getStringExtra("title") as String
            messageExtra=p1.getStringExtra("message") as String

         /*   val i=Intent(p0,FeedFragment::class.java)
            p1.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent=PendingIntent.getActivity(p0,0,i,0)*/
/*

            val builder=NotificationCompat.Builder(p0!!, channelID)
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle(titleExtra)
                .setContentText(messageExtra)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build()

            val notificationManager=NotificationManagerCompat.from(p0)
            notificationManager.notify(123,builder)

*/
        val notification: Notification=NotificationCompat.Builder(p0!!, channelID)
            .setSmallIcon(R.drawable.alarm)
            .setContentTitle(titleExtra)
            .setContentText(messageExtra)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            //.setContentIntent(pendingIntent)
            .build()

        val manager=p0.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID,notification)




    }
}