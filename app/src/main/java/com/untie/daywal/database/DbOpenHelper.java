package com.untie.daywal.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.untie.daywal.main.ItemData;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * DB에 대한 함수가 정의된 곳
 *
 */
public class DbOpenHelper {

    private static final String DATABASE_NAME = "memolist.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private ArrayList<ItemData> itemDatas = null;

    private class DatabaseHelper extends SQLiteOpenHelper {

        // 생성자
        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // 최초 DB를 만들때 한번만 호출된다.
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DataBases.CreateDB._CREATE);

        }

        // 버전이 업데이트 되었을 경우 DB를 다시 만들어 준다.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+ DataBases.CreateDB._TABLENAME);
            onCreate(db);
        }
    }

    public DbOpenHelper(Context context){
        this.mCtx = context;
    }

    public DbOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }
    /**
     * DB에 데이터 추가
    */
    public void DbInsert(ItemData itemData){

        mDB = mDBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("date",itemData.date);
        values.put("title",itemData.title);
        values.put("content",itemData.content);
        values.put("image",itemData.image);
        values.put("isChecked",String.valueOf(itemData.isChecked));

        mDB.insert("memoinfo",null,values);

    }



    /** DB항목 업그레이드 - radiobuton 수정할 때 사용 */
     public boolean DbUpdateRadio(int id,Boolean isChecked){

     ContentValues values = new ContentValues();
     values.put("isChecked",String.valueOf(isChecked));


     int result = mDB.update("memoinfo", values, "_id=?", new String[]{String.valueOf(id)});
        if(result > 0) {
            return true;
        }

         return false;
     }



    /**
     * DB항목 업그레이드2 - EditText 에서 수정할 때 사용
     */
    public void DbUpdate(ItemData itemData){

        mDB = mDBHelper.getWritableDatabase();
        int id = itemData.getId();
        ContentValues values = new ContentValues();
        values.put("title",itemData.getTitle());
        values.put("content",itemData.getContent());
        values.put("image",itemData.getImage());

        mDB.update("memoinfo", values, "_id=?", new String[]{String.valueOf(id)});

    }


    public int getCountByDate(String mDate) {
        int count = 0;
        String sql = "select _id from memoinfo where date="+"'"+mDate+"'";
        Cursor cursor = mDB.rawQuery(sql,null);
        count = cursor.getCount();
        return count;
    }

    public ItemData getDayView(String mDate) {
        SQLiteDatabase mDB;
        mDB = mDBHelper.getReadableDatabase();
        String sql = "select title, image from memoinfo where date="+"'"+mDate+"' and isChecked='true'";
        Cursor cursor = mDB.rawQuery(sql,null);
        int count = cursor.getCount();
        if (count == 0) {
            return null;
        } else {
            cursor.moveToNext();

            String title = cursor.getString(cursor.getColumnIndex("title"));
            String image = cursor.getString(cursor.getColumnIndex("image"));
            ItemData itemData = new ItemData();
            itemData.title = title;
            itemData.image = image;
            return itemData;
        }
    }


    /**
     * 항목 삭제하는 함수
     */
    public boolean DbDelete(int id) {
        try {
            int result = mDB.delete("memoinfo", "_id=?", new String[]{String.valueOf(id)});
            if(result > 0){
                return true;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    public int getMaxId() {

        mDB = mDBHelper.getReadableDatabase();
        final SQLiteStatement stmt = mDB
                .compileStatement("select MAX(_id) from memoinfo");

        return (int) stmt.simpleQueryForLong();
    }

    /** 전체 목록 recyclerview에 뿌릴 데이터 정렬 */
    public ArrayList<ItemData> DbMainSelect(){
        SQLiteDatabase getDb;
        getDb = mDBHelper.getReadableDatabase();
        Cursor c = getDb.rawQuery( "select * from memoinfo" , null);

        itemDatas = new ArrayList<ItemData>();
//
//        Log.i("myTag" , "갯수 : " + String.valueOf(c.getCount()));

        while(c.moveToNext()){
            String date = c.getString(c.getColumnIndex("date"));
            int order = c.getInt(c.getColumnIndex("order"));
            String title = c.getString(c.getColumnIndex("title"));
            String content = c.getString(c.getColumnIndex("content"));
            String image = c.getString(c.getColumnIndex("image"));


            ItemData listViewItem = new ItemData();

            listViewItem.title = title;
            listViewItem.content = content;
            listViewItem.image = image;
            listViewItem.date = date;


            itemDatas.add(listViewItem);

        }

        return itemDatas;
    }


    /**팝업 recyclerview (PopupActivity), EditActivity 뿌릴 데이터*/
    public ArrayList<ItemData> DbPopupActivity(String mDate){
        SQLiteDatabase getDb;
        getDb = mDBHelper.getReadableDatabase();
        String sql = "select * from memoinfo where date="+"'"+mDate+"'";
        Log.d("1dbPopupActivity",sql);
        Cursor cursor = getDb.rawQuery(sql , null);


        itemDatas = new ArrayList<ItemData>();
//
//        Log.i("myTag" , "갯수 : " + String.valueOf(cursor.getCount()));

        Log.i("myTag" , "갯수 : " + String.valueOf(cursor.getCount()));
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String image = cursor.getString(cursor.getColumnIndex("image"));
                String isChecked = cursor.getString(cursor.getColumnIndex("isChecked"));

                ItemData listViewItem = new ItemData();

                listViewItem.id = _id;
                listViewItem.title = title;
                listViewItem.content = content;
                listViewItem.image = image;
                listViewItem.date = date;
               // listViewItem.isChecked = Boolean.getBoolean(isChecked);
                listViewItem.isChecked = Boolean.valueOf(isChecked).booleanValue();

                itemDatas.add(listViewItem);

            }
        }

        return itemDatas;
    }




    public void close(){
        mDB.close();
    }

}
