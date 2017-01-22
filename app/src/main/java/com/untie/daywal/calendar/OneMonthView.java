package com.untie.daywal.calendar;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.untie.daywal.HLog;
import com.untie.daywal.MConfig;
import com.untie.daywal.R;

import java.util.ArrayList;
import java.util.Calendar;


public class OneMonthView extends LinearLayout implements View.OnClickListener, View.OnLongClickListener {




    public interface OnClickDayListener {
        void onClick(OneDayView odv);
    }

    public interface OnLongClickDayListener {
        void onLongClick(OneDayView odv);
    }


    private int mYear;
    private int mMonth;
    private ArrayList<LinearLayout> weeks = null;
    private ArrayList<OneDayView> dayViews = null;
    private OnClickDayListener onClickDayListener;
    private OnLongClickDayListener onLongClickDayListener;
    private final OnClickDayListener dummyClickDayListener = new OnClickDayListener() {
        @Override
        public void onClick(OneDayView odv) {
        }
    };

    private final OnLongClickDayListener dummyLongClickDayListener = new OnLongClickDayListener() {
        @Override
        public void onLongClick(OneDayView odv) {
        }
    };



    public void setOnClickDayListener(OnClickDayListener onClickDayListener) {
        if (onClickDayListener != null) {
            this.onClickDayListener = onClickDayListener;
        }
        else {
            this.onClickDayListener = dummyClickDayListener;
        }
    }

    public void setOnLongClickDayListener(OnLongClickDayListener onLongClickDayListener) {
        if (onLongClickDayListener != null) {
            this.onLongClickDayListener = onLongClickDayListener;
        } else {
            this.onLongClickDayListener =  dummyLongClickDayListener;
        }

    }

    public OneMonthView(Context context) {
        this(context, null);
    }

    public OneMonthView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OneMonthView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setOrientation(LinearLayout.VERTICAL);
        onClickDayListener = dummyClickDayListener;

        //뷰를 미리 넉넉한 만큼 만들어 놓는다.
        if(weeks == null) {

            weeks = new ArrayList<>(6); //한달에 최대 6주
            dayViews = new ArrayList<>(42); // 7 days * 6 weeks = 42 days

            LinearLayout ll = null;
            for(int i=0; i<42; i++) {

                if(i % 7 == 0) {
                    ll = new LinearLayout(context);
                    LinearLayout.LayoutParams params
                            = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                    params.weight = 1;
                    ll.setOrientation(LinearLayout.HORIZONTAL);
                    ll.setLayoutParams(params);
                    ll.setWeightSum(7);

                    weeks.add(ll);
                }

                LinearLayout.LayoutParams params
                        = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                params.weight = 1;

                OneDayView ov = new OneDayView(context);
                ov.setLayoutParams(params);
                ov.setOnClickListener(this);

                ll.addView(ov);
                dayViews.add(ov);
            }
        }



        if(isInEditMode()) {
            Calendar cal = Calendar.getInstance();
            make(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
        }

    }

    /**
     * Get current year
     * @return 4 digits number of year
     */
    public int getYear() {
        return mYear;
    }

    /**
     * Get current month
     * @return 0~11 (Calendar.JANUARY ~ Calendar.DECEMBER)
     */
    public int getMonth() {
        return mMonth;
    }


    /**
     * Any layout manager that doesn't scroll will want this.
     */
    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }


    /**
     * Make a Month view
     * @param year year of this month view (4 digits number)
     * @param month month of this month view (0~11)
     */
    public void make(int year, int month)
    {
        if(mYear == year && mMonth == month) {
            return;
        }
        
        long makeTime = System.currentTimeMillis();
        
        this.mYear = year;
        this.mMonth = month;

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);//일요일을 주의 시작일로 지정
        
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);//1일의 요일
        int maxOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);//마지막 일수
        ArrayList<OneDayData> oneDayDataList = new ArrayList<>();
        
        calendar.add(Calendar.DAY_OF_MONTH, Calendar.SUNDAY - dayOfWeek);//주의 첫 일로 이동
        //HLog.d(TAG, CLASS, "first day : " + calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.KOREA) + " / " + calendar.get(Calendar.DAY_OF_MONTH));

        /* add previous month */
        int seekDay;
        while(true) {
            seekDay = calendar.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek == seekDay) break;
            
            OneDayData one = new OneDayData();
            one.setDay(calendar);
            oneDayDataList.add(one);
            //하루 증가
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        //HLog.d(TAG, CLASS, "this month : " + calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.KOREA) + " / " + calendar.get(Calendar.DAY_OF_MONTH));
        /* add this month */
        for(int i=0; i < maxOfMonth; i++) {
            OneDayData one = new OneDayData();
            one.setDay(calendar);
            oneDayDataList.add(one);
            //add one day
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        /* add next month */
        while(true){
            if(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                OneDayData one = new OneDayData();
                one.setDay(calendar);
                oneDayDataList.add(one);
            } 
            else {
                break;
            }
            //add one day
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        if(oneDayDataList.size() == 0) return;

        //Remove all day-views
        this.removeAllViews();
        
        int count = 0;
        for(OneDayData one : oneDayDataList) {
            
            if(count % 7 == 0) {
                addView(weeks.get(count / 7));
            }
            OneDayView ov = dayViews.get(count);
            ov.setDay(one);
            ov.setMessage("");
            /**ov.setImmage()*/

            /*
            Calendar now = Calendar.getInstance();
            if (now.equals(ov.one.calendar)) {
                ov.setBackgroundResource(R.drawable.today_day_cell_bg);
            }
            */



            ov.refresh();

            count++;
        }

        //Set the weight-sum of LinearLayout to week counts
        this.setWeightSum(getChildCount());


    }


    protected String doubleString(int value) {

        String temp;
 
        if(value < 10){
            temp = "0"+ String.valueOf(value);
             
        }else {
            temp = String.valueOf(value);
        }
        return temp;
    }
 
    @Override
    public void onClick(View v) {

        OneDayView odv = (OneDayView) v;
        this.onClickDayListener.onClick(odv);

    }

    @Override
    public boolean onLongClick(View v) {
        OneDayView odv = (OneDayView) v;
        this.onLongClickDayListener.onLongClick(odv);

        return true;
    }

}