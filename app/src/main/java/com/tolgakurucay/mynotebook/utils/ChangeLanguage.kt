package com.tolgakurucay.mynotebook.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.FragmentChangeLanguageBinding
import com.tolgakurucay.mynotebook.views.login.LoginActivity
import com.tolgakurucay.mynotebook.views.main.MainActivity
import com.tolgakurucay.mynotebook.views.main.ProfileFragmentDirections
import java.util.*


class ChangeLanguage : DialogFragment() {

private lateinit var binding:FragmentChangeLanguageBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentChangeLanguageBinding.inflate(layoutInflater)
        if(dialog!=null && dialog!!.window!=null){
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val lang=Util.getLanguage(requireActivity())


        if(lang=="tr"){
            binding.switchTurkish.isChecked=true
            binding.switchEnglish.isChecked=false
        }
        else if(lang=="en"){
            binding.switchTurkish.isChecked=false
            binding.switchEnglish.isChecked=true
        }




        binding.switchTurkish.setOnCheckedChangeListener { compoundButton, b ->
            binding.switchEnglish.isChecked = !binding.switchTurkish.isChecked
        }
        binding.switchEnglish.setOnCheckedChangeListener { compoundButton, b ->
            binding.switchTurkish.isChecked = binding.switchEnglish.isChecked != true
        }

        binding.button.setOnClickListener {
            if(binding.switchTurkish.isChecked){
              Util.setLocale("tr",this.requireActivity())
                Toast.makeText(this.requireContext(), getText(R.string.languageturkish), Toast.LENGTH_SHORT).show()
                Util.saveLanguage("tr",this.requireActivity())
            }
            else if(binding.switchEnglish.isChecked){
                Util.setLocale("en",this.requireActivity())
                Toast.makeText(this.requireContext(), getText(R.string.languageenglish), Toast.LENGTH_SHORT).show()
                Util.saveLanguage("en",this.requireActivity())
            }

            this.dismiss()
            startActivity(Intent(this.requireActivity(),MainActivity::class.java))




        }







    }




}