package com.tolgakurucay.mynotebook.views.main

import android.graphics.ColorFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.red
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.FragmentAddNoteBinding
import com.tolgakurucay.mynotebook.viewmodels.main.AddNoteFragmentViewModel
import kotlin.math.log


class AddNoteFragment : Fragment() {

    private lateinit var binding:FragmentAddNoteBinding
    private lateinit var viewModel:AddNoteFragmentViewModel
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
        init()
        textChangeListeners()
        observeLiveData()


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




    }



}