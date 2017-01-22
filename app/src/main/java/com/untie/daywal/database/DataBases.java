package com.untie.daywal.database;

import android.provider.BaseColumns;


public final class DataBases {

    public static final class CreateDB implements BaseColumns {
        public static final String _ID = "_id";
        public static final String TITLE = "title";
        public static final String CONTENT = "content";
        public static final String IMAGE = "image";
        public static final String DATE = "date";
        public static final String ISCHECKED = "isChecked";
        public static final String _TABLENAME = "memoinfo";
        // id name number time image
        public static final String _CREATE =
                "create table "+_TABLENAME+"("
                        +_ID+" integer primary key autoincrement, "
                        +DATE+" text not null,"
                        +TITLE+" text , "
                        +CONTENT+" text , "
                        +IMAGE+" text , "
                        +ISCHECKED+" text)";
    }



}
