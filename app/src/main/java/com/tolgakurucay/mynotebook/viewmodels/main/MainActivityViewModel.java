package com.tolgakurucay.mynotebook.viewmodels.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainActivityViewModel extends ViewModel {

    @Inject
    public MainActivityViewModel(){};

    public MutableLiveData<Boolean> isAgreed = new MutableLiveData<>();

    public void checkPolicyAgreement(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        firestore.collection("PrivacyPolicy").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Boolean isPolicyAgreed = documentSnapshot.getBoolean("isPolicyAgreed");
                    if(isPolicyAgreed!=null && isPolicyAgreed){
                        isAgreed.setValue(true);
                    }
                    else
                    {
                        isAgreed.setValue(false);
                    }


                });

    }


}
