package com.tolgakurucay.mynotebook.viewmodels.main

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.util.rangeTo
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.*
import com.google.common.collect.ImmutableList
import com.google.firebase.storage.FirebaseStorage
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.models.Payment
import kotlinx.coroutines.launch

class UpgradePackageViewModel : ViewModel() {
val TAG="bilgi"

    val uriList=MutableLiveData<ArrayList<Uri>>()
    val loading=MutableLiveData<Boolean>()
    val arrayList=MutableLiveData<ArrayList<Payment>>()
    private val uriArrayList=ArrayList<Uri>()
    val paymentListTemp=ArrayList<Payment>()



    private lateinit var billingClient:BillingClient
    private val purchasesUpdatedListener=
        PurchasesUpdatedListener{billingResult,purchases->
            if(billingResult.responseCode==BillingClient.BillingResponseCode.OK){
                Log.d(TAG, "updatedListener: ")
                if(!purchases.isNullOrEmpty()){
                    val consumeParams=ConsumeParams.newBuilder()
                        .setPurchaseToken(purchases?.get(0)!!.purchaseToken)
                        .build()
                    billingClient.consumeAsync(consumeParams,object:ConsumeResponseListener{
                        override fun onConsumeResponse(p0: BillingResult, p1: String) {
                            if(p0.responseCode==BillingClient.BillingResponseCode.OK){ 
                                Log.d(TAG, "onConsumeResponse: tüketildi")
                            }
                            else
                            {
                                Log.d(TAG, "onConsumeResponse: tüketilemedi")
                            }

                        }

                    })
                }
               
            }
            
        }





    fun getImagesFromFirebase(storage:FirebaseStorage){
        loading.value=true

        val advantagePlan=storage.reference.child("advantage_plan.png")
        val basicPlan=storage.reference.child("basic_plan.png")
        val premiumPlan=storage.reference.child("premium_plan.png")

        Log.d(TAG, "getImagesFromFirebase: $advantagePlan")
        basicPlan.downloadUrl.addOnSuccessListener { basicPlan->
            basicPlan?.let {bsc->
                uriArrayList.add(bsc)
                advantagePlan.downloadUrl.addOnSuccessListener { advantagePlan->
                    advantagePlan?.let { adv->
                        uriArrayList.add(adv)
                        premiumPlan.downloadUrl.addOnSuccessListener { premiumPlan->
                            premiumPlan?.let { prm->
                                uriArrayList.add(prm)

                                uriList.value=uriArrayList
                                loading.value=false
                            }
                        }.addOnFailureListener { loading.value=false }
                    }
                }.addOnFailureListener { loading.value=false }

            }
        }.addOnFailureListener { loading.value=false }
    }


    fun getInformations(context: Context,purchaseIdList:List<String>,uriArrayList:ArrayList<Uri>){
        loading.value=false
        loading.value=true
        billingClient=BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object:BillingClientStateListener{
            override fun onBillingServiceDisconnected() {
                getInformations(context,purchaseIdList,uriArrayList)
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if(billingResult.responseCode==BillingClient.BillingResponseCode.OK){
                    Log.d(TAG, "onBillingSetupFinished: girildi1")
                    Log.d(TAG, "idList: ${purchaseIdList}")
                    //continue
                    

                    val params=QueryProductDetailsParams.newBuilder()
                        .setProductList(
                            ImmutableList.of(
                                QueryProductDetailsParams.Product.newBuilder()
                                     .setProductId(purchaseIdList[0])
                                      .setProductType(BillingClient.ProductType.INAPP)
                                      .build(),
                                QueryProductDetailsParams.Product.newBuilder()
                                    .setProductId(purchaseIdList[1])
                                    .setProductType(BillingClient.ProductType.INAPP)
                                     .build(),
                                QueryProductDetailsParams.Product.newBuilder()
                                    .setProductId(purchaseIdList[2])
                                    .setProductType(BillingClient.ProductType.INAPP)
                                    .build()

                            )

                        ).build()

                    Log.d(TAG, "onBillingSetupFinished: girildi2")

                    billingClient.queryProductDetailsAsync(params){
                        billingRes,
                        productDetailsList->
                        if(billingRes.responseCode==BillingClient.BillingResponseCode.OK){
                            //continue
                            //viewmodelscope burada işlemlerin sırayla yapılmasını sağlar(coroutines)
                            viewModelScope.launch {
                                for((j, i) in productDetailsList.withIndex()){
                                    paymentListTemp.add(Payment(i.name,i.oneTimePurchaseOfferDetails?.formattedPrice.toString(),i.description+" "+context.getString(R.string.right),uriArrayList[j],i.productId))
                                }

                                Log.d(TAG, "gönderilmeden önce$paymentListTemp")

                                arrayList.value=paymentListTemp
                                //completion(paymentListTemp)
                                loading.value=false
                            }


                            



                        }

                    }

                }
                else
                {
                    loading.value=false
                }

            }

        })



    }
    
    
    fun pay(id:String,activity:Activity){
        loading.value=true

        billingClient.startConnection(object:BillingClientStateListener{
            override fun onBillingServiceDisconnected() {
                pay(id,activity)
            }

            override fun onBillingSetupFinished(p0: BillingResult) {
               if(p0.responseCode==BillingClient.BillingResponseCode.OK){
                   val params=QueryProductDetailsParams.newBuilder()
                       .setProductList(
                           ImmutableList.of(
                               QueryProductDetailsParams.Product.newBuilder()
                                   .setProductId(id)
                                   .setProductType(BillingClient.ProductType.INAPP)
                                   .build()
                           )

                       ).build()
                   
                   billingClient.queryProductDetailsAsync(params){
                           billingRes,
                           productDetailsList->
                       if (billingRes.responseCode==BillingClient.BillingResponseCode.OK){
                           viewModelScope.launch {
                               val productDetailsParamsList = listOf(
                                   BillingFlowParams.ProductDetailsParams.newBuilder()
                                       .setProductDetails(productDetailsList.first())
                                       .build()
                               )
                               val billingFlowParams = BillingFlowParams.newBuilder()
                                   .setProductDetailsParamsList(productDetailsParamsList)
                                   .build()

                               loading.value=false
                               billingClient.launchBillingFlow(activity,billingFlowParams)

                           }

                       }



                       
                   }
               }
                else
               {
                   loading.value=false
               }
            }

        })
    }









    override fun onCleared() {
        billingClient.endConnection()
        super.onCleared()
    }

}