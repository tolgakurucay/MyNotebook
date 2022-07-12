package com.tolgakurucay.mynotebook.views.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.FragmentProfileBinding
import com.tolgakurucay.mynotebook.utils.CustomLoadingDialog
import com.tolgakurucay.mynotebook.utils.SignType
import com.tolgakurucay.mynotebook.utils.Util
import com.tolgakurucay.mynotebook.viewmodels.main.ProfileFragmentViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {

    private lateinit var binding:FragmentProfileBinding
    private lateinit var viewModel:ProfileFragmentViewModel
    private lateinit var auth:FirebaseAuth
    private lateinit var loadingDialog: CustomLoadingDialog

   val TAG="bilgi"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.getFromMail(auth.currentUser!!)
            signType{
                if(it=="email"){
                    viewModel.getFromMail(auth.currentUser!!)


                }
                else if(it=="phone"){
                    binding.resetMyPassword.visibility=View.GONE
                    viewModel.getFromPhone(auth.currentUser!!)
                }
                else
                {
                    Log.d(TAG, "onViewCreated: ERROR")
                }
            }
         saveChanges()
         observeFlowData()
    }

    private fun init(){
        binding= FragmentProfileBinding.inflate(layoutInflater)
        loadingDialog= CustomLoadingDialog()
        viewModel= ViewModelProvider(this)[ProfileFragmentViewModel::class.java]
        auth=FirebaseAuth.getInstance()
        signType {
            if(it=="email"){
                binding.editTextMail.hint=getString(R.string.email)
                binding.editTextName.hint=getString(R.string.name)
                binding.editTextName.hint=getString(R.string.surname)
            }
            else if(it=="phone"){
                binding.editTextMail.hint=getString(R.string.phonenumber)
                binding.editTextName.hint=getString(R.string.name)
                binding.editTextName.hint=getString(R.string.surname)
            }
            else
            {
                this.activity?.finish()
            }
        }
    }


    private fun saveChanges(){

        binding.buttonProfileSave.setOnClickListener {

            val name=binding.editTextName.text.toString()
            val surname=binding.editTextSurname.text.toString()

            signType {
                if(it=="email"){
                    viewModel.setMail(name,surname,null,auth.currentUser!!)

                }
                else if(it=="phone"){
                    viewModel.setPhone(name,surname,null,auth.currentUser!!)
                }
                else
                {

                }
            }
        }


    }


    private fun signType(completion:(signType:String)->Unit){
        val signType=SignType.signType
        signType?.let {
            if(signType=="email"){
                Log.d(TAG, "signType: email")
                completion("email")

            }
            else if(signType=="phone")
            {
                Log.d(TAG, "signType: phone")
                completion("phone")
            }
            else
            {
                Log.d(TAG, "signType: error")
                completion("error")
            }
        }
    }

   private fun observeFlowData(){

       lifecycle.coroutineScope.launch {
           viewModel.savedPhone.collect{
               it?.let {
                   if(it=="saved"){
                       Toast.makeText(this@ProfileFragment.requireContext(),getString(R.string.savedchanges,Toast.LENGTH_SHORT),Toast.LENGTH_SHORT).show()
                   }
                   else
                   {
                       Util.alertDialog(this@ProfileFragment.requireContext(),getString(R.string.error),it,
                           com.google.android.material.R.drawable.mtrl_ic_error,getString(R.string.okay))
                   }
               }

           }
       }


       lifecycle.coroutineScope.launch{
           viewModel.phoneFlow.filter { it!=null }.collect{
               binding.editTextMail.setText(it!!.phoneNumber)
               binding.editTextName.setText(it!!.name)
               binding.editTextSurname.setText(it!!.surname)

           }

       }

       lifecycle.coroutineScope.launch {

           viewModel.mailFlow.collect{

               it?.let {
                   Log.d(TAG, "observeFlowData: "+it.toString())
                   binding.editTextName.setText(it.name)
                   binding.editTextSurname.setText(it.surname)
                   binding.editTextMail.setText(it.mail)
               }
           }

       }

       lifecycleScope.launch{

           viewModel.savedMail.collect{
               if(it=="saved"){
                   Toast.makeText(this@ProfileFragment.requireContext(),getString(R.string.savedchanges),Toast.LENGTH_SHORT).show()

               }
               else
               {
                   Util.alertDialog(this@ProfileFragment.requireContext(),getString(R.string.error),it, com.google.android.material.R.drawable.mtrl_ic_error,getString(R.string.okay))
               }
           }


       }
       lifecycleScope.launch {
           viewModel.loading.filter { it!=null }.collect{
               if(it!!){
                   loadingDialog.show(requireFragmentManager(),"started")
               }
               else
               {
                   loadingDialog.dismiss()
               }


           }
       }

    }


}

