package com.example.to_dolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBManager {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
//        Log.d("debug000", "open: ");
        return this;
    }

    public void close() {
        dbHelper.close();
//        Log.d("debug001", "close: ");
    }

    public void insert(String country, String currency, String status) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.TITLE, country);
        contentValue.put(DatabaseHelper.DESCRIPTION, currency);
        contentValue.put(DatabaseHelper.STATUS, status);
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.TITLE, DatabaseHelper.DESCRIPTION, DatabaseHelper.CATEGORY, DatabaseHelper.STATUS, DatabaseHelper.CREATED_AT };
        String orderBy = "CASE " + DatabaseHelper.CATEGORY + " WHEN 'general' THEN 3 WHEN 'important' THEN 2 WHEN 'urgent' THEN 1 ELSE 4 END ASC;";
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, orderBy);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(int _id, String name, String desc) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TITLE, name);
        contentValues.put(DatabaseHelper.DESCRIPTION, desc);
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = ?", new String[]{String.valueOf(_id)});
        return i;
    }

    public void updateStatus(int taskId, String newStatus) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.STATUS, newStatus);

        database.update(DatabaseHelper.TABLE_NAME, values, DatabaseHelper._ID + " = ?", new String[]{String.valueOf(taskId)});
    }

    public void updateCategory(int taskId, String newCategory) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CATEGORY, newCategory);
        database.update(DatabaseHelper.TABLE_NAME, values, DatabaseHelper._ID + " = ?", new String[]{String.valueOf(taskId)});
    }

    public void delete(int _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }
}