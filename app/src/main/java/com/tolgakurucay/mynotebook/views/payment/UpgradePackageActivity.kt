package com.tolgakurucay.mynotebook.views.payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
    private lateinit var viewmodel: UpgradePackageViewModel
    private lateinit var storage:FirebaseStorage
    private lateinit var adapter:PaymentAdapter
    @Inject
    lateinit var loadingDialog:CustomLoadingDialog
    val TAG="bilgi"
    var tempPosition:Int=0
    var tempList= arrayListOf<Payment>()

    //have to 3 items here

    private val purchaseIdList= listOf("com.mynotebook.20","com.mynotebook.50","com.mynotebook.100")



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

       // adapter.updateList(arrayListOf(Payment("sdasdasd","fiyat","100","https://firebasestorage.googleapis.com/v0/b/my-notebook-39613.appspot.com/o/advantage_plan.png?alt=media&token=50ab5642-edf1-4694-a31d-8e595dc30180".toUri(),"idddd")))


    }

    private fun observeLiveData(){
        // TODO: yapılacaklar arasında liste gözlenememe işi var

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

                //görüntülerin uri'i geldikten sonra bilgileri getir
               viewmodel.getInformations(this,purchaseIdList,uriList)



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



}