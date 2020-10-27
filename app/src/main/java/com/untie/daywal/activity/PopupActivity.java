package com.untie.daywal.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.untie.daywal.R;
import com.untie.daywal.application.ApplicationController;
import com.untie.daywal.database.DbOpenHelper;
import com.untie.daywal.main.ItemData;
import com.untie.daywal.main.SwipeHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PopupActivity extends Activity {

    /** order = Recyclerview Position값 */

    @BindView(R.id.rv_popup)
    RecyclerView rv_popup;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.addButton)
    ImageButton addButton;
    @BindView(R.id.backButton)
    ImageButton backButton;
    int year;
    int month;
    int day;
    int week;
    String dayOfWeek;
    DbOpenHelper mDbOpenHelper;
    LinearLayoutManager mLayoutManager;
    PopupAdapter adapter;
    ArrayList<ItemData> itemDatas = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 1.0f;
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.activity_popup);


        // title = (TextView) findViewById(R.id.date);
        // addButton = (ImageButton) findViewById(R.id.addButton);
        ButterKnife.bind(this);
        viewTitle();



        mDbOpenHelper = ApplicationController.getInstance().mDbOpenHelper;

        itemDatas = new ArrayList<ItemData>();
        itemDatas =  mDbOpenHelper.DbPopupActivity(String.valueOf(year)+"-"+String.valueOf(month)+"-"+String.valueOf(day));
        rv_popup.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_popup.setLayoutManager(mLayoutManager);

        adapter = new PopupAdapter(this,itemDatas,clickEvent);
        rv_popup.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SwipeHelper(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(rv_popup);


    }

    @OnClick(R.id.backButton)
    public void moveMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("year",year);
        intent.putExtra("month",month-1);
        intent.putExtra("order",0);
        ////intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    /** +버튼 , back 버튼 누를시 star radio button 디비에 저장 */
    /** 새글 작성 (+버튼 클릭) 을 누르면 디비에 저장  */
    @OnClick(R.id.addButton)
    public void moveEditPage() {
        /** adapter.getItemDatas() */
        ArrayList<ItemData> list = adapter.getItemDatas();


        int newOrder = rv_popup.getChildCount() + 1;
        Intent intent = new Intent(PopupActivity.this, CreateActivity.class);
        intent.putExtra("order",newOrder);
        intent.putExtra("year",year);
        intent.putExtra("month",month);
        intent.putExtra("day",day);
        intent.putExtra("week",week);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    /** item 항목 클릭했을 시 */
    public View.OnClickListener clickEvent = new View.OnClickListener() {
        public void onClick(View v) {
            int itemPosition = rv_popup.getChildAdapterPosition(v);
            Intent intent = new Intent(PopupActivity.this, EditActivity.class);
            intent.putExtra("year",year);
            intent.putExtra("month",month);
            intent.putExtra("day",day);
            intent.putExtra("week",week);

            ItemData itemData = new ItemData();
            itemData.setId((itemDatas.get(itemPosition).getId()));
            itemData.setDate(itemDatas.get(itemPosition).getDate());
            itemData.setContent(itemDatas.get(itemPosition).getContent());
            itemData.setImage(itemDatas.get(itemPosition).getImage());
            itemData.setTitle(itemDatas.get(itemPosition).getTitle());
            intent.putExtra("itemData",itemData);

            startActivity(intent);
            overridePendingTransition(0, 0);


            /** flag? */
        }
    };



    private void viewTitle() {
        Intent intent = getIntent();
        year = intent.getExtras().getInt("year");
        month = intent.getExtras().getInt("month") + 1;
        day = intent.getExtras().getInt("day");
        week = intent.getExtras().getInt("week");
        dayOfWeek = getDayOfWeek(week);

        title.setText(String.valueOf(month) + "월 " + String.valueOf(day) + "일 " + dayOfWeek);
    }




    private String getDayOfWeek(int value) {
        String day = "";
        switch (value) {
            case 1:
                day = "일요일";
                break;
            case 2:
                day = "월요일";
                break;
            case 3:
                day = "화요일";
                break;
            case 4:
                day = "수요일";
                break;
            case 5:
                day = "목요일";
                break;
            case 6:
                day = "금요일";
                break;
            case 7:
                day = "토요일";
                break;
        }
        return day;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("year",year);
        intent.putExtra("month",month-1);
        intent.putExtra("order",0);
        ////intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}