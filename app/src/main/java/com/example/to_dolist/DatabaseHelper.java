package com.example.to_dolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "TASK";

    // Table columns
    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION  = "description";
    public static final String CATEGORY  = "category";
    public static final String STATUS = "status";
    public static final String CREATED_AT = "created_at";

    // Database Information
    static final String DB_NAME = "DOTOLIST.DB";

    // database version
    static final int DB_VERSION = 2;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITLE + " TEXT NOT NULL, " + DESCRIPTION + " TEXT, " + CATEGORY + " TEXT CHECK( " + CATEGORY + " IN ('general', 'important', 'urgent')) DEFAULT 'general', " +
            STATUS + " TEXT CHECK (" + STATUS + " IN ('pending', 'in progress', 'completed')) DEFAULT 'pending', " + CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
