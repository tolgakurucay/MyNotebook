package com.tolgakurucay.mynotebook.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.tolgakurucay.mynotebook.databinding.FavoritesLayoutBinding
import com.tolgakurucay.mynotebook.databinding.FragmentFavoritesBinding
import com.tolgakurucay.mynotebook.models.NoteFavoritesModel
import com.tolgakurucay.mynotebook.utils.GetCurrentDate
import com.tolgakurucay.mynotebook.utils.Util
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject




class FavoritesAdapter(var favoritesList:List<NoteFavoritesModel>,val completion:(NoteFavoritesModel,process:String,title:String?,description:String?)->Unit) : RecyclerView.Adapter<FavoritesAdapter.FavoritesHolder>() {

    private var dateClass=GetCurrentDate()
    private lateinit var binding: FavoritesLayoutBinding


    class FavoritesHolder(binding:FavoritesLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val title=binding.textViewFavoritesTitle
        val date=binding.textVliewFavoritesDate
        val description=binding.editTextFavoritesDescription
        val imageView=binding.imageViewFavorites
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesHolder {
        val inflater=LayoutInflater.from(parent.context)
         binding=FavoritesLayoutBinding.inflate(inflater)
        return FavoritesHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: FavoritesHolder, position: Int) {
        holder.date.setText(dateClass.getDateFromLong(favoritesList[position].date))
        holder.description.setText(favoritesList[position].description)
        holder.title.setText(favoritesList[position].title)
        if(favoritesList[position].imageBase64!=null){
            holder.imageView.background=null
            holder.imageView.setImageBitmap(Util.base64ToBitmap(favoritesList[position].imageBase64))
        }



        binding.imageViewDeleteFavorites.setOnClickListener {

            completion(favoritesList[position],"delete",null,null)
        }

        binding.imageViewFavoritesSave.setOnClickListener {
            completion(favoritesList[position],"update",binding.textViewFavoritesTitle.text.toString(),binding.editTextFavoritesDescription.text.toString())
        }

        binding.imageViewFavoritesShare.setOnClickListener {
            completion(favoritesList[position],"share",binding.textViewFavoritesTitle.text.toString(),binding.editTextFavoritesDescription.text.toString())
        }

    }

    override fun getItemCount(): Int {
        return favoritesList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newFavoritesList:List<NoteFavoritesModel>){
        favoritesList=newFavoritesList
        notifyDataSetChanged()
    }
}