package com.tolgakurucay.mynotebook.views.payment

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.storage.FirebaseStorage
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.adapters.PaymentAdapter
import com.tolgakurucay.mynotebook.databinding.ActivityUpgradePackageBinding
import com.tolgakurucay.mynotebook.models.Payment
import com.tolgakurucay.mynotebook.utils.CustomLoadingDialog
import com.tolgakurucay.mynotebook.viewmodels.payment.UpgradePackageViewModel
import com.tolgakurucay.mynotebook.views.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UpgradePackageActivity : AppCompatActivity() {

    private lateinit var binding:ActivityUpgradePackageBinding
    private val viewmodel: UpgradePackageViewModel by viewModels()
    private lateinit var storage:FirebaseStorage
    private lateinit var adapter:PaymentAdapter
    @Inject lateinit var loadingDialog:CustomLoadingDialog
    val TAG="bilgi"
    var tempPosition:Int=0

    //have to 3 items here

    private val purchaseIdList= listOf("com.mynotebook.20","com.mynotebook.50","com.mynotebook.100")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUpgradePackageBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.from_left,R.anim.to_right)
        setContentView(binding.root)
        init()
        initButtons()

        viewmodel.getImagesFromFirebase(storage)

        observeLiveData()



    }

    private fun init(){
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


        viewmodel.paymentStatus.observe(this, Observer {
            it?.let {
                if(it.equals("success")){
                    //payment successful
                    val intent= Intent(this@UpgradePackageActivity, MainActivity::class.java)
                    Toast.makeText(this, getString(R.string.paymentsuccessful), Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                    finish()
                }
                else
                {
                    Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                }
            }
        })


        viewmodel.uriList.observe(this, Observer {
            it?.let {uriList->
                val newList=ArrayList<Uri>()
                newList.add(it[2])
                newList.add(it[0])
                newList.add(it[1])

               viewmodel.getInformations(this,purchaseIdList,newList)



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

        viewmodel.arrayList.observe(this, Observer {
            it?.let {arrayList: ArrayList<Payment> ->
                lifecycleScope.launch {
                    loadingDialog.show(supportFragmentManager,null)
                    //bilgiler geldikten sonra düzenle(play storedaki purchase sırasına göre gidiyor)
                    val newList=ArrayList<Payment>()
                    newList.add(arrayList[1])
                    newList.add(arrayList[2])
                    newList.add(arrayList[0])

                    adapter.updateList(newList)
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
                0->{viewmodel.pay(purchaseIdList[0],this)}
                1->{viewmodel.pay(purchaseIdList[1],this)}
                2->{viewmodel.pay(purchaseIdList[2],this)}
            }
        }







    }

    override fun onBackPressed() {

        val intent=Intent(this,MainActivity::class.java)
        intent.putExtra("from","buy")
        startActivity(intent)

    }



}