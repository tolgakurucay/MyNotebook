package com.tolgakurucay.mynotebook.views.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.storage.FirebaseStorage
import com.tolgakurucay.mynotebook.adapters.PaymentAdapter
import com.tolgakurucay.mynotebook.databinding.ActivityUpgradePackageBinding
import com.tolgakurucay.mynotebook.models.Payment
import com.tolgakurucay.mynotebook.utils.CustomLoadingDialog
import com.tolgakurucay.mynotebook.viewmodels.main.UpgradePackageViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UpgradePackageActivity : AppCompatActivity() {

    private lateinit var binding:ActivityUpgradePackageBinding
    private lateinit var viewmodel:UpgradePackageViewModel
    private lateinit var storage:FirebaseStorage
    private lateinit var adapter:PaymentAdapter
    @Inject
    lateinit var loadingDialog:CustomLoadingDialog
    val TAG="bilgi"
    val paymentList=ArrayList<Payment>()
    var tempPosition:Int=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUpgradePackageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initButtons()

        viewmodel.getImagesFromFirebase(storage)
        observeLiveData()

    }

    private fun init(){
        viewmodel=ViewModelProvider(this).get(UpgradePackageViewModel::class.java)
        storage= FirebaseStorage.getInstance()
        adapter= PaymentAdapter(arrayListOf())
        binding.paymentViewPager.adapter=adapter
        TabLayoutMediator(binding.tabLayoutPayment,binding.paymentViewPager){tab,position->
        }.attach()

        binding.paymentViewPager.registerOnPageChangeCallback(object:ViewPager2.OnPageChangeCallback(){
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                tempPosition=position
            }


        })

    }

    private fun observeLiveData(){
        viewmodel.uriList.observe(this, Observer {
            it?.let {
                paymentList.add(Payment("10","50 TL",it[1]))
                paymentList.add(Payment("20","70 TL",it[0]))
                paymentList.add(Payment("50","100 TL",it[2]))
                adapter.updateList(paymentList)

            }
        })

        viewmodel.loading.observe(this, Observer {
            it?.let {
                if(it){
                    loadingDialog.show(supportFragmentManager,null)
                }
                else
                {
                    loadingDialog.dismiss()
                }
            }
        })
    }

    private fun initButtons(){

        binding.buttonBack.setOnClickListener {
            if(tempPosition>0){
                tempPosition--
                binding.paymentViewPager.currentItem=tempPosition
            }
        }
        binding.buttonNext.setOnClickListener {
           if (tempPosition<2){
               tempPosition++
               binding.paymentViewPager.currentItem=tempPosition
           }

        }
        binding.buttonPay.setOnClickListener {
            when(tempPosition){
                0->{}
                1->{}
                2->{}
            }
        }







    }



}