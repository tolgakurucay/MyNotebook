package com.tolgakurucay.mynotebook.views.main

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.tolgakurucay.mynotebook.DataBinderMapperImpl
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.FragmentSelectDateBinding
import com.tolgakurucay.mynotebook.models.NoteModel
import com.tolgakurucay.mynotebook.services.channelID
import com.tolgakurucay.mynotebook.services.messageExtra
import com.tolgakurucay.mynotebook.services.notificationID
import com.tolgakurucay.mynotebook.services.titleExtra
import com.tolgakurucay.mynotebook.utils.GetCurrentDate
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class SelectDateFragment @Inject constructor() : DialogFragment() {

    private var arrayList=ArrayList<NoteModel>()
    private lateinit var binding:FragmentSelectDateBinding
    val TAG="bilgi"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        arguments?.let { 
           arrayList= it.getSerializable("data") as ArrayList<NoteModel> 
        }
        binding= FragmentSelectDateBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        createNotificationChannel()
        binding.button2.setOnClickListener {
            showAlert()
        }
        //Log.d(TAG, "onViewCreated: $arrayList")
    }

    private fun scheduleNotification() {
        val intent= Intent(requireActivity().applicationContext,Notification::class.java)
        val title=arrayList[0].title
        val message=arrayList[0].description
        intent.putExtra (messageExtra,message)
        intent.putExtra(titleExtra,title)

        val pendingIntent=PendingIntent.getBroadcast(requireContext().applicationContext, notificationID,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager=requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time=getTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        this.dismiss()
       // showAlert(time,title,desc)
    }

    private fun showAlert() {
        AlertDialog.Builder(requireContext())
            .setIcon(R.drawable.alarm)
            .setTitle(getString(R.string.alarm))
            .setCancelable(false)
            .setMessage(getString(R.string.areyousureyouwanttocreatealarm))
            .setPositiveButton(getString(R.string.yes),object:DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                   scheduleNotification()
                }

            })
            .setNegativeButton(getString(R.string.no),object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {

                }

            })


            .create()
            .show()
    }

    private fun getTime() : Long {
        val minute=binding.timePicker.minute
        val hour=binding.timePicker.hour
        val day=binding.datePicker.dayOfMonth
        val month=binding.datePicker.month
        val year=binding.datePicker.year

        val calendar=Calendar.getInstance()
        calendar.set(year,month,day,hour,minute)
        return calendar.timeInMillis
    }

    private fun createNotificationChannel(){
        val name = "Notifation Channel"
        val desc="A Description of the Channel"
        val importance=NotificationManager.IMPORTANCE_DEFAULT
        val channel=NotificationChannel(channelID,name, importance)
        channel.description=desc
        val notificationManager=requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


}