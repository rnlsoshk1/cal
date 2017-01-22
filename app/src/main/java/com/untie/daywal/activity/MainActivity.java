/*
* Copyright (C) 2015 Hansoo Lab.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package com.untie.daywal.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.untie.daywal.HLog;
import com.untie.daywal.MConfig;
import com.untie.daywal.calendar.MonthlyFragment;
import com.untie.daywal.R;
import com.untie.daywal.calendar.OneDayView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends FragmentActivity {

    private static final String TAG = MConfig.TAG;
    private static final String NAME = "MainActivity";
    private final String CLASS = NAME + "@" + Integer.toHexString(hashCode());

    private TextView thisMonthTv;

    @BindView(R.id.today_tv)
    TextView today_tv;

    int order;
    int year;
    int month;
    TextView dialog_year_title;
    TextView dialog_month_title;
    int pickedYear;
    int pickedMonth;
    //Back 키 두번 클릭 여부 확인
    private final long FINSH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    MonthlyFragment mf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        thisMonthTv = (TextView) findViewById(R.id.this_month_tv);

        Calendar now = Calendar.getInstance();

        Intent intent = getIntent();
        year = intent.getIntExtra("year",now.get(Calendar.YEAR));
        month = intent.getIntExtra("month",now.get(Calendar.MONTH));
        order = intent.getIntExtra("order",0);

        if ( order == 0) {
            mf = MonthlyFragment.newInstance(year, month);
            getSupportFragmentManager().beginTransaction().add(R.id.monthly, mf).commit();
        } else if (order == 1) {
            mf = MonthlyFragment.newInstance(year, month-1);
            getSupportFragmentManager().beginTransaction().replace(R.id.monthly, mf).commit();
        }
        mf.setOnMonthChangeListener(new MonthlyFragment.OnMonthChangeListener() {
            
            @Override
            public void onChange(int year, int month) {
                HLog.d(TAG, CLASS, "onChange " + year + "." + month);
                thisMonthTv.setText(year + "년 " + (month + 1)+"월");

            }

            @Override
            public void onDayClick(OneDayView dayView) {
                int year = dayView.get(Calendar.YEAR);
                int month = dayView.get(Calendar.MONTH);
                int day = dayView.get(Calendar.DAY_OF_MONTH);
                int week = dayView.get(Calendar.DAY_OF_WEEK);



                Intent intent = new Intent(MainActivity.this, PopupActivity.class);
                intent.putExtra("year",year);
                intent.putExtra("month",month);
                intent.putExtra("day",day);
                intent.putExtra("week",week);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                //overridePendingTransition(0, 0);
            }

            @Override
            public void onDayLongClick(OneDayView dayView) {

                if (dayView.getImage() != null) {
                    final Dialog dayPickerDialog = new Dialog(MainActivity.this);
                    dayPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dayPickerDialog.setContentView(R.layout.dialog_image);
                    dayPickerDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    dayPickerDialog.getWindow().setBackgroundDrawable((new ColorDrawable(0x7000000)));
                    ImageView imageView = (ImageView) dayPickerDialog.findViewById(R.id.image_popup);
                    Uri uri = dayView.getImage();
                    Glide.with(MainActivity.this)
                            .load(uri)
                            .centerCrop()
                            .into(imageView);
                    dayPickerDialog.show();
                    //dayPickerDialog.dismiss();
                }
            }
        });

        thisMonthTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDayPicker();
            }
        });

    }


    private void showDayPicker() {

            Calendar calender = Calendar.getInstance();
            pickedYear = calender.get(Calendar.YEAR);
            pickedMonth = calender.get(Calendar.MONTH)+1;
            final Dialog dayPickerDialog = new Dialog(this);
            dayPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dayPickerDialog.setContentView(R.layout.dialog);
            Button okBtn = (Button) dayPickerDialog.findViewById(R.id.birthday_btn_ok);
            Button cancelBtn = (Button) dayPickerDialog.findViewById(R.id.birthday_btn_cancel);
            dialog_year_title = (TextView) dayPickerDialog.findViewById(R.id.dialog_year_title);
            dialog_month_title = (TextView) dayPickerDialog.findViewById(R.id.dialog_month_title);
            final NumberPicker yearPicker = (NumberPicker) dayPickerDialog.findViewById(R.id.yearPicker);
            final NumberPicker monthPicker = (NumberPicker) dayPickerDialog.findViewById(R.id.monthPicker);

            dialog_year_title.setText(String.valueOf(pickedYear)+"년");
            dialog_month_title.setText(String.valueOf(pickedMonth)+"월");
            yearPicker.setMinValue(pickedYear - 100);
            yearPicker.setMaxValue(pickedYear + 40);
            yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            //   setDividerColor(yearPicker, android.R.color.white );
            yearPicker.setWrapSelectorWheel(false);
            yearPicker.setValue(pickedYear);
            yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    dialog_year_title.setText(String.valueOf(newVal)+"년");
                }
            });

            monthPicker.setMinValue(1);
            monthPicker.setMaxValue(12);
            monthPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            //   setDividerColor(monthPicker, android.R.color.white);
            //monthPicker.setWrapSelectorWheel(false);
            monthPicker.setValue(pickedMonth);
            monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    dialog_month_title.setText(String.valueOf(newVal)+"월");
                }
            });

            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO : 날짜로 이동 yearPicker.getValue() monthPicker.getValue()

                    //Toast.makeText(MainActivity.this, String.valueOf(yearPicker.getValue())+"/"+monthPicker.getValue(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("year",yearPicker.getValue());
                    intent.putExtra("month",monthPicker.getValue());
                    intent.putExtra("order",1);
                    dayPickerDialog.dismiss();
                    startActivity(intent);
                    overridePendingTransition(0, 0);

                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dayPickerDialog.dismiss();
                }
            });
            dayPickerDialog.show();

    }


    @Override
    public void onBackPressed() {

        long tempTime        = System.currentTimeMillis();
        long intervalTime    = tempTime - backPressedTime;

        /**
         * Back키 두번 연속 클릭 시 앱 종료
         */

        if ( 0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime ) {
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(),"뒤로 가기 키을 한번 더 누르시면 종료됩니다.",Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.today_tv)
    public void moveToday() {

        Calendar now = Calendar.getInstance();

        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.putExtra("year",now.get(Calendar.YEAR));
        intent.putExtra("month",now.get(Calendar.MONTH)+1);
        intent.putExtra("order",1);
        startActivity(intent);
        overridePendingTransition(0, 0);

    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportFragmentManager().beginTransaction().remove(mf).commit();
    }*/
}
