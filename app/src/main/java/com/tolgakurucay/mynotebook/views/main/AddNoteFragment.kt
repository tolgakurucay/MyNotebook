package com.tolgakurucay.mynotebook.views.main

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.FragmentAddNoteBinding
import com.tolgakurucay.mynotebook.models.NoteModel
import com.tolgakurucay.mynotebook.utils.CustomLoadingDialog
import com.tolgakurucay.mynotebook.utils.GetCurrentDate
import com.tolgakurucay.mynotebook.utils.Util
import com.tolgakurucay.mynotebook.viewmodels.main.AddNoteFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.log

@AndroidEntryPoint
class AddNoteFragment : Fragment() {

    private lateinit var binding:FragmentAddNoteBinding
    private val viewModel:AddNoteFragmentViewModel by viewModels()
    @Inject lateinit var customDialog:CustomLoadingDialog
    private var imageUri:Uri?=null
    val TAG="bilgi"
    @Inject lateinit var currentDate:GetCurrentDate



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentAddNoteBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        textChangeListeners()
        observeLiveData()
       binding.imageViewUpload.setOnClickListener {

           val intent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
           sActivityResultLauncher.launch(intent)

       }

        binding.buttonSave.setOnClickListener {
            var scaled:Bitmap?=null
            customDialog.show(requireFragmentManager(),null)
            if(binding.titleLayout.helperText==null && binding.descriptionLayout.helperText==null){

                if(imageUri!=null){
                    scaled=Util.makeSmallerBitmap(MediaStore.Images.Media.getBitmap(this.activity!!.contentResolver,imageUri),200)

                }



                val note=NoteModel(binding.titleInput.text.toString().replaceFirstChar { it.uppercase() },binding.descriptionInput.text.toString(),Util.bitmapToBase64(scaled),currentDate.currentDateAsLong())
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
                            imageUri=it
                            binding.imageViewUpload.background=null
                            binding.imageViewUpload.setImageURI(it)
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
                    val action=AddNoteFragmentDirections.actionAddNoteFragmentToFeedFragment()
                    Navigation.findNavController(this.requireView()).navigate(action)
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