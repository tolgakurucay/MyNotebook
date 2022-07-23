package com.tolgakurucay.mynotebook.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.NoteLayoutBinding
import com.tolgakurucay.mynotebook.models.NoteModel
import com.tolgakurucay.mynotebook.utils.GetCurrentDate
import com.tolgakurucay.mynotebook.utils.Util
import com.tolgakurucay.mynotebook.views.main.FeedFragmentDirections

class NoteAdapter(var noteList:ArrayList<NoteModel>) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private lateinit var binding:NoteLayoutBinding
    private val dateClass= GetCurrentDate()
    val TAG="bilgi"
    val list=HashMap<NoteModel,Boolean>()



    class NoteViewHolder(view:View): RecyclerView.ViewHolder(view){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        binding=NoteLayoutBinding.inflate(inflater)
        return NoteViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, @SuppressLint("RecyclerView") position: Int) {

        for(note in noteList){
            list.put(note,false)
        }

        binding.textViewTitle.setText(noteList[position].title)
        binding.textViewDate.setText(dateClass.getDateFromLong(noteList[position].date))
       if(noteList[position].imageBase64!=null){
           noteList[position].imageBase64?.let {
               binding.imageViewImage.setImageBitmap(Util.base64ToBitmap(it))
               binding.imageViewImage.background=null
           }
        }

        binding.itemLayout.setOnClickListener {
            val noteModel=noteList[position]
            val action=FeedFragmentDirections.actionFeedFragmentToNoteFragment(noteModel)
            holder.itemView.findNavController().navigate(action)
        }

       holder.itemView.setOnLongClickListener(object:View.OnLongClickListener{
           override fun onLongClick(p0: View?): Boolean {
               p0?.let { view->
                   holder.itemView.setBackgroundColor(R.color.purple_500)
                   list.put(noteList[position],true)

                   Log.d(TAG, "onLongClick: ${noteList[position]}")


               }
               return true
           }

       })





    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    fun updateNoteList(newNoteList:List<NoteModel>){
        noteList.clear()
        noteList= ArrayList(newNoteList)
        notifyDataSetChanged()
    }





}

