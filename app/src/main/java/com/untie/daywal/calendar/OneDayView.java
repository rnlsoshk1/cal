package com.untie.daywal.calendar;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.untie.daywal.MConfig;
import com.untie.daywal.R;
import com.untie.daywal.application.ApplicationController;
import com.untie.daywal.database.DbOpenHelper;
import com.untie.daywal.main.ItemData;

import java.util.Calendar;


public class OneDayView extends LinearLayout {

    Context context;
    public TextView dayTv;
    private TextView msgTv;
    private ImageView imageIv;
    public OneDayData one;
    DbOpenHelper mDbOpenHelper;

    public OneDayView(Context context) {
        super(context);
        this.context = context;
        init(context);
 
    }


    public OneDayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
 
    private void init(Context context)
    {
        mDbOpenHelper = ApplicationController.getInstance().mDbOpenHelper;

        View v = View.inflate(context, R.layout.oneday, this);
        
        dayTv = (TextView) v.findViewById(R.id.onday_dayTv);
        imageIv = (ImageView) v.findViewById(R.id.onday_imageIv);
        msgTv = (TextView) v.findViewById(R.id.onday_msgTv);
        one = new OneDayData();
        
    }
    

    public void setDay(int year, int month, int day) {
        this.one.calendar.set(year, month, day);
    }


    public void setDay(Calendar cal) {
        this.one.setDay((Calendar) cal.clone());
    }


    public void setDay(OneDayData one) {
        this.one = one;
    }
    

    public OneDayData getDay() {
        return one;
    }

    public void setMessage(String msg){
        one.setMessage(msg);
    }


    public CharSequence getMessage(){
        return  one.getMessage();
    }

    public int get(int field) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return one.get(field);
    }


    public void setImage(Uri imageUri) {
        this.one.setImageUri(imageUri);
    }

    public Uri getImage() {
        return this.one.getImageUri();
    }

    public void refresh() {
        
        //HLog.d(TAG, CLASS, "refresh");

        Calendar now = Calendar.getInstance();
        if ((now.get(Calendar.DAY_OF_MONTH) ==(one.calendar.get(Calendar.DAY_OF_MONTH))) &&
                (now.get(Calendar.MONTH) ==(one.calendar.get(Calendar.MONTH))) &&
                (now.get(Calendar.YEAR) ==(one.calendar.get(Calendar.YEAR)))) {
            Log.d("today","today");
            setBackgroundResource(R.drawable.today_day_cell_bg);
        }

        dayTv.setText(String.valueOf(one.get(Calendar.DAY_OF_MONTH)));




        if(one.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            dayTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_sunday));
        }
        else if(one.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            dayTv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_saturday));
        }
        else {
            dayTv.setTextColor(Color.BLACK);
        }





        ItemData itemData = mDbOpenHelper.getDayView(String.valueOf(one.get(Calendar.YEAR))+"-"
                          +String.valueOf(one.get(Calendar.MONTH)+1)+"-"
                          +String.valueOf(one.get(Calendar.DATE)));

        if (itemData != null) {
            if (itemData.getImage() != null) {
                Uri uri = Uri.parse(itemData.getImage());
                setImage(uri);
                imageIv.setVisibility(ImageView.VISIBLE);
                Glide.with(context)
                        .load(uri)
                        .centerCrop()
                        .into(imageIv);
            }
            msgTv.setVisibility(TextView.VISIBLE);
            msgTv.setText(itemData.getTitle());
        }
        
    }
    
}