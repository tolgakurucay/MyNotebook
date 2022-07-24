package com.tolgakurucay.mynotebook.utils;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

public class GetCurrentDate  {

    //constructor
    @Inject
    public GetCurrentDate(){}

    public Timestamp currentDate(){
       return Timestamp.now();
    }
    public Long currentDateAsLong(){return Timestamp.now().toDate().getTime();}
    public String getDateFromLong(Long date){
        Date date1 = new Date(date);
        SimpleDateFormat format= new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return format.format(date1);
    }


}
