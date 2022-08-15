package com.tolgakurucay.mynotebook.adapters

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.PaymentRowBinding
import com.tolgakurucay.mynotebook.models.Payment

class PaymentAdapter : RecyclerView.Adapter<PaymentAdapter.PaymentHolder>() {

    class PaymentHolder(val binding:PaymentRowBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Payment>(){
        override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean {
           return oldItem==newItem
        }

    }

    private val asyncListDiffer=AsyncListDiffer(this,diffCallback)

    var paymentList: List<Payment>
    get() = asyncListDiffer.currentList
    set(value) = asyncListDiffer.submitList(value)



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






}