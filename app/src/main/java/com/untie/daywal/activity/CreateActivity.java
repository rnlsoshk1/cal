package com.untie.daywal.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.untie.daywal.R;
import com.untie.daywal.application.ApplicationController;
import com.untie.daywal.database.DbOpenHelper;
import com.untie.daywal.main.ItemData;
import com.untie.daywal.ui.activity.DrawActivity;

import java.util.ArrayList;

public class CreateActivity extends AppCompatActivity {

    public static final String SHARED_PREF_FIRST_USER_KEY = "1000";

    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.day_title)
    TextView day_title;
    @BindView(R.id.ll_camera_btn)
    LinearLayout cameraButton;
    @BindView(R.id.ll_gallery_btn)
    LinearLayout galleryButton;
    @BindView(R.id.ll_draw_btn)
    LinearLayout drawingButton;
    @BindView(R.id.iv_image)
    ImageView ivImage;
    @BindView(R.id.ib_back)
    ImageButton backButton;
    @BindView(R.id.ib_check)
    ImageButton checkButton;
    @BindView(R.id.changeSizeBtn)
    Button changeSizeBtn;

    DbOpenHelper mDbOpenHelper;

    static int CHANGE_SIZE;
    static final int REQUEST_IMAGE_CAPTURE = 1000;
    static final int REQUEST_GALLERY = 3000;
    static final int REQUEST_DRAW = 5000;
    int year;
    int month;
    int day;
    int week;
    int order;
    String dayOfWeek;
    String imagePath;

    ArrayList<ItemData> itemDatas = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        ButterKnife.bind(this);
        mDbOpenHelper = ApplicationController.getInstance().mDbOpenHelper;

        viewTitle();
        init();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        int firstUser = sharedPref.getInt(SHARED_PREF_FIRST_USER_KEY,-1);

        if (firstUser == -1) {
            Toast toast = Toast.makeText(this,"카메라는 기본카메라만 사용할 수 있습니다",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            saveUserIsNotFirst();
        }





    }

    private void init() {
        itemDatas = new ArrayList<ItemData>();
        itemDatas =  mDbOpenHelper.DbPopupActivity(String.valueOf(year)+"-"+String.valueOf(month)+"-"+ String.valueOf(day));


    }

    private void viewTitle() {
        Intent intent = getIntent();
        year = intent.getExtras().getInt("year");
        month = intent.getExtras().getInt("month");
        day = intent.getExtras().getInt("day");
        order = intent.getExtras().getInt("order");
        week = intent.getExtras().getInt("week");
        dayOfWeek = getDayOfWeek(week);
        day_title.setText(String.valueOf(month) + "월 " + String.valueOf(day) + "일 " + dayOfWeek);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("title",etTitle.getText().toString());
        outState.putString("content",etContent.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            etTitle.setText(savedInstanceState.getString("title"));
            etContent.setText(savedInstanceState.getString("content"));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @OnClick(R.id.ib_back)
    public void back() {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("year",year);
        intent.putExtra("month",month-1);
        intent.putExtra("order",0);
        ////intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);


    }

    @OnClick(R.id.ib_check)
    public void save() {
        /** 데이터베이스 저장 */
        // TODO : 날짜로 검색해서 레코드의 개수가 0이면 isChecked=true
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        String date = String.valueOf(year)+"-"+String.valueOf(month) + "-" + String.valueOf(day);
        int count = mDbOpenHelper.getCountByDate(date);



        ItemData data = new ItemData();
        data.title = title;
        data.content = content;
        data.date = date;
        data.image = imagePath;
        if(count == 0) {
            data.isChecked = true;
        } else {
            data.isChecked = false;
        }

        data.id = mDbOpenHelper.getMaxId() + 1;

        mDbOpenHelper.DbInsert(data);
        Toast toast = Toast.makeText(getApplicationContext(),"저장 완료",Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,200);
        toast.show();

        /** Edit Activity로 넘어가기 */
        Intent intent = new Intent(CreateActivity.this, EditActivity.class);
        intent.putExtra("year",year);
        intent.putExtra("month",month);
        intent.putExtra("day",day);
        intent.putExtra("week",week);
        intent.putExtra("itemData",data);


        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();


    }


    @OnClick(R.id.ll_camera_btn)
    public void moveCamera() {
        int permissionCheck = ContextCompat.checkSelfPermission(CreateActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            startCameraActivity();
        } else {
            ActivityCompat.requestPermissions(CreateActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    2000);
           /* Toast toast = Toast.makeText(getApplicationContext(),"권한을 거부하면 사진을 저장할 수 없습니다",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,200);
            toast.show();*/
        }
    }




    @OnClick(R.id.ll_gallery_btn)
    public void moveGallery() {
        int permissionCheck = ContextCompat.checkSelfPermission(CreateActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            startGalleryActivity();
        } else {
            ActivityCompat.requestPermissions(CreateActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    3000);
            /*Toast toast = Toast.makeText(getApplicationContext(),"권한을 거부하면 사진을 저장할 수 없습니다",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,200);
            toast.show();*/

        }
    }

    // TODO : 메모 구현
    @OnClick(R.id.ll_draw_btn)
    public void moveDrawActivity() {

        int permissionCheck = ContextCompat.checkSelfPermission(CreateActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(CreateActivity.this, DrawActivity.class);
            startActivityForResult(intent, REQUEST_DRAW);
            overridePendingTransition(0, 0);
        } else {
            ActivityCompat.requestPermissions(CreateActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    4000);
            /*Toast toast = Toast.makeText(getApplicationContext(),"권한을 거부하면 사진을 저장할 수 없습니다",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,200);
            toast.show();*/

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2000 && grantResults.length > 0) {

            startCameraActivity();
        } else if (requestCode == 3000 && grantResults.length > 0) {
            startGalleryActivity();
        } else if (requestCode == 4000 && grantResults.length > 0) {
            Intent intent = new Intent(CreateActivity.this, DrawActivity.class);
            startActivityForResult(intent, REQUEST_DRAW);
            overridePendingTransition(0, 0);
        }
    }
    private void startGalleryActivity() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(galleryIntent, REQUEST_GALLERY);

    }

    private void startCameraActivity() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

        }
    }

    // TODO : 손노트 , 다른 카메라 어플 인텐트 기능 추가

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /** 사진 받아오기 코드 */
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {





            //
            Uri photoUri = data.getData();

            DisplayMetrics dm = getResources().getDisplayMetrics();
            int height = Math.round(240 * dm.density);

            ivImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height));
            changeSizeBtn.setText("+");
            CHANGE_SIZE = 1;


            ivImage.setVisibility(ImageView.VISIBLE);
            imagePath ="content://media"+photoUri.getPath();
            Glide.with(this)
                    .load(photoUri)
                    .into(ivImage);
            changeSizeBtn.setVisibility(Button.VISIBLE);


        }
        /** 갤러리 받아오기 코드*/
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            Uri photoUri = data.getData();

            DisplayMetrics dm = getResources().getDisplayMetrics();
            int height = Math.round(240 * dm.density);

            ivImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height));
            changeSizeBtn.setText("+");
            CHANGE_SIZE = 1;


            ivImage.setVisibility(ImageView.VISIBLE);
            Glide.with(this)
                    .load(photoUri)
                    .into(ivImage);
            imagePath ="content://media"+photoUri.getPath();
            changeSizeBtn.setVisibility(Button.VISIBLE);

        }


        /** 그림 받아오기 코드*/
        if (requestCode == REQUEST_DRAW && resultCode == RESULT_OK) {

            String uriPath = data.getStringExtra("uriPath");
            Uri uri = Uri.parse(uriPath);

            DisplayMetrics dm = getResources().getDisplayMetrics();
            int height = Math.round(240 * dm.density);

            ivImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height));
            changeSizeBtn.setText("+");
            CHANGE_SIZE = 1;
            ivImage.setVisibility(ImageView.VISIBLE);
            Glide.with(this)
                    .load(uri)
                    .into(ivImage);
            imagePath = uriPath;
            changeSizeBtn.setVisibility(Button.VISIBLE);





        }
    }

    //1 : 현재 축소상태 누르면 확대하기 -  0 : 현재 확대상태 누르면 축소하기
    @OnClick(R.id.changeSizeBtn)
    public void changeSize() {
        if (CHANGE_SIZE == 1) {
            ivImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            changeSizeBtn.setText("-");
            CHANGE_SIZE = 0;
        } else if (CHANGE_SIZE == 0) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int height = Math.round(240 * dm.density);

            ivImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height));
            changeSizeBtn.setText("+");
            CHANGE_SIZE = 1;
        }

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

    private void saveUserIsNotFirst() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(SHARED_PREF_FIRST_USER_KEY, 1);
        editor.commit();

    }

}
