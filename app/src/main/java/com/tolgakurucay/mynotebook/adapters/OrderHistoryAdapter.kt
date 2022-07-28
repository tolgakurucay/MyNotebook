package com.tolgakurucay.mynotebook.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Index
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.OrderHistoryRowBinding
import com.tolgakurucay.mynotebook.models.OrderHistory

class OrderHistoryAdapter(val arrayList:ArrayList<OrderHistory>) : RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryHolder>() {
    class OrderHistoryHolder(val binding:OrderHistoryRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

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

    fun updateAdapter(newList:ArrayList<OrderHistory>){
        arrayList.clear()
        arrayList.addAll(newList)
        notifyDataSetChanged()
    }
}