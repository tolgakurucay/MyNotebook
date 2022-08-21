package com.tolgakurucay.mynotebook

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResultListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tolgakurucay.mynotebook.databinding.FragmentPolicyBinding
import javax.inject.Inject


class PolicyFragment @Inject constructor() : DialogFragment() {

    private lateinit var binding : FragmentPolicyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPolicyBinding.inflate(inflater)
        if(dialog!=null && dialog!!.window!=null){
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            this.isCancelable=false
        }
        return binding.root

    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.webViewPolicy.loadUrl("https://sites.google.com/view/appmynotebook/ana-sayfa")
        binding.webViewPolicy.setNetworkAvailable(true)
        binding.webViewPolicy.settings.javaScriptEnabled=true

        binding.textViewIntentPrivacyPolicy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW,"https://sites.google.com/view/appmynotebook/ana-sayfa".toUri()))
        }

        binding.buttonPolicy.setOnClickListener {
            val uid=FirebaseAuth.getInstance().currentUser!!.uid
            val firestore=FirebaseFirestore.getInstance()
            val data= hashMapOf<String,Boolean>()
            data["isPolicyAgreed"] = true
            firestore.collection("PrivacyPolicy").document(uid).set(data)
                .addOnSuccessListener {
                   this.dismiss()
                }
        }

    }

}