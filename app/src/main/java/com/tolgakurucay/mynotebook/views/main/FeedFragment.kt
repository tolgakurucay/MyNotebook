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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
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
import com.tolgakurucay.mynotebook.utils.Util.showAlertDialog
import com.tolgakurucay.mynotebook.utils.Util.showAlertDialogWithFuncs
import com.tolgakurucay.mynotebook.utils.Util.showAlertDialogWithOneButtonFunc
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
    private val viewModel:FeedFragmentViewModel by viewModels()
    private var favoritesList=ArrayList<NoteFavoritesModel>()
    @Inject lateinit var selectDateFragment:SelectDateFragment
    @Inject lateinit var loadingDialog: CustomLoadingDialog

    private var imageByteArrey:ByteArray?=null


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
    ): View {
        binding=FragmentFeedBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        buttonClickListener()
        observeLiveData()

    }

    private fun observeLiveData(){


        viewModel.loading.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it){
                    if (loadingDialog.isAdded){
                        loadingDialog.show(parentFragmentManager,null)
                    }

                }
                else
                {
                    if(loadingDialog.isAdded){
                        loadingDialog.dismiss()
                    }

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
                    showAlertDialog(getString(R.string.error),it,R.drawable.error,getString(R.string.okay))
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

        viewModel.byteArray.observe(viewLifecycleOwner, Observer {
            it?.let {
                Util.byteArray=it
                Glide.with(this).asBitmap().load(it).into(object:SimpleTarget<Bitmap>(){
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
        if(Util.byteArray!=null){
            Log.d(TAG, "init: boş değil yüklüyor")
            Glide.with(this).asBitmap().load(Util.byteArray).into(object:SimpleTarget<Bitmap>(){
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    val drawable=BitmapDrawable(resource)
                    binding.constraintFeed.setBackgroundDrawable(drawable)
                }

            })
        }
        else
        {

            Log.d(TAG, "init: boş, internetten çekildi")
            viewModel.getPPfromStorage()
        }


        setHasOptionsMenu(false)
        noteAdapter.modelArrayListEx.clear()
        for(i in 0 until noteAdapter.viewIdList.size){
            noteAdapter.viewIdList.put(i,false)
        }

        binding.recyclerView.layoutManager=GridLayoutManager(this.requireContext(),2,GridLayoutManager.VERTICAL,false)
        binding.recyclerView.adapter=noteAdapter

        binding.bottomNavigationView.background=null
        binding.bottomNavigationView.menu.getItem(2).isEnabled=false
        viewModel.getAllNotes(this.requireContext())
        


    }


    private fun requestPermissions(){
        if(ContextCompat.checkSelfPermission(this.requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            Snackbar.make(this.requireView(),getString(R.string.permissionneeded),Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.givepermission)
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this@FeedFragment.requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    ActivityCompat.requestPermissions(
                        this@FeedFragment.requireActivity(),
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1
                    )
                } else {
                    val intent = Intent()
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts(
                        "package",
                        this@FeedFragment.requireActivity().packageName,
                        null
                    )
                    intent.setData(uri)
                    startActivity(intent)
                }
            }.show()
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
           val direction=FeedFragmentDirections.actionFeedFragmentToAddNoteFragment(null)
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

    private fun refreshCurrentFragment(){
        val id = requireView().findNavController().currentDestination?.id
        requireView().findNavController().popBackStack(id!!,true)
        requireView().findNavController().navigate(id)
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_bar_menu,menu)
        menuTop=menu


        super.onCreateOptionsMenu(menu, inflater)

    }



    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.deleteItems->  {
                showAlertDialogWithFuncs(getString(R.string.delete),getString(R.string.youwantdelete),R.drawable.ic_baseline_delete_24,getString(R.string.delete),getString(R.string.cancel),{
                    viewModel.deleteNotes(requireContext(),tempNoteModels)
                    setHasOptionsMenu(false)
                    noteAdapter.viewIdListSetFalse()
                    noteAdapter.modelArrayListClear()
                    refreshCurrentFragment()
                                                                                                                                                                                             },{})
            }
            R.id.favoriteItems-> {
                showAlertDialogWithFuncs(getString(R.string.favorites),getString(R.string.areyousureyouwanttoaddfavorites),R.drawable.favorites,getString(R.string.addtofavorites),getString(R.string.cancel),{
                    favoritesList.clear()
                    for(i in tempNoteModels){
                        favoritesList.add(NoteFavoritesModel(i.title,i.description,i.imageBase64,i.date))
                    }
                    viewModel.addFavorites(requireContext(),favoritesList)
                    setHasOptionsMenu(false)
                    viewModel.deleteNotes(requireContext(),tempNoteModels)
                    noteAdapter.modelArrayListClear()
                    noteAdapter.viewIdListSetFalse()
                    refreshCurrentFragment()

                },{})




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

                    selectDateFragment.arguments=args
                    selectDateFragment.show(parentFragmentManager,null)
                    refreshCurrentFragment()

                    }

            }
           R.id.saveToFirebase->{viewModel.saveNoteToFirebase(tempNoteModels,context)}
        }
        return true
    }

}





