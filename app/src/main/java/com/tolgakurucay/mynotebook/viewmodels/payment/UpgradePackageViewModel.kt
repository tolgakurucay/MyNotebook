package com.tolgakurucay.mynotebook.viewmodels.payment

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.*
import com.google.common.collect.ImmutableList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.models.Payment
import com.tolgakurucay.mynotebook.models.PaymentHistory
import com.tolgakurucay.mynotebook.utils.GetCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class UpgradePackageViewModel @Inject constructor(): ViewModel() {
val TAG="bilgi"

    val uriList=MutableLiveData<ArrayList<Uri>>()
    val loading=MutableLiveData<Boolean>()
    val arrayList=MutableLiveData<ArrayList<Payment>>()
    private val uriArrayList=ArrayList<Uri>()
    val paymentListTemp=ArrayList<Payment>()

    val productDetailList= arrayListOf<ProductDetails>()
    val auth=FirebaseAuth.getInstance()
    val firestore=FirebaseFirestore.getInstance()
    val paymentStatus=MutableLiveData<String>()


    //1-alınan fiyata göre hak arttır
    //2-ödeme bilgilerini db'ye kaydet
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
                              viewModelScope.launch {
                                  val currentDate=GetCurrentDate()
                                  val json =JSONObject(purchases.first().originalJson)
                                  val orderID=purchases.first().orderId
                                  val productID=json.getString("productId")
                                  val purchaseToken=purchases.first().purchaseToken
                                  val price=productDetailList.first().oneTimePurchaseOfferDetails!!.formattedPrice
                                  val packageName=productDetailList.first().name
                                  Log.d(TAG, "denemedeneme\n\n${purchases.first()}")

                                  val payHistory=PaymentHistory(currentDate.currentDate(),orderID,price,auth.currentUser?.uid.toString(),productID,purchaseToken,packageName)
                                  var purchasedRight=0
                                  firestore.collection("PaymentHistory").add(payHistory)
                                      .addOnSuccessListener {
                                          if(productID.equals("com.mynotebook.20")){
                                              purchasedRight=20
                                          }
                                          else if(productID.equals("com.mynotebook.50")){
                                              purchasedRight=50
                                          }
                                          else if(productID.equals("com.mynotebook.100")){
                                              purchasedRight=100
                                          }


                                         increaseRightForCurrentUser(purchasedRight){
                                             if(it){
                                                 paymentStatus.value="success"
                                             }
                                             else
                                             {
                                                 paymentStatus.value="Network Error"
                                             }
                                         }
                                      }
                                      .addOnFailureListener {
                                          paymentStatus.value=it.localizedMessage
                                      }
                              }



                              

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



    fun increaseRightForCurrentUser(right:Int,completion:(Boolean)->Unit){
        loading.value=true
        firestore.collection("Right").document(auth.uid.toString()).get()
            .addOnSuccessListener {
                val path=it.reference.path

                if(it.exists()){
                    Log.d(TAG, "increaseRightForCurrentUser: alan var")
                    val oldRight=it.getDouble("right")
                    oldRight?.let { oldRght->
                        val newRight=(oldRght+right).toInt()
                        firestore.document(path).update("right",newRight)
                            .addOnSuccessListener { completion(true) }
                            .addOnFailureListener { completion(false) }

                    }
                    loading.value=false

                }
                else
                {
                    Log.d(TAG, "increaseRightForCurrentUser: alan yok")
                    val map=HashMap<String,Int>()
                    map.put("right",right)
                    firestore.document(path).set(map)
                        .addOnSuccessListener { completion(true) }
                        .addOnFailureListener { completion(false) }

                    loading.value=false

                }
            }
            .addOnFailureListener {
                completion(false)
                loading.value=false
            }

    }

    fun getImagesFromFirebase(storage:FirebaseStorage){
        loading.value=true

        val advantagePlan=storage.reference.child("advantage_plan.png")
        val basicPlan=storage.reference.child("basic_plan.png")
        val premiumPlan=storage.reference.child("premium_plan.png")

       //görevler tamamlanana kadar ui'ı bloklar
        runBlocking {
            // Log.d(TAG, "getImagesFromFirebase: $advantagePlan")
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

                viewModelScope.launch {
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


                                    //Log.d(TAG, "onBillingSetupFinished: ${productDetailsList.first().oneTimePurchaseOfferDetails.formattedPrice}")
                                    productDetailList.addAll(productDetailsList)
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


            }

        })
    }









    override fun onCleared() {
        billingClient.endConnection()
        super.onCleared()
    }

}