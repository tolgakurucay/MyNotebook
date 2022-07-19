package com.tolgakurucay.mynotebook.views.main

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
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
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.FragmentNoteBinding
import com.tolgakurucay.mynotebook.models.NoteModel
import com.tolgakurucay.mynotebook.utils.GetCurrentDate
import com.tolgakurucay.mynotebook.viewmodels.main.NoteFragmentViewModel
import io.grpc.okhttp.internal.Util


class NoteFragment : Fragment() {

    private lateinit var binding:FragmentNoteBinding
    private var model:NoteModel?=null
    private val args by navArgs<NoteFragmentArgs>()
    private var imageBitmap: Bitmap?=null
    private val currDate=GetCurrentDate()
    private lateinit var viewModel:NoteFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=FragmentNoteBinding.inflate(layoutInflater)
         model=args.noteObject



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(this)[NoteFragmentViewModel::class.java]
        loadObject()
        clickListeners()
        textChangeListeners()
        observeLiveData()
    }

    private fun loadObject(){
        binding.titleInputUpdate.setText(model?.title)
        binding.descriptionInputUpdate.setText(model?.description)

        if(model?.imageBase64!=null && model?.imageBase64!="null"){
            imageBitmap= com.tolgakurucay.mynotebook.utils.Util.base64ToBitmap(model?.imageBase64)
            binding.imageViewUpdate.setImageBitmap(imageBitmap)
            binding.imageViewUpdate.background=null
        }


    }

    var sActivityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()

    ) { result ->
        result?.let {

            if (it.resultCode == Activity.RESULT_OK) {
                val data = result.data
                data?.let {
                    val uri = it.data
                    uri?.let {
                        val imageUri = it
                        val bitmap=MediaStore.Images.Media.getBitmap(this.activity!!.contentResolver,imageUri)
                        imageBitmap=com.tolgakurucay.mynotebook.utils.Util.makeSmallerBitmap(bitmap,200)
                        binding.imageViewUpdate.setImageBitmap(imageBitmap)


                    }
                }

            }
        }
    }

    private fun clickListeners(){
        binding.imageViewUpdate.setOnClickListener {
            val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)


            sActivityResultLauncher.launch(intent)

        }


        binding.buttonUpdate.setOnClickListener {

            saveObject()
        }

        binding.buttonDelete.setOnClickListener {

            viewModel.deleteModel(model,requireContext())
        }





    }


    private fun textChangeListeners(){
        binding.titleInputUpdate.addTextChangedListener {
            viewModel.validateTitle(binding.titleInputUpdate.text.toString())
        }
        binding.descriptionInputUpdate.addTextChangedListener {
            viewModel.validateDescription(binding.descriptionInputUpdate.text.toString())
        }
    }

    private fun saveObject(){

        if(binding.titleLayoutUpdate.helperText==null && binding.descriptionLayoutUpdate.helperText==null){
            model?.title=binding.titleInputUpdate.text.toString()
            model?.description=binding.descriptionInputUpdate.text.toString()
            model?.date=currDate.currentDateAsLong()
            model?.imageBase64=com.tolgakurucay.mynotebook.utils.Util.bitmapToBase64(imageBitmap)
            Log.d("bilgi","idddd  "+ model!!.id.toString())
            viewModel.updateModel(model,this.requireContext())
        }
        else
        {
            Toast.makeText(this.requireContext(), getString(R.string.blankfields), Toast.LENGTH_SHORT).show()
        }


    }


    private fun observeLiveData(){
        viewModel.updated.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it=="updated"){
                    Toast.makeText(requireContext(), getString(R.string.updatedsuccessfully), Toast.LENGTH_SHORT).show()
                    val action=NoteFragmentDirections.actionNoteFragmentToFeedFragment()
                    Navigation.findNavController(this.requireView()).navigate(action)
                }
                else
                {
                    com.tolgakurucay.mynotebook.utils.Util.alertDialog(this.requireContext(),getString(R.string.error),it,R.drawable.error,getString(R.string.okay))
                }
            }
        })


        viewModel.titleMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it=="enteratitle"){
                    binding.titleLayoutUpdate.helperText=getString(R.string.enteratitle)
                }
                else if(it=="atleast3"){
                    binding.titleLayoutUpdate.helperText=getString(R.string.atleast3character)
                }
                else if(it=="validated"){
                    binding.titleLayoutUpdate.helperText=null
                }
            }
        })


        viewModel.descriptionMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it=="enteradescription"){
                binding.descriptionLayoutUpdate.helperText=getString(R.string.enteradescription)
                }
                else if(it=="atleast3"){
                    binding.descriptionLayoutUpdate.helperText=getString(R.string.atleast3character)
                }
                else if(it=="validated"){
                    binding.descriptionLayoutUpdate.helperText=null
                }
            }
        })


        viewModel.deleted.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it=="deleted"){
                    Toast.makeText(requireContext(), getString(R.string.deletedsuccessfully), Toast.LENGTH_SHORT).show()
                    val action=NoteFragmentDirections.actionNoteFragmentToFeedFragment()
                    Navigation.findNavController(this.requireView()).navigate(action)
                }
                else
                {
                    com.tolgakurucay.mynotebook.utils.Util.alertDialog(requireContext(),getString(R.string.error),it,R.drawable.error,getString(R.string.okay))
                }
            }
        })


    }







}


