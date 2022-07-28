package com.tolgakurucay.mynotebook.viewmodels.payment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Index
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tolgakurucay.mynotebook.models.OrderHistory
import kotlinx.coroutines.launch

class OrderHistoryViewModel : ViewModel() {

    val auth=FirebaseAuth.getInstance()
    val firestore=FirebaseFirestore.getInstance()
    val orderListLive=MutableLiveData<ArrayList<OrderHistory>>()
    val TAG="bilgi"






    fun getOrderList(){

        viewModelScope.launch {
            val orderList=ArrayList<OrderHistory>()
            firestore.collection("PaymentHistory").whereEqualTo("personUID",auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    if(it!=null){
                        for(history in it){
                            Log.d(TAG, "getOrderList: for başladı")
                            val date=history.getDate("orderDate")
                            val price=history.getString("price")
                            val packageName=history.getString("packageName")
                            if(date!=null && price!=null && packageName!=null){
                                val obj=OrderHistory(packageName,date,price)
                                orderList.add(obj)

                            }
                            else
                            {

                            }
                        }
                        orderListLive.value=orderList
                        Log.d(TAG, "getOrderList: for bitti")
                        Log.d(TAG, "getOrderList: dizimiz $orderList")
                    }
                    else
                    {

                    }
                }

        }

    }
}