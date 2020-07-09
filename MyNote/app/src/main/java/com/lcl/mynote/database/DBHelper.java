package com.lcl.mynote.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
    public static final String TABLE = "notes";
    public static final String ID = "_id";
    public static final String TITLE ="title";
    public static final String CONTENT = "content";
    public static final String TIME = "time";
    public static final String TABLE2 = "history";
    public DBHelper(Context context) {
        super(context,"notepad.db",null,3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE "+TABLE+"( "+ID+
                " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                TITLE +" VARCHAR(30) ,"+
                CONTENT + " TEXT , "+
                TIME + " DATETIME NOT NULL )";
        String sql2 = "CREATE TABLE "+TABLE2+"( "+ID+
                " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                TITLE +" VARCHAR(30) ,"+
                CONTENT + " TEXT , "+
                TIME + " DATETIME NOT NULL )";
        db.execSQL(sql);
        db.execSQL(sql2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists notes");
        db.execSQL("drop table if exists history");//如果一张表存在 onCreate（）方法都不会再次执行 因此新添加的表也无法创建了
        onCreate(db);

    }

}
