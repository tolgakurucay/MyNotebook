package com.tolgakurucay.mynotebook.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import android.view.View

object Util {

    fun alertDialog(context: Context,title:String,message:String,iconId:Int,buttonName:String){
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setIcon(iconId)
            .setPositiveButton(buttonName,object:DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {

                }

            })
            .create()
            .show()
    }


}