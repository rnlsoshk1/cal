package com.untie.daywal.calendar;


import android.net.Uri;

import java.util.Calendar;


public class OneDayData {

    Calendar calendar;
    Uri imageUri;
    private CharSequence msg = "";

    public OneDayData() {
        this.calendar = Calendar.getInstance();
    }

    public void setDay(int year, int month, int day) {
        calendar = Calendar.getInstance();
        calendar.set(year, month, day);
    }


    public void setDay(Calendar cal) {
        this.calendar = (Calendar) cal.clone();
    }


    public Calendar getDay() {
        return calendar;
    }

    public int get(int field) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return calendar.get(field);
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }


    public Uri getImageUri() {
        return imageUri;
    }

    public CharSequence getMessage() {
        return msg;
    }


    public void setMessage(CharSequence msg) {
        this.msg = msg;
    }
}