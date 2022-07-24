package com.tolgakurucay.mynotebook.adapters

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.tolgakurucay.mynotebook.databinding.FavoritesLayoutBinding
import com.tolgakurucay.mynotebook.models.NoteFavoritesModel

class FavoritesAdapter(var favoritesList:List<NoteFavoritesModel>) : RecyclerView.Adapter<FavoritesAdapter.FavoritesHolder>() {
    class FavoritesHolder(binding:FavoritesLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val title=binding.textViewFavoritesTitle
        val date=binding.textVliewFavoritesDate
        val description=binding.textView4
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesHolder {
        val inflater=LayoutInflater.from(parent.context)
        val binding=FavoritesLayoutBinding.inflate(inflater)
        return FavoritesHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoritesHolder, position: Int) {
        holder.date.setText(favoritesList[position].date.toString())
        holder.description.setText(favoritesList[position].description)
        holder.title.setText(favoritesList[position].title)
    }

    override fun getItemCount(): Int {
        return favoritesList.size
    }

    fun updateList(newFavoritesList:List<NoteFavoritesModel>){
        favoritesList=newFavoritesList
        notifyDataSetChanged()
    }
}