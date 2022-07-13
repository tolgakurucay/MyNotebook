package com.tolgakurucay.mynotebook.utils

import android.R
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.DisplayMetrics
import androidx.appcompat.app.AlertDialog
import java.io.ByteArrayOutputStream
import java.sql.Blob
import java.util.*


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

    fun bitmapToBase64(bitmap: Bitmap?) : String?{
        if(bitmap==null){
            return null
        }
        else
        {
            var byteArrayOutputStream= ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream)
            var byteArray=byteArrayOutputStream.toByteArray()
            var encoded=Base64.getEncoder().encodeToString(byteArray)
            return encoded
        }


    }

    fun base64ToBitmap(base64String:String?) : Bitmap?{
        if(base64String!=null){
            var decodedString=Base64.getDecoder().decode(base64String)
            var decodedByte=BitmapFactory.decodeByteArray(decodedString,0,decodedString.size)
            return decodedByte
        }
        else
        {
            return null
        }

    }

    fun makeSmallerBitmap(bitmap:Bitmap?,maximumSize:Int):Bitmap?{

        if(bitmap!=null){
            var width=bitmap.width
            var height=bitmap.height

            val bitmapRatio : Double=width.toDouble()/height.toDouble()
            if(bitmapRatio>1){
                //landscape
                width=maximumSize
                val scaledHeight=width/bitmapRatio
                height=scaledHeight.toInt()
            }
            else
            {
                //portrait
                height=maximumSize
                val scaledWidht=height*bitmapRatio
                width=scaledWidht.toInt()
            }

            return Bitmap.createScaledBitmap(bitmap,width,height,true)
        }
        else
        {
            return null
        }


    }

    fun setLocale(lang: String?,activity: Activity) {
        val myLocale = Locale(lang)
        val res: Resources = activity.resources
        val dm: DisplayMetrics = res.getDisplayMetrics()
        val conf: Configuration = res.getConfiguration()
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)

    }

     fun getLanguage(activity:Activity) : String{
        val sharedPreference=activity.getSharedPreferences("com.tolgakurucay.mynotebook",Context.MODE_PRIVATE)
        return sharedPreference.getString("language","en").toString()
    }

     fun saveLanguage(languageCode:String,activity: Activity){
        val sharedPreference=activity.getSharedPreferences("com.tolgakurucay.mynotebook",Context.MODE_PRIVATE)
        sharedPreference.edit().putString("language",languageCode).commit()

    }

    fun saveSignType(activity:Activity,signType:String){
        val sharedPreferences=activity.getSharedPreferences("com.tolgakurucay.mynotebook",Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("signType",signType).commit()
    }

    fun getSignType(activity:Activity): String{
        val sharedPreferences=activity.getSharedPreferences("com.tolgakurucay.mynotebook",Context.MODE_PRIVATE)
        return sharedPreferences.getString("signType","google").toString()
    }









}