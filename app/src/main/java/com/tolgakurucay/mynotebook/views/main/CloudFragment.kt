package com.tolgakurucay.mynotebook.views.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
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
    private val viewmodel:CloudFragmentViewModel by viewModels()
    private lateinit var cloudAdapter:CloudAdapter
    @Inject lateinit var customLoadingDialog: CustomLoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        binding=FragmentCloudBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        viewmodel.getFavorites()
        observeLiveData()
    }

    private fun init(){
        cloudAdapter= CloudAdapter()
        binding.cloudAdapter.adapter=cloudAdapter

    }

    private fun observeLiveData(){
        val notes=ArrayList<NoteModel>()
        viewmodel.notesLiveData.observe(viewLifecycleOwner, Observer {querySnapshot->
            if(querySnapshot!=null && !querySnapshot.isEmpty){
                lifecycleScope.launch {
                    for(i in querySnapshot){
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

                   // cloudAdapter.updateAdapter(notes)
                    cloudAdapter.noteList=notes
                }

            }
            else{
                val navigation=CloudFragmentDirections.actionCloudFragmentToFeedFragment()
                Navigation.findNavController(requireView()).navigate(navigation)
                Toast.makeText(requireContext(), getString(R.string.noclouditemtoshow), Toast.LENGTH_SHORT).show()
                

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