package com.tolgakurucay.mynotebook.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tolgakurucay.mynotebook.R
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.tolgakurucay.mynotebook.databinding.CloudRowBinding
import com.tolgakurucay.mynotebook.models.NoteModel
import com.tolgakurucay.mynotebook.utils.GetCurrentDate
import com.tolgakurucay.mynotebook.utils.Util

class CloudAdapter() : RecyclerView.Adapter<CloudAdapter.CloudHolder>() {


    var noteList : List<NoteModel>
    get() =listDiffer.currentList
    set(value) =listDiffer.submitList(value)

    class CloudHolder(val binding:CloudRowBinding) : RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CloudHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
       val view=CloudRowBinding.inflate(layoutInflater,parent,false)
        return CloudHolder(view)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CloudHolder, position: Int) {
        val date=GetCurrentDate()
       holder.binding.textViewCloudTitle.setText(noteList[position].title)
        holder.binding.textViewCloudDescription.setText(noteList[position].description)


        val imageConverter=Util.base64ToBitmap(noteList[position].imageBase64)
        if(imageConverter!=null){
            holder.binding.imageViewCloud.setImageBitmap(imageConverter)
            holder.binding.imageViewCloud.background=null
        }



        val dateAsString=date.getDateFromLong(noteList[position].date)
        holder.binding.textViewCloudDate.setText(dateAsString)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }


    private val diffUtil = object : DiffUtil.ItemCallback<NoteModel>(){
        override fun areItemsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
           return oldItem==newItem
        }

    }

    private val listDiffer = AsyncListDiffer(this,diffUtil)

}