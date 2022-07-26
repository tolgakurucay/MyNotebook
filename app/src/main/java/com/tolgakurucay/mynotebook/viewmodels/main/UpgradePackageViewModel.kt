package com.tolgakurucay.mynotebook.viewmodels.main

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage

class UpgradePackageViewModel : ViewModel() {
val TAG="bilgi"

    val uriList=MutableLiveData<ArrayList<Uri>>()
    val loading=MutableLiveData<Boolean>()




    fun getImagesFromFirebase(storage:FirebaseStorage){

        loading.value=true
        val arrayList=ArrayList<Uri>()


        val advantagePlan=storage.reference.child("advantage_plan.png")
        val basicPlan=storage.reference.child("basic_plan.png")
        val premiumPlan=storage.reference.child("premium_plan.png")

        Log.d(TAG, "getImagesFromFirebase: $advantagePlan")
        advantagePlan.downloadUrl.addOnSuccessListener { advantagePlan->
            advantagePlan?.let {adv->
                arrayList.add(adv)
                basicPlan.downloadUrl.addOnSuccessListener { basicPlan->
                    basicPlan?.let { bsc->
                        arrayList.add(bsc)
                        premiumPlan.downloadUrl.addOnSuccessListener { premiumPlan->
                            premiumPlan?.let { prm->
                                arrayList.add(prm)
                                uriList.value=arrayList
                                loading.value=false
                            }
                        }.addOnFailureListener { loading.value=false }
                    }
                }.addOnFailureListener { loading.value=false }

            }
        }.addOnFailureListener { loading.value=false }
    }
}