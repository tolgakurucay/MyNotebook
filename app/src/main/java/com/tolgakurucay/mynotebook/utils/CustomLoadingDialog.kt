package com.tolgakurucay.mynotebook.utils


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.tolgakurucay.mynotebook.databinding.FragmentCustomLoadingDialogBinding
import javax.inject.Inject


class CustomLoadingDialog @Inject constructor(): DialogFragment() {

    private lateinit var binding:FragmentCustomLoadingDialogBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentCustomLoadingDialogBinding.inflate(inflater)
        if(dialog!=null && dialog!!.window!=null){
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }



        this.isCancelable=false
        return binding.root
    }







}