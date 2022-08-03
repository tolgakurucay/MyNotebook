package com.tolgakurucay.mynotebook.views.main

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.adapters.FavoritesAdapter
import com.tolgakurucay.mynotebook.adapters.NoteAdapter
import com.tolgakurucay.mynotebook.databinding.FragmentFavoritesBinding
import com.tolgakurucay.mynotebook.models.NoteFavoritesModel
import com.tolgakurucay.mynotebook.utils.GetCurrentDate
import com.tolgakurucay.mynotebook.utils.Util
import com.tolgakurucay.mynotebook.viewmodels.main.FavoritesFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FavoritesFragment : Fragment() {
    


    private lateinit var binding:FragmentFavoritesBinding
    private val viewModel:FavoritesFragmentViewModel by viewModels()
    @Inject lateinit var date:GetCurrentDate
    private var adapter=FavoritesAdapter(arrayListOf()){item,process,title,desc->

        if(process=="delete"){
            AlertDialog.Builder(requireContext())
                .setIcon(R.drawable.ic_baseline_delete_24)
                .setTitle(getString(R.string.deleteTitle))
                .setMessage(getString(R.string.youwantdelete))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.delete),object:DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                        viewModel.deleteFavorite(requireContext(),item)
                    }

                })
                .setNegativeButton(getString(R.string.cancel),object:DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                    }

                })
                .create()
                .show()
        }
        else if(process=="update")
        {
            //update item

            AlertDialog.Builder(requireContext())
                .setIcon(R.drawable.save)
                .setTitle(getString(R.string.update))
                .setMessage(getString(R.string.areyousureyouwanttosavechanges))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.update),object:DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        val updatedItem=item
                        updatedItem.title=title.toString()
                        updatedItem.description=desc.toString()
                        updatedItem.date=date.currentDateAsLong()

                        viewModel.updateFavorites(requireContext(),updatedItem)
                    }

                })
                .setNegativeButton(getString(R.string.cancel),object:DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                        Toast.makeText(requireContext(), getString(R.string.updatedcancelled), Toast.LENGTH_SHORT).show()
                    }

                })
                .create()
                .show()

        }
        else if(process=="share"){
            Log.d(TAG, "share clicked: ")
            viewModel.shareFavorites(item.title,item.description,requireActivity())
        }
        
        

        

    }

 
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
      
        binding.recyclerFavorites.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.recyclerFavorites.adapter=adapter

    }

    private fun observeLiveData(){
           viewModel.favoritesList.observe(viewLifecycleOwner, Observer { 
               it?.let {
                   if(!it.isEmpty()){
                       Log.d(TAG, "observeLiveData: ${it.size}")
                       adapter.updateList(it)
                   }
                   else
                   {
                       val action=FavoritesFragmentDirections.actionFavoritesFragmentToFeedFragment()
                       Navigation.findNavController(requireView()).navigate(action)
                       Toast.makeText(requireContext(), getString(R.string.thereisnofavorites), Toast.LENGTH_SHORT).show()
                   }

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

        viewModel.updated.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it){
                    Toast.makeText(requireContext(), getString(R.string.updatedsuccessfully), Toast.LENGTH_SHORT).show()
                    viewModel.getFavorites(requireContext())
                }

            }
        })





    }



}