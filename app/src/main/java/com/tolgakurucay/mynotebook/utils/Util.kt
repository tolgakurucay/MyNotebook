package com.tolgakurucay.mynotebook.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.tolgakurucay.mynotebook.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


object Util {



    fun Fragment.showAlertDialog(title:String,message:String,iconId:Int,buttonName:String){
        AlertDialog.Builder(this.requireContext())
            .setTitle(title)
            .setMessage(message)
            .setIcon(iconId)
            .setPositiveButton(buttonName,object:DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                }

            })
            .setCancelable(false)
            .create()
            .show()
    }

    fun Activity.showAlertDialog(title:String,message:String,iconId:Int,buttonName:String,buttonFunc:()->Unit ){
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setIcon(iconId)
            .setPositiveButton(buttonName,object:DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    buttonFunc()
                }

            })
            .setCancelable(false)
            .create()
            .show()
    }

    fun Fragment.showAlertDialogWithFuncs(title:String,message:String,iconID:Int,buttonPositive:String,buttonNegative:String,positiveFun:()->Unit,negativeFun:()->Unit){
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setIcon(iconID)
            .setPositiveButton(buttonPositive,object:DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    positiveFun()
                }

            })
            .setNegativeButton(buttonNegative,object:DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    negativeFun()
                }

            })
            .setCancelable(false)
            .create()
            .show()

    }


    fun Fragment.showAlertDialogWithOneButtonFunc(title:String,message:String,iconID:Int,buttonName:String,buttonFunc:()->Unit){
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setIcon(iconID)
            .setPositiveButton(buttonName
            ) { p0, p1 -> buttonFunc() }
            .setCancelable(false)
            .create()
            .show()
    }


    fun placeHolderProgressBar(context: Context): CircularProgressDrawable {
        return CircularProgressDrawable(context).apply {
            this.strokeWidth = 10f
            this.arrowEnabled = true
            this.centerRadius = 45f
            this.arrowScale =60f
            this.backgroundColor= android.R.color.holo_orange_dark
            start()
        }
    }


    fun ImageView.downloadFromUri(uri: Uri?){
        Glide.with(context)
            .load(uri)
            .placeholder(placeHolderProgressBar(context))
            .into(this)
    }

    fun ImageView.downloadFromString(imageUrlString:String,placeHolder:CircularProgressDrawable){
        val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry {
                add((SvgDecoder(this@downloadFromString.context)))
            }.build()

        val request = ImageRequest.Builder(this.context)
            .crossfade(true)
            .crossfade(500)
            .placeholder(placeHolder)
            .error(com.google.android.material.R.drawable.mtrl_ic_error)
            .data(imageUrlString)
            .target(this)
            .build()

        imageLoader.enqueue(request)

    }



    @JvmStatic
    @BindingAdapter("android:downloadImage")
    fun downloadImageForBinding(imageView:ImageView,uri: Uri?){
        imageView.downloadFromUri(uri)
    }


    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
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

    fun urlStringToBitmap(uri:String,context: Context,completion:(returnedBitmap:Bitmap?)->Unit){
        Glide.with(context)
            .asBitmap()
            .load(uri)
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    completion(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                   completion(null)
                }


            })
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

    var byteArray:ByteArray?=null









}