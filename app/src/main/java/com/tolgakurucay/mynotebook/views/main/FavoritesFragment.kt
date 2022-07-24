package com.tolgakurucay.mynotebook.views.main

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.adapters.FavoritesAdapter
import com.tolgakurucay.mynotebook.adapters.NoteAdapter
import com.tolgakurucay.mynotebook.databinding.FragmentFavoritesBinding
import com.tolgakurucay.mynotebook.models.NoteFavoritesModel
import com.tolgakurucay.mynotebook.viewmodels.main.FavoritesFragmentViewModel


class FavoritesFragment : Fragment() {
    


    private lateinit var binding:FragmentFavoritesBinding
    private lateinit var viewModel:FavoritesFragmentViewModel
    private var adapter=FavoritesAdapter(arrayListOf()){
        
        AlertDialog.Builder(requireContext())
            .setIcon(R.drawable.ic_baseline_delete_24)
            .setTitle(getString(R.string.deleteTitle))
            .setMessage(getString(R.string.youwantdelete))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.delete),object:DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    Log.d(TAG, "onClick: yalandan silindi")
                    viewModel.deleteFavorite(requireContext(),it)
                }

            })
            .setNegativeButton(getString(R.string.cancel),object:DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    Log.d(TAG, "onClick: silinmedi")
                }

            })
            .create()
            .show()
        

    }

 
    val TAG="bilgi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=FragmentFavoritesBinding.inflate(layoutInflater)
        viewModel=ViewModelProvider(this)[FavoritesFragmentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            viewModel.getFavorites(requireContext())

            observeLiveData()

    }


    private fun init(){
      
        binding.recyclerFavorites.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        binding.recyclerFavorites.adapter=adapter
    }

    private fun observeLiveData(){
           viewModel.favoritesList.observe(viewLifecycleOwner, Observer { 
               it?.let {
                   Log.d(TAG, "observeLiveData: ${it.size}")
                   adapter.updateList(it)
               }
           })


        viewModel.deleted.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it){
                    Toast.makeText(requireContext(), getString(R.string.deletedsuccessfully), Toast.LENGTH_SHORT).show()
                    viewModel.getFavorites(requireContext())
                }

            }
        })


    }


}