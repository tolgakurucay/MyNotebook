package com.tolgakurucay.mynotebook.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Index
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.OrderHistoryRowBinding
import com.tolgakurucay.mynotebook.models.OrderHistory

class OrderHistoryAdapter : RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryHolder>() {
    class OrderHistoryHolder(val binding:OrderHistoryRowBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffUtilCallback = object : DiffUtil.ItemCallback<OrderHistory>(){
        override fun areItemsTheSame(oldItem: OrderHistory, newItem: OrderHistory): Boolean {
           return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: OrderHistory, newItem: OrderHistory): Boolean {
            return oldItem==newItem
        }


    }

    private val recyclerListDiffer=AsyncListDiffer(this,diffUtilCallback)
    var arrayList:List<OrderHistory>
    get() = recyclerListDiffer.currentList
    set(value) = recyclerListDiffer.submitList(value)




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryHolder {
        val inflater=LayoutInflater.from(parent.context)
       val view= DataBindingUtil.inflate<OrderHistoryRowBinding>(inflater,
            R.layout.order_history_row,
            parent,
            false)
        return OrderHistoryHolder(view)
    }

    override fun onBindViewHolder(holder: OrderHistoryHolder, position: Int) {

       holder.binding.orderHistoryObject=arrayList[position]

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }


}