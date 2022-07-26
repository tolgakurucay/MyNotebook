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
import com.tolgakurucay.mynotebook.models.Payment
import kotlinx.coroutines.launch

class UpgradePackageViewModel : ViewModel() {
val TAG="bilgi"

    val uriList=MutableLiveData<ArrayList<Uri>>()
    val loading=MutableLiveData<Boolean>()
    private lateinit var billingClient:BillingClient
    private val purchasesUpdatedListener=
        PurchasesUpdatedListener{billingResult,purchases->
            if(billingResult.responseCode==BillingClient.BillingResponseCode.OK){
                Log.d(TAG, "updatedListener: ")
            }
            
        }
    private val uriArrayList=ArrayList<Uri>()
    val paymentListTemp=ArrayList<Payment>()




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


    fun getInformations(context: Context,purchaseIdList:List<String>,uriArrayList:ArrayList<Uri>,completion:(ArrayList<Payment>)->Unit){
        loading.value=false
        loading.value=true
        billingClient=BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object:BillingClientStateListener{
            override fun onBillingServiceDisconnected() {
                getInformations(context,purchaseIdList,uriArrayList,completion)
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

                            for((j, i) in productDetailsList.withIndex()){
                                paymentListTemp.add(Payment(i.name,i.oneTimePurchaseOfferDetails?.formattedPrice.toString(),i.description,uriArrayList[j],i.productId))
                            }

                            Log.d(TAG, "gönderilmeden önce\n\n$paymentListTemp")

                            completion(paymentListTemp)
                            loading.value=false



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
                           val productDetailsParamsList = listOf(
                               BillingFlowParams.ProductDetailsParams.newBuilder()
                                   .setProductDetails(productDetailsList.first())
                                   .build()
                           )
                           val billingFlowParams = BillingFlowParams.newBuilder()
                               .setProductDetailsParamsList(productDetailsParamsList)
                               .build()


                           billingClient.launchBillingFlow(activity,billingFlowParams)

                       }




                       
                       
                   }
               }
            }

        })
    }









    override fun onCleared() {
        billingClient.endConnection()
        super.onCleared()
    }

}