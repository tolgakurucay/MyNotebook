package com.tolgakurucay.mynotebook.views.main

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.FragmentProfileBinding
import com.tolgakurucay.mynotebook.utils.ChangeLanguage
import com.tolgakurucay.mynotebook.utils.CustomLoadingDialog
import com.tolgakurucay.mynotebook.utils.ResetMyPasswordPopup
import com.tolgakurucay.mynotebook.utils.Util
import com.tolgakurucay.mynotebook.utils.Util.showAlertDialog
import com.tolgakurucay.mynotebook.utils.Util.showAlertDialogWithFuncs
import com.tolgakurucay.mynotebook.viewmodels.main.ProfileFragmentViewModel
import com.tolgakurucay.mynotebook.views.login.LoginActivity
import com.tolgakurucay.mynotebook.views.payment.UpgradePackageActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject







@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding:FragmentProfileBinding
    private val viewModel:ProfileFragmentViewModel by viewModels()
    private lateinit var auth:FirebaseAuth
    private lateinit var storage:FirebaseStorage
    @Inject lateinit var loadingDialog: CustomLoadingDialog
    private var imageBitmap:Bitmap?=null
    private var imageBase64:String?=null




   val TAG="bilgi"




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        init()


        return binding.root
    }




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.getRightForCurrentUser()
        val signType=Util.getSignType(requireActivity())
        Log.d(TAG, "onViewCreated: "+signType)
        when(signType){
            "email" -> {viewModel.getFromMail(auth.currentUser!!)
                binding.resetMyPassword.visibility=View.VISIBLE}

            "phone" -> {binding.resetMyPassword.visibility=View.GONE
                          viewModel.getFromPhone(auth.currentUser!!)}

            "google" -> {binding.resetMyPassword.visibility=View.GONE
                        viewModel.getFromMail(auth.currentUser!!)}
            "facebook"->{
                binding.resetMyPassword.visibility=View.GONE
                viewModel.getFromMail(auth.currentUser!!)


            }
        }


         buttonClickListeners()
         observeFlowData()
    }

    private fun init(){




        binding= FragmentProfileBinding.inflate(layoutInflater)
        auth=FirebaseAuth.getInstance()
        storage= FirebaseStorage.getInstance()

        val signType=Util.getSignType(requireActivity())
        Log.d(TAG, "init: "+signType)

        when(signType){
            "email" ->  {binding.editTextMail.hint=getString(R.string.email)
                        binding.editTextName.hint=getString(R.string.name)
                         binding.editTextSurname.hint=getString(R.string.surname)}

            "phone" -> {binding.editTextMail.hint=getString(R.string.phonenumber)
                         binding.editTextName.hint=getString(R.string.name)
                        binding.editTextSurname.hint=getString(R.string.surname)}

            "google" -> {binding.editTextMail.hint=getString(R.string.email)
                binding.editTextName.hint=getString(R.string.name)
                binding.editTextSurname.hint=getString(R.string.surname)}
            "facebook"->{binding.editTextMail.hint=getString(R.string.email)
                binding.editTextName.hint=getString(R.string.name)
                binding.editTextSurname.hint=getString(R.string.surname)}

        }


    }




    private fun buttonClickListeners(){

        binding.resetMyPassword.setOnClickListener {
            val resetMyPasswordPopup=ResetMyPasswordPopup()
            resetMyPasswordPopup.show(requireFragmentManager(),null)
        }


        binding.whoAmI.setOnClickListener {
            showAlertDialog("Tolga Kuruçay",getString(R.string.mydescription),R.drawable.pencil_black,getString(R.string.okay))
        }

        binding.changeLanguage.setOnClickListener {
            val fragmentChangeLanguage=ChangeLanguage()
            fragmentChangeLanguage.show(requireFragmentManager(),null)


        }
        binding.upgradeMyPackage.setOnClickListener {
            val intent=Intent(requireActivity(), UpgradePackageActivity::class.java)
            startActivity(intent)
        }






        binding.buttonProfileSave.setOnClickListener {

            val name=binding.editTextName.text.toString()
            val surname=binding.editTextSurname.text.toString()

            val signType=Util.getSignType(requireActivity())
            if(signType=="email"){
                viewModel.setMail(name,surname,imageBase64,auth.currentUser!!)
            }
            else if(signType=="google"){
                viewModel.setMail(name,surname,imageBase64,auth.currentUser!!)
            }
            else if(signType=="phone"){
                viewModel.setPhone(name,surname,imageBase64,auth.currentUser!!)
            }
            else if(signType=="facebook"){
                viewModel.setMail(name,surname,imageBase64,auth.currentUser!!)
            }


        }
        /////////////////////////////////// for pp
        var sActivityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult(),object:ActivityResultCallback<ActivityResult>{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onActivityResult(result: ActivityResult?) {
                result?.let {

                    if(it.resultCode==Activity.RESULT_OK){
                        val data=result.data
                        data?.let {
                            val uri=it.data
                            uri?.let {
                                val imageUri=it
                                val bigBitmap=MediaStore.Images.Media.getBitmap(this@ProfileFragment.requireActivity().contentResolver,imageUri)
                                imageBitmap=Util.makeSmallerBitmap(bigBitmap,200)
                                binding.imageViewProfile.setImageBitmap(imageBitmap)
                                binding.imageViewProfile.background=null
                                imageBase64=Util.bitmapToBase64(imageBitmap)



                            }
                        }

                    }
                }


            }

        })
        ////////////////////////////
        //for background
        val launcher2=registerForActivityResult(ActivityResultContracts.StartActivityForResult()
        ) { result ->
            result?.let {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    data?.let {
                        val uri = data.data
                        uri?.let {
                            viewModel.saveBackgroundToStorage(it, requireActivity())

                        }
                    }
                }
            }
        }

        binding.imageViewProfile.setOnClickListener {
            val intent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            sActivityResultLauncher.launch(intent)
        }

        binding.changeBackground.setOnClickListener {
            val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            launcher2.launch(intent)
        }

        binding.orderHistory.setOnClickListener {
            val action=ProfileFragmentDirections.actionProfileFragmentToOrderHistoryFragment()
            Navigation.findNavController(binding.root).navigate(action)
        }
        binding.deleteAccount.setOnClickListener {
            showAlertDialogWithFuncs(getString(R.string.deleteaccount),getString(R.string.areyousureyouwanttodeleteaccount),R.drawable.delete_account_black,getString(R.string.delete),getString(R.string.cancel),{
                viewModel.deleteAccountInformations()
            },{})
        }







    }



   @RequiresApi(Build.VERSION_CODES.O)
   private fun observeFlowData(){

       lifecycleScope.launch {
           viewModel.deleteAccount.filter { it!=null }.collect{
               if(it=="deleted"){
                   Log.d(TAG, "observeFlowData: deleteddd")
                   Toast.makeText(requireContext(),getString(R.string.accountdeleted),Toast.LENGTH_LONG).show()
                   startActivity(Intent(requireActivity(),LoginActivity::class.java))
                   requireActivity().finish()
               }
               else if(it=="notdeleted"){
                   Log.d(TAG, "observeFlowData: hesap silinemedi")
               }

           }
       }


       lifecycleScope.launch {
           viewModel.userRight.collect{
               it?.let {
                   binding.textViewRemaining.text = it
               }
           }
       }

       lifecycle.coroutineScope.launch{
           viewModel.savebackgroundMessage.collect{
               if(it == "success"){
                   Toast.makeText(requireContext(), getString(R.string.background), Toast.LENGTH_SHORT).show()
                   Util.byteArray=null
               }
               else
               {
                   Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
               }
           }
       }

       lifecycle.coroutineScope.launch {
           viewModel.savedPhone.collect{
               it?.let {
                   if(it=="saved"){
                       Toast.makeText(this@ProfileFragment.requireContext(),getString(R.string.savedchanges),Toast.LENGTH_SHORT).show()
                   }
                   else
                   {
                       showAlertDialog(getString(R.string.error),it,
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

               if(it.photo!="null"){
                   val bitmap=Util.base64ToBitmap(it.photo)
                   imageBitmap=bitmap
                   imageBase64=Util.bitmapToBase64(imageBitmap)
                   binding.imageViewProfile.setImageBitmap(bitmap)
                   binding.imageViewProfile.background=null
               }
               else
               {
                //image yok birşey koyma

               }



           }

       }

       lifecycle.coroutineScope.launch {

           viewModel.mailFlow.collect{

               it?.let {

                   binding.editTextName.setText(it.name)
                   binding.editTextSurname.setText(it.surname)
                   binding.editTextMail.setText(it.mail)
                   if(it.photo!="null"){
                       val bitmap=Util.base64ToBitmap(it.photo)
                       imageBitmap=bitmap
                       imageBase64=Util.bitmapToBase64(imageBitmap)
                       binding.imageViewProfile.setImageBitmap(bitmap)
                       binding.imageViewProfile.background=null

                   }
                   else
                   {
                       //image yok birşey koyma
                       Log.d("bilgi","setimage alınamadı")

                   }
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
                   showAlertDialog(getString(R.string.error),it, com.google.android.material.R.drawable.mtrl_ic_error,getString(R.string.okay))
               }
           }


       }
       lifecycleScope.launch {
           var bool=false
           viewModel.loading.filter { it!=null }.collect{
               //burdaki sıkıntı birden fazla kez livedata geldiği için üst üste ekleme yapmaya çalışıyor ve crash yiyiyor
               if(it!!){


                   if(!bool){
                       loadingDialog.show(parentFragmentManager,"started")
                       bool=true
                   }


               }
               else
               {
                   loadingDialog.dismiss()
                          

               }


           }
       }

    }


}

