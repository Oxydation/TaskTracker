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

    private static final String DATABASE_CREATE_TASKS = "CREATE TABLE " + TaskEntry.TABLE_NAME +
            "("
            + TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT " + ","
            + TaskEntry.COLUMN_NAME_NAME + " TEXT " + ","
            + TaskEntry.COLUMN_NAME_COLOR + " INTEGER " + ","
            + TaskEntry.COLUMN_NAME_DESC + " TEXT " + ","
            + TaskEntry.COLUMN_NAME_TIME_EST + " NUMERIC " + ","
            + TaskEntry.COLUMN_NAME_TIME_DONE + " NUMERIC " + ","
            + TaskEntry.COLUMN_NAME_IS_DONE + " INTEGER DEFAULT 0"
            + ");";

    private static final String DATABASE_DELETE_TASKS =
            "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_TASKS);
        Log.i(LOGTAG, "Created database.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");


        // Create a way to update instead of delete database
        db.execSQL(DATABASE_DELETE_TASKS);
        onCreate(db);
    }
}
