package com.tolgakurucay.mynotebook.views.main

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.tolgakurucay.mynotebook.adapters.NoteAdapter
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.FragmentFeedBinding
import com.tolgakurucay.mynotebook.models.NoteFavoritesModel
import com.tolgakurucay.mynotebook.models.NoteModel
import com.tolgakurucay.mynotebook.utils.CustomLoadingDialog
import com.tolgakurucay.mynotebook.utils.Util
import com.tolgakurucay.mynotebook.viewmodels.main.FeedFragmentViewModel
import com.tolgakurucay.mynotebook.views.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() { 

    private lateinit var binding:FragmentFeedBinding
    private var menuTop:Menu?=null
    private var tempNoteModels=ArrayList<NoteModel>()
    private lateinit var viewModel:FeedFragmentViewModel
    private var favoritesList=ArrayList<NoteFavoritesModel>()
    @Inject
     lateinit var selectDateFragment:SelectDateFragment
     @Inject
     lateinit var loadingDialog: CustomLoadingDialog


    private var noteAdapter= NoteAdapter(arrayListOf()){

        if(it.size==0){//menüyü gizle
            setHasOptionsMenu(false)
            tempNoteModels=it
            menuTop?.findItem(R.id.shareItem)?.isVisible=false
            menuTop?.findItem(R.id.alarmItem)?.isVisible=false
        }
        else if(it.size==1){
            setHasOptionsMenu(true)
            tempNoteModels=it
            menuTop?.findItem(R.id.shareItem)?.isVisible=true
            menuTop?.findItem(R.id.alarmItem)?.isVisible=true
        }
        else//menüyü göster
        {
            setHasOptionsMenu(true)
            tempNoteModels=it
            menuTop?.findItem(R.id.shareItem)?.isVisible=false
            menuTop?.findItem(R.id.alarmItem)?.isVisible=false
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
        viewModel.getPPfromStorage()
        buttonClickListener()
        observeLiveData()

    }

    private fun observeLiveData(){


        viewModel.loading.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it){
                    loadingDialog.show(parentFragmentManager,null)
                }
                else
                {
                    loadingDialog.dismiss()
                }
            }
        })

        viewModel.firebaseMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it.equals("success")){

                    tempNoteModels.clear()
                    noteAdapter.viewIdListSetFalse()
                    noteAdapter.modelArrayListClear()
                    setHasOptionsMenu(false)
                    viewModel.getAllNotes(requireContext())
                    Toast.makeText(requireContext(), getString(R.string.noteshassavedtofirebase), Toast.LENGTH_LONG).show()
                }
                else if(it.equals("noright")){
                    Toast.makeText(requireContext(), getString(R.string.youhavenoright), Toast.LENGTH_LONG).show()
                    tempNoteModels.clear()
                    noteAdapter.viewIdListSetFalse()
                    noteAdapter.modelArrayListClear()

                    setHasOptionsMenu(false)
                    viewModel.getAllNotes(requireContext())
                }
                else
                {
                    Util.alertDialog(requireContext(),getString(R.string.error),it,R.drawable.error,getString(R.string.okay))
                    tempNoteModels.clear()
                    noteAdapter.viewIdListSetFalse()
                    noteAdapter.modelArrayListClear()
                    setHasOptionsMenu(false)
                    viewModel.getAllNotes(requireContext())
                }
            }
        })


        viewModel.noteList.observe(viewLifecycleOwner, Observer {

            if(it!=null){
                binding.textViewError.visibility=View.INVISIBLE
                if(it.isEmpty()){
                    noteAdapter.updateNoteList(it)
                    for(i in it){
                        favoritesList.add(NoteFavoritesModel(i.title,i.description,i.imageBase64,i.date))
                    }
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
        
        viewModel.uriLiveData.observe(viewLifecycleOwner, Observer { 
            it?.let {
                Log.d(TAG, "observeLiveData: $it")
                Glide.with(this@FeedFragment).asBitmap().load(it).into(object: SimpleTarget<Bitmap>(){
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val drawable=BitmapDrawable(resource)
                        binding.constraintFeed.setBackgroundDrawable(drawable)
                    }

                })
            }
        })


    }
    private fun init(){

        auth= FirebaseAuth.getInstance()



        

        setHasOptionsMenu(false)
        noteAdapter.modelArrayListEx.clear()
        for(i in 0 until noteAdapter.viewIdList.size){
            noteAdapter.viewIdList.put(i,false)
        }

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
                R.id.cloud-> {navigator("cloud")}
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
        else if(fragmentName=="cloud"){
            val direction=FeedFragmentDirections.actionFeedFragmentToCloudFragment()
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
            R.id.deleteItems->  {
               AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.delete))
                    .setMessage(getString(R.string.youwantdelete))
                    .setIcon(R.drawable.ic_baseline_delete_24)
                    .setPositiveButton(getString(R.string.delete),object:DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            viewModel.deleteNotes(requireContext(),tempNoteModels)
                            tempNoteModels.clear()
                            noteAdapter.viewIdListSetFalse()
                            noteAdapter.modelArrayListClear()
                            setHasOptionsMenu(false)
                            viewModel.getAllNotes(requireContext())
                        }

                    })
                    .setNegativeButton(getString(R.string.cancel), object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            //hiçbirşey silimedi
                        }

                    })
                    .setCancelable(false)
                    .create()
                    .show()

            }
            R.id.favoriteItems-> {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.favorites))
                    .setMessage(getString(R.string.areyousureyouwanttoaddfavorites))
                    .setIcon(R.drawable.favorites)
                    .setPositiveButton(getString(R.string.addtofavorites),object:DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            favoritesList.clear()
                            for(i in tempNoteModels){
                                favoritesList.add(NoteFavoritesModel(i.title,i.description,i.imageBase64,i.date))
                            }
                            viewModel.addFavorites(requireContext(),favoritesList)
                            setHasOptionsMenu(false)
                            viewModel.deleteNotes(requireContext(),tempNoteModels)
                            tempNoteModels.clear()
                            viewModel.getAllNotes(requireContext())
                            noteAdapter.modelArrayListClear()
                            noteAdapter.viewIdListSetFalse()
                        }

                    })
                    .setNegativeButton(getString(R.string.cancel),object :DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {

                        }


                    })
                    .setCancelable(false)
                    .create()
                    .show()



            }
            R.id.shareItem->{viewModel.shareNote(tempNoteModels.first().title,tempNoteModels.first().description,requireActivity())}
            R.id.alarmItem->{

                lifecycleScope.launch {
                    Log.d(TAG, "onOptionsItemSelected: ")
                    val args=Bundle()
                    args.putSerializable("data",tempNoteModels[0])
                    setHasOptionsMenu(false)

                    viewModel.getAllNotes(requireContext())
                    tempNoteModels.clear()
                    noteAdapter.modelArrayListClear()
                    noteAdapter.viewIdListSetFalse()

                    Log.d(TAG, "onOptionsItemSelected: $tempNoteModels")
                    selectDateFragment.arguments=args
                    selectDateFragment.show(parentFragmentManager,null)




                    }
                    




            }
           R.id.saveToFirebase->{viewModel.saveNoteToFirebase(tempNoteModels,context)}
        }
        return true
    }

}





