package com.tolgakurucay.mynotebook

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.tolgakurucay.mynotebook.databinding.NoteLayoutBinding
import com.tolgakurucay.mynotebook.models.NoteModel
import com.tolgakurucay.mynotebook.utils.GetCurrentDate
import com.tolgakurucay.mynotebook.utils.Util

class NoteAdapter(var noteList:ArrayList<NoteModel>) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private lateinit var binding:NoteLayoutBinding
    private val dateClass= GetCurrentDate()

    class NoteViewHolder(view:View): RecyclerView.ViewHolder(view){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        binding=NoteLayoutBinding.inflate(inflater)
        return NoteViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        binding.textViewTitle.setText(noteList[position].title)
        binding.textViewDate.setText(dateClass.getDateFromLong(noteList[position].date))
        binding.imageViewImage.setImageURI(noteList[position].title.toUri())
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    fun updateNoteList(newNoteList:List<NoteModel>){
        noteList.clear()
        noteList=ArrayList(newNoteList)
        notifyDataSetChanged()
    }

}