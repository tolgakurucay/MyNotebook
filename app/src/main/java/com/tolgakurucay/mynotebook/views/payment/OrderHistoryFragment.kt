package com.tolgakurucay.mynotebook.views.payment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.FragmentOrderHistoryBinding
import com.tolgakurucay.mynotebook.viewmodels.payment.OrderHistoryViewModel


class OrderHistoryFragment : Fragment() {

    private lateinit var binding:FragmentOrderHistoryBinding
    private lateinit var viewModel:OrderHistoryViewModel
    val TAG="bilgi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding= FragmentOrderHistoryBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
         viewModel.getOrderList()

     observeLiveData()


    }

    private fun init(){
        viewModel=ViewModelProvider(this)[OrderHistoryViewModel::class.java]
    }

     fun observeLiveData(){
         viewModel.orderListLive.observe(viewLifecycleOwner, Observer {
             it?.let {
                 Log.d(TAG, "observeLiveData: $it")
             }
         })
     }





}