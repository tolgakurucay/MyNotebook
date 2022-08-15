package com.tolgakurucay.mynotebook.views.payment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.adapters.OrderHistoryAdapter
import com.tolgakurucay.mynotebook.databinding.FragmentOrderHistoryBinding
import com.tolgakurucay.mynotebook.utils.CustomLoadingDialog
import com.tolgakurucay.mynotebook.viewmodels.payment.OrderHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderHistoryFragment : Fragment() {

    private lateinit var binding:FragmentOrderHistoryBinding
    private val viewModel:OrderHistoryViewModel by viewModels()
    private lateinit var historyAdapter:OrderHistoryAdapter
    @Inject lateinit var loadingDialog:CustomLoadingDialog
    val TAG="bilgi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding= FragmentOrderHistoryBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
         viewModel.getOrderList()

         observeLiveData()


    }

    private fun init(){

        historyAdapter=OrderHistoryAdapter()
        binding.orderHistoryRecycler.adapter=historyAdapter
        binding.orderHistoryRecycler.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

    }

     fun observeLiveData(){
         viewModel.orderListLive.observe(viewLifecycleOwner, Observer {
             it?.let {
                 historyAdapter.arrayList=it
                 Log.d(TAG, "observeLiveData: $it")
             }
         })

         viewModel.loadingLive.observe(viewLifecycleOwner, Observer {
             it?.let {
                 if(it){
                     loadingDialog.show(parentFragmentManager,null)
                 }
                 else
                 {
                     loadingDialog.dismiss()
                 }
             }
         })
     }





}