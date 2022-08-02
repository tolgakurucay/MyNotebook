package com.tolgakurucay.mynotebook.viewmodels.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.ktx.FirebaseStorageKtxRegistrar;
import com.tolgakurucay.mynotebook.models.AlarmItem;
import com.tolgakurucay.mynotebook.models.NoteFavoritesModel;
import com.tolgakurucay.mynotebook.models.NoteModel;
import com.tolgakurucay.mynotebook.services.NoteDAO;
import com.tolgakurucay.mynotebook.services.NoteDatabase;

import java.util.ArrayList;
import java.util.List;

public class FeedFragmentViewModel extends ViewModel {


    public MutableLiveData<List<NoteModel>> noteList= new MutableLiveData<>();
    public MutableLiveData<Uri> uriLiveData= new MutableLiveData<>();
    private FirebaseAuth auth= FirebaseAuth.getInstance();
    private FirebaseFirestore firestore= FirebaseFirestore.getInstance();
    private FirebaseStorage storage= FirebaseStorage.getInstance();
    public MutableLiveData<String> firebaseMessage= new MutableLiveData<>();
    public MutableLiveData<Boolean> loading= new MutableLiveData<>();

    String TAG="bilgi";





    public void getAllNotes(Context context){
        loading.setValue(true);
        NoteDatabase db = new NoteDatabase() {
            @NonNull
            @Override
            public NoteDAO noteDao() {
                return null;
            }

            @NonNull
            @Override
            protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
                return null;
            }

            @NonNull
            @Override
            protected InvalidationTracker createInvalidationTracker() {
                return null;
            }

            @Override
            public void clearAllTables() {

            }
        }.getBookDatabase(context);


        if(db!=null){
            NoteDAO dao= db.noteDao();
           List<NoteModel> notes = dao.getAllNotes();
           noteList.setValue(notes);
            loading.setValue(false);
        }
        loading.setValue(false);

    }

    public void deleteNotes(Context context, ArrayList<NoteModel> noteList){
        loading.setValue(true);
        NoteDatabase db = new NoteDatabase() {
            @NonNull
            @Override
            public NoteDAO noteDao() {
                return null;
            }

            @NonNull
            @Override
            protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
                return null;
            }

            @NonNull
            @Override
            protected InvalidationTracker createInvalidationTracker() {
                return null;
            }

            @Override
            public void clearAllTables() {

            }
        }.getBookDatabase(context);

        if(db!=null){
            NoteDAO dao =db.noteDao();
            for(int i=0;i<noteList.size();i++){
                Log.d("bilgi", i+" "+noteList.get(i));
                dao.deleteNote(noteList.get(i));
                loading.setValue(false);
            }


        }
        loading.setValue(false);

    }

    public void addFavorites(Context context,ArrayList<NoteFavoritesModel> favoritesList){
        loading.setValue(true);
        NoteDatabase db= new NoteDatabase() {
            @NonNull
            @Override
            public NoteDAO noteDao() {
                return null;
            }

            @NonNull
            @Override
            protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
                return null;
            }

            @NonNull
            @Override
            protected InvalidationTracker createInvalidationTracker() {
                return null;
            }

            @Override
            public void clearAllTables() {

            }
        }.getBookDatabase(context);


        if(db!=null){

            NoteDAO dao=db.noteDao();

            for(int i=0;i<favoritesList.size();i++){

                dao.insertFavorites(favoritesList.get(i));
            }

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
            reference.child("backgrounds").child(user.getUid()).getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            uriLiveData.setValue(uri);
                            loading.setValue(false);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            uriLiveData.setValue(null);
                            loading.setValue(false);
                        }
                    });
        }


    }



    public void saveNoteToFirebase(ArrayList<NoteModel> notes,Context context){
        loading.setValue(true);
        firestore.collection("Right").document(auth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, "onSuccess: success girildi");
                        if (documentSnapshot!=null && documentSnapshot.exists()){
                            Log.d(TAG, "onSuccess: döküman var ve null değil");
                            DocumentReference reference=documentSnapshot.getReference();
                            Double myRight=documentSnapshot.getDouble("right");
                            Integer myRightAsInt= myRight.intValue();
                            if(notes.size()<=myRightAsInt){
                                for(int i=0;i<notes.size();i++){
                                    firestore.collection("Notes").document(auth.getCurrentUser().getUid()).collection("Notes").add(notes.get(i));
                                }
                                //success
                                firestore.document(reference.getPath()).update("right",(myRightAsInt-notes.size()))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        deleteNotes(context,notes);
                                                        firebaseMessage.setValue("success");
                                                        loading.setValue(false);

                                                    }
                                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                firebaseMessage.setValue(e.getLocalizedMessage());
                                                loading.setValue(false);
                                            }
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


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //null döndürelim hata niyetine
                        Log.d(TAG, "onFailure: "+e.getLocalizedMessage());
                        firebaseMessage.setValue(e.getLocalizedMessage());
                        loading.setValue(false);

                    }
                });
    }





}
