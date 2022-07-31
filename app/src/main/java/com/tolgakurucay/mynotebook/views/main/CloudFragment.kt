package com.tolgakurucay.mynotebook.views.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.adapters.CloudAdapter
import com.tolgakurucay.mynotebook.databinding.FragmentCloudBinding
import com.tolgakurucay.mynotebook.models.NoteModel
import com.tolgakurucay.mynotebook.utils.CustomLoadingDialog
import com.tolgakurucay.mynotebook.viewmodels.main.CloudFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CloudFragment : Fragment() {

    private lateinit var binding:FragmentCloudBinding
    private lateinit var viewmodel:CloudFragmentViewModel
    private lateinit var cloudAdapter:CloudAdapter
    @Inject
    lateinit var customLoadingDialog: CustomLoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        binding=FragmentCloudBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        viewmodel.getFavorites()
        observeLiveData()
    }

    private fun init(){
        viewmodel=ViewModelProvider(this)[CloudFragmentViewModel::class.java]
        cloudAdapter= CloudAdapter(arrayListOf())
        binding.cloudAdapter.adapter=cloudAdapter

    }

    private fun observeLiveData(){
        val notes=ArrayList<NoteModel>()
        viewmodel.notesLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                lifecycleScope.launch {
                    for(i in it){
                        val title=i.getString("title")
                        val description=i.getString("description")
                        val dateAsDouble=i.getDouble("date")
                        val imageBase64=i.get("imageBase64")
                        if(imageBase64==null){
                            val note=NoteModel(title.toString(),description.toString(),null,dateAsDouble!!.toLong())
                            notes.add(note)
                        }
                        else
                        {
                            val note=NoteModel(title.toString(),description.toString(),imageBase64.toString(),dateAsDouble!!.toLong())
                            notes.add(note)

                        }
                    }
                    Log.d("bakbakalim", "observeLiveData: $notes")
                    cloudAdapter.updateAdapter(notes)
                }




            }
        })

        viewmodel.loading.observe(viewLifecycleOwner, Observer {
         it?.let {
             if(it){
                 customLoadingDialog.show(parentFragmentManager,null)
             }
             else
             {
                 customLoadingDialog.dismiss()
             }
         }
        })

    }
}