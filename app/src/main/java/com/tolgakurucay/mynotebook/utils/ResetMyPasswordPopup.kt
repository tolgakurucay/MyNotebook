package com.tolgakurucay.mynotebook.utils

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.FragmentResetMyPasswordPopupBinding
import com.tolgakurucay.mynotebook.views.login.LoginActivity


class ResetMyPasswordPopup : DialogFragment() {

    private lateinit var binding:FragmentResetMyPasswordPopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=FragmentResetMyPasswordPopupBinding.inflate(layoutInflater)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if(dialog!=null && dialog!!.window!=null){
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }
        this.isCancelable=false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        textListeners()

        binding.resetPasswordClose.setOnClickListener {
            this.dismiss()
        }
        binding.buttonSave.setOnClickListener {
            validatePasswordsAndChangedPasswords()
        }


    }


    private fun textListeners(){
        binding.editTextPassword1.addTextChangedListener {
            if(binding.editTextPassword1.text.toString().isEmpty()){
                binding.textInputLayoutPassword1.helperText=getString(R.string.enteranpassword)
            }
            else if(binding.editTextPassword1.text.toString().length<8){
                binding.textInputLayoutPassword1.helperText=(getString(R.string.password8))
            }
            else if(binding.editTextPassword2.text.toString()!=binding.editTextPassword1.text.toString()){
                binding.textInputLayoutPassword1.helperText=(getString(R.string.passwordsarentmatch))
                binding.textInputLayoutPassqword2.helperText=getString(R.string.passwordsarentmatch)
            }
            else
            {
                binding.textInputLayoutPassword1.helperText=null
                binding.textInputLayoutPassqword2.helperText=null
            }
        }
        binding.editTextPassword2.addTextChangedListener {
            if(binding.editTextPassword1.text.toString().isEmpty()){
                binding.textInputLayoutPassqword2.helperText=getString(R.string.enteranpassword)
            }

            else if(binding.editTextPassword2.text.toString()!=binding.editTextPassword1.text.toString()){
                binding.textInputLayoutPassqword2.helperText=getString(R.string.passwordsarentmatch)
                binding.textInputLayoutPassword1.helperText=(getString(R.string.passwordsarentmatch))
            }
            else
            {
                binding.textInputLayoutPassqword2.helperText=null
                binding.textInputLayoutPassword1.helperText=null
            }
        }

    }

    private fun validatePasswordsAndChangedPasswords(){
        val passwordHelper=binding.textInputLayoutPassword1.helperText
        val passwordTryHelper=binding.textInputLayoutPassqword2.helperText
        if(passwordHelper==null && passwordTryHelper==null){
            val auth=FirebaseAuth.getInstance()
            auth.currentUser?.let {
                it.updatePassword(binding.editTextPassword1.text.toString())
                    .addOnSuccessListener {
                        Toast.makeText(this.requireContext(), getString(R.string.passwordchangedsuccesfully), Toast.LENGTH_SHORT).show()
                        this.dismiss()
                    }
                    .addOnFailureListener {
                        Util.alertDialog(this.requireContext(),getString(R.string.error),it.localizedMessage,R.drawable.error,getString(R.string.okay))
                        startActivity(Intent(this.requireActivity(),LoginActivity::class.java))
                        requireActivity().finish()
                    }
            }
        }
        else
        {
            Toast.makeText(requireContext(), getString(R.string.blankfields), Toast.LENGTH_SHORT).show()
        }
    }


}