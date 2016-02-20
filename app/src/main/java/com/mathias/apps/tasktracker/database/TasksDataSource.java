package com.mathias.apps.tasktracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mathias.apps.tasktracker.database.TaskTrackerContract.SubTaskEntry;
import com.mathias.apps.tasktracker.database.TaskTrackerContract.TaskEntry;
import com.mathias.apps.tasktracker.models.SubTask;
import com.mathias.apps.tasktracker.models.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mathias on 18/02/2016.
 */
public class TasksDataSource {

    private static final String LOGTAG = "TASKTRACKER";

    private SQLiteDatabase db;
    private SQLiteOpenHelper helper;

    public TasksDataSource(Context context) {
        // Create or access db
        helper = new DBHelper(context);
    }

    public void open() {
        Log.i(LOGTAG, "Database opened");
        db = helper.getWritableDatabase();
        close();
    }

    public void close() {
        Log.i(LOGTAG, "Database closed");
        helper.close();
    }

    public Task createTask(Task task) {
        if (task == null) {
            Log.w(LOGTAG, "Could not create task, task is null.");
            return null;
        }

        open();

        ContentValues values = taskToContentValues(task);

        long insertId = db.insert(TaskEntry.TABLE_NAME, null, values);
        task.setId(insertId);

        //TODO
        // Create sub tasks
//        if (task.getSubTasks() != null) {
//            for (int i = 0; i < task.getSubTasks().size(); i++) {
//                SubTask currentSubTask = task.getSubTasks().get(i);
//                task.getSubTasks().set(i, createSubTask(currentSubTask));
//            }
//        }
        close();

        return task;

    }

    public SubTask createSubTask(SubTask subTask) {
        ContentValues values = new ContentValues();
        values.put(SubTaskEntry.COLUMN_NAME_NAME, subTask.getName());
        values.put(SubTaskEntry.COLUMN_NAME_PARENT_TASK, subTask.getParent().getId());
        values.put(SubTaskEntry.COLUMN_NAME_IS_DONE, subTask.isDone());

        long insertId = db.insert(SubTaskEntry.TABLE_NAME, null, values);
        subTask.setId(insertId);
        return subTask;
    }

    public List<Task> findAllTasks() {
        open();

        List<Task> tasks = new ArrayList<>();
        Cursor cursor = db.query(TaskEntry.TABLE_NAME, TaskEntry.ALL_COLUMNS, null, null, null, null, null);

        Log.i(LOGTAG, "Retrieved " + cursor.getCount() + " task entries.");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Task task = cursorToTask(cursor);
                if (task != null) {
                    tasks.add(task);
                }
            }
        }

        close();

        return tasks;
    }

    private Task cursorToTask(Cursor cursor) {
        try {
            Task task = new Task();
            task.setId(cursor.getLong(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID)));
            task.setName(cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_NAME)));
            task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESC)));
            task.setTimeEstaminated(cursor.getLong(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TIME_EST)));
            task.setTimeDone(cursor.getDouble(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TIME_DONE)));
            task.setColor(cursor.getInt(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COLOR)));
            task.setDone(cursor.getInt(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_IS_DONE)) == 1);
            return task;
        } catch (CursorIndexOutOfBoundsException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private ContentValues taskToContentValues(Task task) {
        // New value for one column
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_NAME, task.getName());
        values.put(TaskEntry.COLUMN_NAME_DESC, task.getDescription());
        values.put(TaskEntry.COLUMN_NAME_COLOR, task.getColor());
        values.put(TaskEntry.COLUMN_NAME_TIME_EST, task.getTimeEstaminated());
        values.put(TaskEntry.COLUMN_NAME_TIME_DONE, task.getTimeDone());
        values.put(TaskEntry.COLUMN_NAME_IS_DONE, task.isDone());
        return values;
    }

    private Cursor getAllTasksCursor() {
        open();
        Cursor cursor = db.query(TaskEntry.TABLE_NAME, TaskEntry.ALL_COLUMNS, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        close();
        return cursor;
    }

    public Task getTask(long id) {
        open();

        String where = TaskEntry.COLUMN_NAME_ENTRY_ID + "=?";
        String[] args = new String[]{Long.toString(id)};
        Cursor cursor = db.query(TaskEntry.TABLE_NAME, TaskEntry.ALL_COLUMNS, where, args, null, null, null);

        cursor.moveToFirst();
        Task task = cursorToTask(cursor);
        cursor.close();
        close();

        return task;
    }

    public int updateTask(Task task) {
        open();

        ContentValues values = taskToContentValues(task);

        // Which row to update, based on the ID
        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(task.getId())};

        int count = db.update(
                TaskEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        close();

        return count;
    }


    public int deleteTask(Task task) {
        open();

        // Define 'where' part of query.
        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " = ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(task.getId())};

        // Issue SQL statement.
        int result = db.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);

        close();

        return result;
    }

    public int deleteTask(long id) {
        open();
        int result = db.delete(TaskEntry.TABLE_NAME, TaskEntry.COLUMN_NAME_ENTRY_ID + " =?", new String[]{Long.toString(id)});
        close();
        return result;
    }
    // Boolean flag = (cursor.getInt(cursor.getColumnIndex("flag")) == 1);
}
