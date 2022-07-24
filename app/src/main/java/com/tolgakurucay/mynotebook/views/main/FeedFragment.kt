package com.tolgakurucay.mynotebook.views.main

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.tolgakurucay.mynotebook.NoteClickListener
import com.tolgakurucay.mynotebook.adapters.NoteAdapter
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.FragmentFeedBinding
import com.tolgakurucay.mynotebook.models.NoteModel
import com.tolgakurucay.mynotebook.viewmodels.main.FeedFragmentViewModel
import com.tolgakurucay.mynotebook.views.login.LoginActivity

class FeedFragment : Fragment() { 

    private lateinit var binding:FragmentFeedBinding
    private var menuTop:Menu?=null
    private var noteModels=ArrayList<NoteModel>()
    private lateinit var viewModel:FeedFragmentViewModel
    private var noteAdapter= NoteAdapter(arrayListOf()){
        if(it.size==0){//menüyü gizle
            Log.d(TAG, "menüyü gizle: ")
            setHasOptionsMenu(false)
            noteModels=it

        }
        else//menüyü göster
        {
            Log.d(TAG, "menüyü göster: ")
            setHasOptionsMenu(true)
            noteModels=it


        }
    }
    

    var TAG="bilgi"
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions()
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


                }
                else
                {

                    noteAdapter.updateNoteList(it)
                }


            }
            else
            {
                binding.textViewError.visibility=View.VISIBLE


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


    private fun requestPermissions(){
        if(ContextCompat.checkSelfPermission(this.requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            Snackbar.make(this.requireView(),"Permission Needed",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",object: View.OnClickListener{
                override fun onClick(p0: View?) {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this@FeedFragment.requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                        ActivityCompat.requestPermissions(this@FeedFragment.requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
                    }
                    else
                    {
                        val intent=Intent()
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri= Uri.fromParts("package",this@FeedFragment.requireActivity().packageName,null)
                        intent.setData(uri)
                        startActivity(intent)
                    }

                }

            }).show()
        }

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_bar_menu,menu)
        menuTop=menu


        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.deleteItems->  {viewModel.deleteNotes(requireContext(),noteModels)
                viewModel.getAllNotes(requireContext())
                noteModels.clear()
                setHasOptionsMenu(false)

            }
            R.id.favoriteItems-> Log.d(TAG,"favorites clicked")
        }
        return true
    }

}





