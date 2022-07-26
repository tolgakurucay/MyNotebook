package com.tolgakurucay.mynotebook.adapters

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter.POSITION_NONE
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.PaymentRowBinding
import com.tolgakurucay.mynotebook.models.Payment

class PaymentAdapter(var paymentList: ArrayList<Payment>) : RecyclerView.Adapter<PaymentAdapter.PaymentHolder>() {

    class PaymentHolder(val binding:PaymentRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view=DataBindingUtil.inflate<PaymentRowBinding>(
            inflater,
            R.layout.payment_row
            ,parent
            ,false
        )
        return PaymentHolder(view)

    }

    override fun onBindViewHolder(holder: PaymentHolder, position: Int) {
       holder.binding.paymentObject=paymentList[position]
    }

    override fun getItemCount(): Int {
       return paymentList.size
    }


    fun updateList(newList:ArrayList<Payment>){

        paymentList=newList
        notifyDataSetChanged()

    }




}