package com.tolgakurucay.mynotebook.views.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.adapters.FavoritesAdapter
import com.tolgakurucay.mynotebook.adapters.NoteAdapter
import com.tolgakurucay.mynotebook.databinding.FragmentFavoritesBinding
import com.tolgakurucay.mynotebook.viewmodels.main.FavoritesFragmentViewModel


class FavoritesFragment : Fragment() {


    private lateinit var binding:FragmentFavoritesBinding
    private lateinit var viewModel:FavoritesFragmentViewModel
    private var adapter=FavoritesAdapter(arrayListOf())

 
    val TAG="bilgi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=FragmentFavoritesBinding.inflate(layoutInflater)
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
       viewModel=ViewModelProvider(this)[FavoritesFragmentViewModel::class.java]
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
    }


}