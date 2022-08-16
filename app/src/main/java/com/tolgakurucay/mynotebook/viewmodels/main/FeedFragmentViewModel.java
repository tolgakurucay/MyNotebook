package com.tolgakurucay.mynotebook.viewmodels.main;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tolgakurucay.mynotebook.dependencyinjection.AppModule;
import com.tolgakurucay.mynotebook.models.NoteFavoritesModel;
import com.tolgakurucay.mynotebook.models.NoteModel;
import com.tolgakurucay.mynotebook.services.NoteDAO;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FeedFragmentViewModel extends ViewModel {

    @Inject
    public FeedFragmentViewModel(){}


    public MutableLiveData<List<NoteModel>> noteList= new MutableLiveData<>();
    public MutableLiveData<Uri> uriLiveData= new MutableLiveData<>();
    private FirebaseAuth auth= FirebaseAuth.getInstance();
    private FirebaseFirestore firestore= FirebaseFirestore.getInstance();
    private FirebaseStorage storage= FirebaseStorage.getInstance();
    public MutableLiveData<String> firebaseMessage= new MutableLiveData<>();
    public MutableLiveData<Boolean> loading= new MutableLiveData<>();
    public MutableLiveData<byte[]> byteArray= new MutableLiveData<>();

    String TAG="bilgi";





    public void getAllNotes(Context context){

        loading.setValue(true);
        NoteDAO dao= AppModule.INSTANCE.injectRoomDatabase(context).noteDao();
        List<NoteModel> notes=dao.getAllNotes();
        noteList.setValue(notes);
        loading.setValue(false);

    }

    public void deleteNotes(Context context, ArrayList<NoteModel> noteList){

        loading.setValue(true);

        NoteDAO dao=AppModule.INSTANCE.injectRoomDatabase(context).noteDao();
        for(int i=0;i<noteList.size();i++){
            dao.deleteNote(noteList.get(i));
        }
        loading.setValue(false);

    }

    public void addFavorites(Context context,ArrayList<NoteFavoritesModel> favoritesList){
        loading.setValue(true);
        NoteDAO dao=AppModule.INSTANCE.injectRoomDatabase(context).noteDao();
        for(int i=0;i<favoritesList.size();i++){

            dao.insertFavorites(favoritesList.get(i));
        }
        loading.setValue(false);
    }
    public void shareNote(String title, String description, Activity activity){
        loading.setValue(true);

            Intent shareIntent=new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,title+"\n\n"+description);
            activity.startActivity(Intent.createChooser(shareIntent,title));

        loading.setValue(false);

    }

    public void getPPfromStorage(){
        loading.setValue(true);
       FirebaseAuth auth=FirebaseAuth.getInstance();
       FirebaseStorage storage= FirebaseStorage.getInstance();
        FirebaseUser user=auth.getCurrentUser();

       if(user!=null){
            StorageReference reference = storage.getReference();
            final long ONE_MEGABYTE= 1024 * 1024;
            reference.child("backgrounds").child(user.getUid()).getBytes(ONE_MEGABYTE)
                    .addOnSuccessListener(bytes -> {
                        byteArray.setValue(bytes);
                        loading.setValue(false);
                    })
                    .addOnFailureListener(e -> {
                        byteArray.setValue(null);
                        loading.setValue(false);

                    });
        }


    }




    public void saveNoteToFirebase(ArrayList<NoteModel> notes,Context context){
        loading.setValue(true);
        firestore.collection("Right").document(auth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d(TAG, "onSuccess: success girildi");
                    if (documentSnapshot!=null && documentSnapshot.exists()){
                        Log.d(TAG, "onSuccess: döküman var ve null değil");
                        DocumentReference reference=documentSnapshot.getReference();
                        Double myRight=documentSnapshot.getDouble("right");
                        assert myRight != null;
                        int myRightAsInt= myRight.intValue();
                        if(notes.size()<=myRightAsInt){
                            for(int i=0;i<notes.size();i++){
                                firestore.collection("Notes").document(auth.getCurrentUser().getUid()).collection("Notes").add(notes.get(i));
                            }
                            //success
                            firestore.document(reference.getPath()).update("right",(myRightAsInt-notes.size()))
                                            .addOnSuccessListener(unused -> {
                                                deleteNotes(context,notes);
                                                firebaseMessage.setValue("success");
                                                loading.setValue(false);

                                            })
                                    .addOnFailureListener(e -> {

                                        firebaseMessage.setValue(e.getLocalizedMessage());
                                        loading.setValue(false);
                                    });


                        }
                        else
                        {
                            //hak yok
                            firebaseMessage.setValue("noright");
                            loading.setValue(false);
                        }
                        Log.d(TAG, "hakkım "+myRight);
                    }
                    else
                    {
                        loading.setValue(false);
                        firebaseMessage.setValue("noright");
                    }


                })
                .addOnFailureListener(e -> {
                    //null döndürelim hata niyetine
                    Log.d(TAG, "onFailure: "+e.getLocalizedMessage());
                    firebaseMessage.setValue(e.getLocalizedMessage());
                    loading.setValue(false);

                });
    }





}
