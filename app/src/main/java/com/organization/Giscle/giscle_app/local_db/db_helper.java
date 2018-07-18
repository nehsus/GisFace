package com.organization.Giscle.giscle_app.local_db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sushen.kumaron 9/17/2017.
 */

public class db_helper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "org_giscle.db";

    private static final String TABLE_USER = "CREATE TABLE IF NOT EXISTS " + Tables.userTable.TABLE_NAME + " (" +
//            Tables.userTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Tables.userTable.COLUMN_NAME + " TEXT NOT NULL," +
            Tables.userTable.COLUMN_EMAIL + " TEXT NOT NULL," +
//            Tables.userTable.COLUMN_AVTAR + " BLOB," +
            Tables.userTable.COLUMN_TYPE + " TEXT NOT NULL," +
            Tables.userTable.COLUMN_NUMBER + " TEXT," +
            Tables.userTable.COLUMN_POINTS + " INTEGER);";
    private static final String TABLE_VIDEO_RECORD = "CREATE TABLE IF NOT EXISTS " + Tables.videoDetails.TABLE_NAME + " (" +
            Tables.videoDetails._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Tables.videoDetails.COLUMN_FILE_NAME + " TEXT NOT NULL," +
//            Tables.videoDetails.COLUMN_INITIAL_LATITUDE+" TEXT NOT NULL," +
//            Tables.videoDetails.COLUMN_INITIAL_LONGITUDE+" TEXT NOT NULL," +
            Tables.videoDetails.COLUMN_FILE_path + " TEXT NOT NULL," +
//            Tables.videoDetails.COLUMN_FINAL_LATITUDE+" TEXT NOT NULL," +
//            Tables.videoDetails.COLUMN_FINAL_LONGITUDE+" TEXT NOT NULL," +
            Tables.videoDetails.COLUMN_ALL_LONGITUDE + " TEXT NOT NULL," +
            Tables.videoDetails.COLUMN_ALL_LATITUDE + " TEXT NOT NULL," +

            Tables.videoDetails.COLUMN_POINTS + " INTEGER," +
            Tables.videoDetails.COLUMN_DISTANCE + " TEXT NOT NULL," +
            Tables.videoDetails.COLUMN_TIME + " TEXT NOT NULL," +
            Tables.videoDetails.COLUMN_START_TIME + " TEXT NOT NULL," +
            Tables.videoDetails.COLUMN_END_TIME + " TEXT NOT NULL," +
            Tables.videoDetails.COLUMN_UPLOAD_STATUS + " TEXT NOT NUll);";

    public db_helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_USER);
        sqLiteDatabase.execSQL(TABLE_VIDEO_RECORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }
}

