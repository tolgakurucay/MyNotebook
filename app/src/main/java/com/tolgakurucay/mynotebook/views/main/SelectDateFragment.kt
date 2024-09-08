package com.tolgakurucay.mynotebook.views.main

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import com.tolgakurucay.mynotebook.DataBinderMapperImpl
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.FragmentSelectDateBinding
import com.tolgakurucay.mynotebook.models.NoteModel
import com.tolgakurucay.mynotebook.services.*
import com.tolgakurucay.mynotebook.utils.GetCurrentDate
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class SelectDateFragment @Inject constructor() : DialogFragment() {

    private lateinit var noteModel: NoteModel
    private lateinit var binding: FragmentSelectDateBinding
    private lateinit var alarmManager: AlarmManager
    val TAG = "bilgi"

    override fun onCreate(savedInstanceState: Bundle?) {
        arguments?.let {
            noteModel = it.getSerializable("data") as NoteModel
        }


        binding = FragmentSelectDateBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        createNotificationChannel()
        binding.button2.setOnClickListener {
            showAlert()
        }

    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification() {
        Log.d(TAG, "scheduleNotification: ")
        lifecycleScope.launch {
            val intent = Intent(requireActivity().applicationContext, AlarmReceiver::class.java)
            val title = noteModel.title
            val message = noteModel.description
            Log.d(TAG, "scheduleNotification: $title ------ $message")
            intent.putExtra("message", message)
            intent.putExtra("title", title)

            val pendingIntent = PendingIntent.getBroadcast(
                requireContext().applicationContext,
                notificationID,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = requireActivity().getSystemService(ALARM_SERVICE) as AlarmManager
            val time = getTime()
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            )


            this@SelectDateFragment.dismiss()
        }


    }


    private fun showAlert() {
        AlertDialog.Builder(requireContext())
            .setIcon(R.drawable.alarm)
            .setTitle(getString(R.string.alarm))
            .setCancelable(false)
            .setMessage(getString(R.string.areyousureyouwanttocreatealarm))
            .setPositiveButton(getString(R.string.yes), object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    scheduleNotification()

                }

            })
            .setNegativeButton(getString(R.string.no), object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {

                }

            })


            .create()
            .show()
    }

    private fun getTime(): Long {
        val minute = binding.timePicker.minute
        val hour = binding.timePicker.hour
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Notifation Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager =
            requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


}