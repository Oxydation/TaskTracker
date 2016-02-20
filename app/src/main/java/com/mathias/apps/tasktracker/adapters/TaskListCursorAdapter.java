package com.mathias.apps.tasktracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mathias.apps.tasktracker.R;
import com.mathias.apps.tasktracker.database.TasksDataSource;
import com.mathias.apps.tasktracker.models.SubTask;
import com.mathias.apps.tasktracker.models.Task;

import java.util.concurrent.TimeUnit;

/**
 * Created by Mathias on 20/02/2016.
 */
public class TaskListCursorAdapter extends CursorAdapter {
    private LayoutInflater cursorInflater;

    private Callback callback;

    public TaskListCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public TaskListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.task_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = new ViewHolder();
        Task task = TasksDataSource.cursorToTask(cursor);
        final long id = task.getId();

        if (view.getTag() == null) {
            // Lookup view for data population
            holder.name = (TextView) view.findViewById(R.id.tvName);
            holder.timeDone = (TextView) view.findViewById(R.id.tvTimeDone);
            holder.status = (TextView) view.findViewById(R.id.tvStatus);
            holder.layout = (RelativeLayout) view.findViewById(R.id.taskItemLayout);
            holder.editButton = (ImageButton) view.findViewById(R.id.editTask);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // Set background color of task
        //relativeLayout.setBackgroundColor(task.getColor());

        // Populate the data into the template view using the data object
        holder.name.setText(task.getName());

        // Get time done in hours and minutes
        long hours = TimeUnit.MINUTES.toHours((long) task.getTimeDone());
        long remainMinute = (long) (task.getTimeDone() - TimeUnit.HOURS.toMinutes(hours));
        String result = String.format("%02d", hours) + ":"
                + String.format("%02d", remainMinute) + "h";
        holder.timeDone.setText(result);

        // Get subtask status
        int amountSubTasks = 0;
        int amountFinishedSubtasks = 0;
        if (task.getSubTasks() != null) {
            amountSubTasks = task.getSubTasks().size();

            for (SubTask subTask : task.getSubTasks()) {
                if (subTask.isDone()) {
                    amountFinishedSubtasks++;
                }
            }
        }

        holder.status.setText(String.format("%d/%d Subtasks done", amountFinishedSubtasks, amountSubTasks));

        // Set opacity of task
        if (task.isDone()) {
            holder.layout.setAlpha((float) 0.5);
        } else {
            holder.layout.setAlpha((float) 1);
        }

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onEditButtonClick(id);
                }
            }
        });
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    static class ViewHolder {
        TextView name;
        TextView timeDone;
        TextView status;
        RelativeLayout layout;
        ImageButton editButton;
    }

    //http://www.c-sharpcorner.com/UploadFile/9e8439/create-custom-listener-on-button-in-listitem-listview-in-a/
    public interface Callback {
        void onEditButtonClick(long id);
    }
}
