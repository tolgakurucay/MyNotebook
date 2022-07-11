package com.tolgakurucay.mynotebook.views.main

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.tolgakurucay.mynotebook.NoteAdapter
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.FragmentFeedBinding
import com.tolgakurucay.mynotebook.viewmodels.main.FeedFragmentViewModel
import com.tolgakurucay.mynotebook.views.login.LoginActivity

class FeedFragment : Fragment() {

    private lateinit var binding:FragmentFeedBinding
    private lateinit var viewModel:FeedFragmentViewModel
    private var noteAdapter= NoteAdapter(arrayListOf())
    var TAG="bilgi"
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentFeedBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        buttonClickListener()
        observeLiveData()

    }

    private fun observeLiveData(){
        viewModel.noteList.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                binding.textViewError.visibility=View.INVISIBLE
                if(it.isEmpty()){
                    Log.d(TAG, "observeLiveData: boş"+it.toString())

                }
                else
                {
                    Log.d(TAG, "observeLiveData: veri var"+it.toString())
                    noteAdapter.updateNoteList(it)
                }


            }
            else
            {
                binding.textViewError.visibility=View.VISIBLE
                Log.d(TAG, "observeLiveData: hata")

            }
            
           
            
        })

    }
    private fun init(){
        auth= FirebaseAuth.getInstance()
        binding.recyclerView.layoutManager=GridLayoutManager(this.requireContext(),2,GridLayoutManager.VERTICAL,false)
        binding.recyclerView.adapter=noteAdapter

        viewModel=ViewModelProvider(this).get(FeedFragmentViewModel::class.java)

        binding.bottomNavigationView.background=null
        binding.bottomNavigationView.menu.getItem(2).isEnabled=false
        viewModel.getAllNotes(this.requireContext())
    }
    private fun buttonClickListener(){
        binding.buttonAddNote.setOnClickListener {
            navigator("addNote")
        }
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.search-> Log.d(TAG, "buttonClickListener: search")
                R.id.profile->navigator("profile")
                R.id.signOut->signOut()
                R.id.favorites->navigator("favorites")

            }
            true
            

        }


    }
    private fun navigator(fragmentName:String){
       if(fragmentName == "profile"){
           val direction=FeedFragmentDirections.actionFeedFragmentToProfileFragment()
           Navigation.findNavController(this.requireView()).navigate(direction)

       }
        else if(fragmentName=="favorites"){

           val direction=FeedFragmentDirections.actionFeedFragmentToFavoritesFragment()
           Navigation.findNavController(this.requireView()).navigate(direction)
       }
        else if(fragmentName=="addNote"){
           val direction=FeedFragmentDirections.actionFeedFragmentToAddNoteFragment()
           Navigation.findNavController(this.requireView()).navigate(direction)
       }



    }

    private fun signOut(){

        AlertDialog.Builder(this.requireContext())
            .setTitle(R.string.exit)
            .setIcon(R.drawable.signout)
            .setMessage(R.string.areyousureyouwanttoexit)
            .setPositiveButton(R.string.yes,object:DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    auth.signOut()
                    Toast.makeText(requireContext(),getString(R.string.loggedout),Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireActivity(),LoginActivity::class.java))
                    requireActivity().finish()
                }

            })
            .setNegativeButton(R.string.no) { _, _ ->
                //nothing
            }
            .create()
            .show()



    }


}