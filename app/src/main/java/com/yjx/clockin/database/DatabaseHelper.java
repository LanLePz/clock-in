package com.yjx.clockin.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    // 创建一个Book表
    public static final String CREATE_CLOCK_IN = "create table clock_in_record ("
            + "id integer primary key autoincrement, "
            + "month text, "
            + "day text, "
            + "clock_in_time text)";

    public static final String CREATE_CLOCK_IN_SUMMARY = "create table clock_in_summary ("
            + "id integer primary key autoincrement, "
            + "month text, "
            + "day text, "
            + "man_hours real)";

    public static final String CREATE_CLOCK_IN_CONFIG = "create table clock_in_config ("
            + "id integer primary key autoincrement, "
            + "month integer, "
            + "weekday integer)";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS clock_in_summary";

    private Context mContext;

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

	// on
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CLOCK_IN);
        db.execSQL(CREATE_CLOCK_IN_SUMMARY);
        db.execSQL(CREATE_CLOCK_IN_CONFIG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

