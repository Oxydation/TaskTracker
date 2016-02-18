package com.mathias.apps.tasktracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mathias.apps.tasktracker.database.TaskTrackerContract.TaskEntry;

/**
 * Created by Mathias on 18/02/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String LOGTAG = "TASKTRACKER";

    private static final String DATABASE_NAME = "tasktracker.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TaskEntry.TABLE_NAME +
            "("
            + TaskEntry.COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " + ","
            + TaskEntry.COLUMN_NAME_NAME + " TEXT " + ","
            + TaskEntry.COLUMN_NAME_COLOR + " INTEGER " + ","
            + TaskEntry.COLUMN_NAME_DESC + " TEXT " + ","
            + TaskEntry.COLUMN_NAME_TIME_EST + " NUMERIC " + ","
            + TaskEntry.COLUMN_NAME_TIME_DONE + " NUMERIC " + ","
            + TaskEntry.COLUMN_NAME_IS_DONE + " INTEGER DEFAULT 0"
            + ");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        Log.i(LOGTAG, "Created database.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        Log.i(LOGTAG, "Deleted and recreated database.");
        onCreate(db);
    }
}
