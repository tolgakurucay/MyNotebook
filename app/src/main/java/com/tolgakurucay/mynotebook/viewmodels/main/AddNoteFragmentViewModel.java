package com.tolgakurucay.mynotebook.viewmodels.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

public class AddNoteFragmentViewModel extends ViewModel {

    public MutableLiveData<String> titleMessage=new MutableLiveData();
    public MutableLiveData<String> descriptionMessage=new MutableLiveData();


    public void verifyTitle(String title){
        if(Objects.equals(title, "")){
            titleMessage.setValue("empty");
        }
        else if(title.length()<3){
            titleMessage.setValue("atleast3characters");
        }
        else
        {
            titleMessage.setValue(null);
        }
    }

    public void verifyDescription(String description){
        if(description.equals("")){
            descriptionMessage.setValue("empty");
        }

        else
        {
            descriptionMessage.setValue(null);
        }
    }


}
