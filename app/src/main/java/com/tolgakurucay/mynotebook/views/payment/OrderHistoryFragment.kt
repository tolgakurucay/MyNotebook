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
import androidx.recyclerview.widget.LinearLayoutManager
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.adapters.OrderHistoryAdapter
import com.tolgakurucay.mynotebook.databinding.FragmentOrderHistoryBinding
import com.tolgakurucay.mynotebook.viewmodels.payment.OrderHistoryViewModel


class OrderHistoryFragment : Fragment() {

    private lateinit var binding:FragmentOrderHistoryBinding
    private lateinit var viewModel:OrderHistoryViewModel
    private lateinit var historyAdapter:OrderHistoryAdapter
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
        historyAdapter=OrderHistoryAdapter(arrayListOf())
        binding.orderHistoryRecycler.adapter=historyAdapter
        binding.orderHistoryRecycler.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

    }

     fun observeLiveData(){
         viewModel.orderListLive.observe(viewLifecycleOwner, Observer {
             it?.let {
                 historyAdapter.updateAdapter(it)
                 Log.d(TAG, "observeLiveData: $it")
             }
         })
     }





}