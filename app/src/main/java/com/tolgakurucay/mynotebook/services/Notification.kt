package com.tolgakurucay.mynotebook.services

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.tolgakurucay.mynotebook.R


const val notificationID=1
const val channelID="channel1"
const val titleExtra="titleExtra"
const val messageExtra="messageExtra"
class Notification : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {

        p0?.let {
            val notification=NotificationCompat.Builder(it, channelID)
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle(p1?.getStringExtra(titleExtra))
                .setContentText(p1?.getStringExtra(messageExtra))
                .build()


            val manager=it.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(notificationID,notification)
        }


    }
}