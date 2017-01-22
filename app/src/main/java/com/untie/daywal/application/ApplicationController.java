package com.untie.daywal.application;

import android.app.Application;

import com.untie.daywal.database.DbOpenHelper;

import java.sql.SQLException;

public class ApplicationController extends Application {

    private static ApplicationController instance;
    public DbOpenHelper mDbOpenHelper;

    public static ApplicationController getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationController.instance = this;
        this.buildDB();

    }

    public void buildDB() {
        // DB Create and Open
        mDbOpenHelper = new DbOpenHelper(this);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
