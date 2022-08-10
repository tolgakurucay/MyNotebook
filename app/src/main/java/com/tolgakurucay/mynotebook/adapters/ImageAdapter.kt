package com.tolgakurucay.mynotebook.adapters

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.tolgakurucay.mynotebook.databinding.ImageRowBinding
import com.tolgakurucay.mynotebook.models.ImageResponse
import com.tolgakurucay.mynotebook.utils.Util
import com.tolgakurucay.mynotebook.utils.Util.downloadFromString
import com.tolgakurucay.mynotebook.views.main.GetImageFromAPIFragmentDirections


class ImageAdapter(var imageResponse: ImageResponse) : RecyclerView.Adapter<ImageAdapter.ImageHolder>() {
    class ImageHolder(val binding:ImageRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val inflater=LayoutInflater.from(parent.context)
        val binding=ImageRowBinding.inflate(inflater,parent,false)
        return ImageHolder(binding)

    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {


        holder.binding.imageViewAPI.downloadFromString(imageResponse.hits[position].previewURL,Util.placeHolderProgressBar(holder.binding.root.context))

        holder.binding.imageViewAPI.setOnClickListener {

            val action=GetImageFromAPIFragmentDirections.actionGetImageFromAPIFragmentToAddNoteFragment(imageResponse.hits[position].previewURL)
            holder.itemView.findNavController().navigate(action)

        }



    }

    override fun getItemCount(): Int {
       return imageResponse.hits.size
    }

    fun updateAdapter(newImageResponse:ImageResponse){
        this.imageResponse=newImageResponse
        notifyDataSetChanged()

    }


}