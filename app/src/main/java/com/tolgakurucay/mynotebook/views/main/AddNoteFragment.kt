package com.tolgakurucay.mynotebook.views.main

import android.app.Activity
import android.content.Intent
import android.graphics.ColorFilter
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.red
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.FragmentAddNoteBinding
import com.tolgakurucay.mynotebook.models.NoteModel
import com.tolgakurucay.mynotebook.utils.CustomLoadingDialog
import com.tolgakurucay.mynotebook.utils.GetCurrentDate
import com.tolgakurucay.mynotebook.viewmodels.main.AddNoteFragmentViewModel
import kotlin.math.log


class AddNoteFragment : Fragment() {

    private lateinit var binding:FragmentAddNoteBinding
    private lateinit var viewModel:AddNoteFragmentViewModel
    private var customDialog=CustomLoadingDialog()
    private var imageUri:String?=null
    val TAG="bilgi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentAddNoteBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //customDialog.show(requireFragmentManager(),"giriş")


        init()
        textChangeListeners()
        observeLiveData()
       binding.imageViewUpload.setOnClickListener {

           val intent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
           sActivityResultLauncher.launch(intent)

       }

        binding.buttonSave.setOnClickListener {
            customDialog.show(requireFragmentManager(),"giriş")
            if(binding.titleLayout.helperText==null && binding.descriptionLayout.helperText==null){
                val currentDate=GetCurrentDate()
                val note=NoteModel(binding.titleInput.text.toString(),binding.descriptionInput.text.toString(),imageUri,currentDate.currentDateAsLong())
                viewModel.addNoteToLocal(note,requireContext())
                customDialog.dismiss()
            }
            else
            {
                Toast.makeText(this.requireContext(),getString(R.string.addtitleanddescription),Toast.LENGTH_LONG).show()
                customDialog.dismiss()
            }
        }




    }

    private fun init(){
        viewModel=ViewModelProvider(this)[AddNoteFragmentViewModel::class.java]

    }



    private fun textChangeListeners(){

        binding.titleInput.addTextChangedListener {
            Log.d(TAG, "textChangeListeners: "+binding.titleInput.text.toString())
            viewModel.verifyTitle(binding.titleInput.text.toString())
        }
        binding.descriptionInput.addTextChangedListener{
            viewModel.verifyDescription(binding.descriptionInput.text.toString())
            Log.d(TAG, "textChangeListeners: "+binding.descriptionInput.text.toString())
        }

    }

    var sActivityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult(),object:ActivityResultCallback<ActivityResult>{
        override fun onActivityResult(result: ActivityResult?) {
            result?.let {

                if(it.resultCode==Activity.RESULT_OK){
                    val data=result.data
                    data?.let {
                        val uri=it.data
                        uri?.let {
                            imageUri=it.toString()
                            binding.imageViewUpload.setImageURI(it)
                            binding.imageViewUpload.background=null
                        }
                    }

                }
            }


        }

    })



    private fun observeLiveData(){

        viewModel.titleMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d(TAG, "observeLiveData: "+it)
                if(it=="empty"){
                    binding.titleLayout.helperText=getString(R.string.enteratitle)
                }
                else if(it=="atleast3characters"){
                    binding.titleLayout.helperText=getString(R.string.atleast3character)
                }

            }
            if(it==null){
                binding.titleLayout.helperText=null
            }
        })

        viewModel.descriptionMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d(TAG, "observeLiveDatades: "+it)
                if(it=="empty"){
                    binding.descriptionLayout.helperText=getString(R.string.enteratitle)

                }

            }
            if(it==null){

                binding.descriptionLayout.helperText=null
            }
        })

        viewModel.addingMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it=="added"){
                    Toast.makeText(this.requireContext(),getString(R.string.addedNote),Toast.LENGTH_LONG).show()
                }
                else
                {
                    Toast.makeText(this.requireContext(),it,Toast.LENGTH_LONG).show()
                    Log.d(TAG, "observeLiveData: $it")
                }
            }
        })




    }



}