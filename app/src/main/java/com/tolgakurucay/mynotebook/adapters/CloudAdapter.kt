package com.tolgakurucay.mynotebook.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tolgakurucay.mynotebook.R
import android.view.View
import com.tolgakurucay.mynotebook.databinding.CloudRowBinding
import com.tolgakurucay.mynotebook.models.NoteModel
import com.tolgakurucay.mynotebook.utils.GetCurrentDate
import com.tolgakurucay.mynotebook.utils.Util

class CloudAdapter(var arrayList:ArrayList<NoteModel>) : RecyclerView.Adapter<CloudAdapter.CloudHolder>() {
    class CloudHolder(val binding:CloudRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CloudHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
       val view=CloudRowBinding.inflate(layoutInflater,parent,false)
        return CloudHolder(view)

    }

    override fun onBindViewHolder(holder: CloudHolder, position: Int) {
        val date=GetCurrentDate()
       holder.binding.textViewCloudTitle.setText(arrayList[position].title)
        holder.binding.textViewCloudDescription.setText(arrayList[position].description)


        val imageConverter=Util.base64ToBitmap(arrayList[position].imageBase64)
        if(imageConverter!=null){
            holder.binding.imageViewCloud.setImageBitmap(imageConverter)
            holder.binding.imageViewCloud.background=null
        }



        val dateAsString=date.getDateFromLong(arrayList[position].date)
        holder.binding.textViewCloudDate.setText(dateAsString)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun updateAdapter(newArrayList:ArrayList<NoteModel>){
        arrayList.clear()
        arrayList=newArrayList
        notifyDataSetChanged()
    }
}