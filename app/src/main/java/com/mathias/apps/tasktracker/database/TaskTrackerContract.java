package com.mathias.apps.tasktracker.database;

import android.provider.BaseColumns;

/**
 * Created by Mathias on 18/02/2016.
 */
public final class TaskTrackerContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public TaskTrackerContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_NAME_ENTRY_ID = "taskId";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESC = "description";
        public static final String COLUMN_NAME_COLOR = "color";
        public static final String COLUMN_NAME_TIME_EST = "timeEstimated";
        public static final String COLUMN_NAME_TIME_DONE = "timeDone";
        public static final String COLUMN_NAME_IS_DONE = "isDone";

        public static final String[] ALL_COLUMNS = {
                COLUMN_NAME_ENTRY_ID,
                COLUMN_NAME_NAME,
                COLUMN_NAME_DESC,
                COLUMN_NAME_COLOR,
                COLUMN_NAME_TIME_DONE,
                COLUMN_NAME_TIME_EST,
                COLUMN_NAME_IS_DONE};
    }

    public static abstract class SubTaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "subtask";
        public static final String COLUMN_NAME_ENTRY_ID = "subTaskId";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_IS_DONE = "isDone";
        public static final String COLUMN_NAME_PARENT_TASK = "taskId";

        public static final String[] ALL_COLUMNS = {
                COLUMN_NAME_ENTRY_ID,
                COLUMN_NAME_NAME,
                COLUMN_NAME_PARENT_TASK,
                COLUMN_NAME_IS_DONE};
    }
}