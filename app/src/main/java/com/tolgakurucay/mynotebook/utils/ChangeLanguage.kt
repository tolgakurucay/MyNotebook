package com.tolgakurucay.mynotebook.utils

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.LocaleList
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.Navigation
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.FragmentChangeLanguageBinding
import com.tolgakurucay.mynotebook.views.main.ProfileFragmentDirections
import java.util.*


class ChangeLanguage : DialogFragment() {

private lateinit var binding:FragmentChangeLanguageBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentChangeLanguageBinding.inflate(layoutInflater)
        if(dialog!=null && dialog!!.window!=null){
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }
        this.isCancelable=false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.switchTurkish.setOnCheckedChangeListener { compoundButton, b ->
            binding.switchEnglish.isChecked = !binding.switchTurkish.isChecked
        }
        binding.switchEnglish.setOnCheckedChangeListener { compoundButton, b ->
            binding.switchTurkish.isChecked = binding.switchEnglish.isChecked != true
        }

        binding.button.setOnClickListener {
            if(binding.switchTurkish.isChecked){

              Util.setLocale("tr",this.requireActivity())
            }
            else if(binding.switchEnglish.isChecked){
                Util.setLocale("en",this.requireActivity())
            }
            this.dismiss()
            Toast.makeText(this.requireContext(), "", Toast.LENGTH_SHORT).show()
        }







    }


}