package com.tolgakurucay.mynotebook.models



data class PaymentHistory(val orderDate : com.google.firebase.Timestamp,var orderID:String,val price:String, val personUID:String,val packageId:String,val purchaseToken:String) {
}