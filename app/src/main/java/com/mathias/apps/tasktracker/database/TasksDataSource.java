package com.mathias.apps.tasktracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

    private SQLiteDatabase database;
    private SQLiteOpenHelper helper;

    public TasksDataSource(Context context) {
        // Create or access database
        helper = new DBHelper(context);
    }

    public void open() {
        Log.i(LOGTAG, "Database opened");
        database = helper.getWritableDatabase();
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

        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_NAME, task.getName());
        values.put(TaskEntry.COLUMN_NAME_DESC, task.getDescription());
        values.put(TaskEntry.COLUMN_NAME_COLOR, task.getColor());
        values.put(TaskEntry.COLUMN_NAME_TIME_EST, task.getTimeEstaminated());
        values.put(TaskEntry.COLUMN_NAME_TIME_DONE, task.getTimeDone());
        values.put(TaskEntry.COLUMN_NAME_IS_DONE, task.isDone());

        long insertId = database.insert(TaskEntry.TABLE_NAME, null, values);
        task.setId(insertId);

        //TODO
        // Create sub tasks
//        if (task.getSubTasks() != null) {
//            for (int i = 0; i < task.getSubTasks().size(); i++) {
//                SubTask currentSubTask = task.getSubTasks().get(i);
//                task.getSubTasks().set(i, createSubTask(currentSubTask));
//            }
//        }

        return task;

    }

    public SubTask createSubTask(SubTask subTask) {
        ContentValues values = new ContentValues();
        values.put(SubTaskEntry.COLUMN_NAME_NAME, subTask.getName());
        values.put(SubTaskEntry.COLUMN_NAME_PARENT_TASK, subTask.getParent().getId());
        values.put(SubTaskEntry.COLUMN_NAME_IS_DONE, subTask.isDone());

        long insertId = database.insert(SubTaskEntry.TABLE_NAME, null, values);
        subTask.setId(insertId);
        return subTask;
    }

    public List<Task> findAllTasks() {
        List<Task> tasks = new ArrayList<>();
        Cursor cursor = database.query(TaskEntry.TABLE_NAME, TaskEntry.ALL_COLUMNS, null, null, null, null, null);

        Log.i(LOGTAG, "Retrieved " + cursor.getCount() + " task entries.");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Task task = new Task();
                task.setId(cursor.getLong(cursor.getColumnIndex(TaskEntry.COLUMN_NAME_ENTRY_ID)));
                task.setName(cursor.getString(cursor.getColumnIndex(TaskEntry.COLUMN_NAME_NAME)));
                task.setDescription(cursor.getString(cursor.getColumnIndex(TaskEntry.COLUMN_NAME_DESC)));
                task.setTimeEstaminated(cursor.getDouble(cursor.getColumnIndex(TaskEntry.COLUMN_NAME_TIME_EST)));
                task.setTimeDone(cursor.getDouble(cursor.getColumnIndex(TaskEntry.COLUMN_NAME_TIME_DONE)));
                task.setColor(cursor.getInt(cursor.getColumnIndex(TaskEntry.COLUMN_NAME_COLOR)));
                task.setDone(cursor.getInt(cursor.getColumnIndex(TaskEntry.COLUMN_NAME_IS_DONE)) == 1);
                tasks.add(task);
            }
        }

        return tasks;
    }

    public int updateTask(Task task) {
        // New value for one column
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_NAME, task.getName());
        values.put(TaskEntry.COLUMN_NAME_DESC, task.getDescription());
        values.put(TaskEntry.COLUMN_NAME_COLOR, task.getColor());
        values.put(TaskEntry.COLUMN_NAME_TIME_EST, task.getTimeEstaminated());
        values.put(TaskEntry.COLUMN_NAME_TIME_DONE, task.getTimeDone());
        values.put(TaskEntry.COLUMN_NAME_IS_DONE, task.isDone());

        // Which row to update, based on the ID
        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(task.getId())};

        int count = database.update(
                TaskEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count;
    }

    public int deleteTask(Task task) {
        // Define 'where' part of query.
        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(task.getId())};

        // Issue SQL statement.
        return database.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
    }
    // Boolean flag = (cursor.getInt(cursor.getColumnIndex("flag")) == 1);
}
