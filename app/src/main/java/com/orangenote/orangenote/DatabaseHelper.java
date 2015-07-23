package com.orangenote.orangenote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by mercurys on 15. 7. 7.
 */

public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String TAG = "MemoDatabase";
    public static String TABLE_MEMO = "MEMO";

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;

    public DatabaseHelper(Context nContext)
    {
        super(nContext, "memo.db", null, 1);
    }

    public void onCreate(SQLiteDatabase db)
    {
        // TABLE_MEMO
        String DROP_SQL = "drop table if exists " + TABLE_MEMO;

        try
        {
            db.execSQL(DROP_SQL);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Exception in DROP_SQL", ex);
        }

        String CREATE_SQL = "create table " + TABLE_MEMO + "("
                + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                + " KEY_IMAGE TEXT,"
                + " KEY_VOICE TEXT, "
                + " KEY_MEMO_TITLE TEXT, "
                + " KEY_MEMO_BODY TEXT, "
                + " CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
                + ")";
        try
        {
            db.execSQL(CREATE_SQL);
        }
        catch(Exception ex)
        {
            Log.e(TAG, "Exception in CREATE_SQL", ex);
        }
    }

    public void insert(String _query)
    {
        db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void update(String _query) {
        db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void delete(String _query) {
        db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}