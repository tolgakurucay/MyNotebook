package com.tolgakurucay.mynotebook.viewmodels.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CloudFragmentViewModel extends ViewModel {

    @Inject
    CloudFragmentViewModel() {

    }

    FirebaseAuth auth= FirebaseAuth.getInstance();
    FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    public MutableLiveData<QuerySnapshot> notesLiveData= new MutableLiveData<>();
    public MutableLiveData<Boolean> loading= new MutableLiveData<>();


    public void getFavorites(){
        loading.setValue(true);
        firestore.collection("Notes").document(auth.getCurrentUser().getUid()).collection("Notes").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                       notesLiveData.setValue(queryDocumentSnapshots);
                       loading.setValue(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    loading.setValue(false);
                    }
                });
    }




}
