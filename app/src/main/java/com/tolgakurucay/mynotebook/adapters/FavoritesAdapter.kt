package com.tolgakurucay.mynotebook.adapters

import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.tolgakurucay.mynotebook.databinding.FavoritesLayoutBinding
import com.tolgakurucay.mynotebook.databinding.FragmentFavoritesBinding
import com.tolgakurucay.mynotebook.models.NoteFavoritesModel
import com.tolgakurucay.mynotebook.utils.GetCurrentDate
import com.tolgakurucay.mynotebook.utils.Util
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject




class FavoritesAdapter(var favoritesList:List<NoteFavoritesModel>,val completion:(NoteFavoritesModel)->Unit) : RecyclerView.Adapter<FavoritesAdapter.FavoritesHolder>() {

    private var dateClass=GetCurrentDate()
    private lateinit var binding: FavoritesLayoutBinding


    class FavoritesHolder(binding:FavoritesLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val title=binding.textViewFavoritesTitle
        val date=binding.textVliewFavoritesDate
        val description=binding.textView4
        val imageView=binding.imageViewFavorites
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesHolder {
        val inflater=LayoutInflater.from(parent.context)
         binding=FavoritesLayoutBinding.inflate(inflater)
        return FavoritesHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoritesHolder, position: Int) {
        holder.date.setText(dateClass.getDateFromLong(favoritesList[position].date))
        holder.description.setText(favoritesList[position].description)
        holder.title.setText(favoritesList[position].title)
        if(favoritesList[position].imageBase64!=null){
            holder.imageView.background=null
            holder.imageView.setImageBitmap(Util.base64ToBitmap(favoritesList[position].imageBase64))
        }

        //doesn't work
      /*  holder.imageView.setOnClickListener {
            Log.d("bilgi", "onBindViewHolder: tıklandı")
            completion(favoritesList[position])

        }*/

        binding.imageViewDeleteFavorites.setOnClickListener {
            Log.d("bilgi", "tıklandıbins")
            completion(favoritesList[position])
        }

    }

    override fun getItemCount(): Int {
        return favoritesList.size
    }

    fun updateList(newFavoritesList:List<NoteFavoritesModel>){
        favoritesList=newFavoritesList
        notifyDataSetChanged()
    }
}