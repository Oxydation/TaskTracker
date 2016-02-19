package com.mathias.apps.tasktracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mathias.apps.tasktracker.R;
import com.mathias.apps.tasktracker.activities.TimerActivity;
import com.mathias.apps.tasktracker.models.SubTask;
import com.mathias.apps.tasktracker.models.Task;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mathias on 06/02/2016.
 */
public class TaskListAdapter extends ArrayAdapter<Task> implements View.OnCreateContextMenuListener {
    private List<Task> tasks;
    private LayoutInflater inflater;
    private Context context;
    private int layoutResourceId;
    private Callback callback;

    public TaskListAdapter(Context context, int resource, List<Task> tasks) {
        super(context, resource, tasks);
        this.tasks = tasks;
        this.context = context;
        this.layoutResourceId = resource;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder = new Holder();

        // Check if an existing view is being reused, otherwise inflate the view
        if (row == null) {
            inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            // Lookup view for data population
            holder.name = (TextView) row.findViewById(R.id.tvName);
            holder.timeDone = (TextView) row.findViewById(R.id.tvTimeDone);
            holder.status = (TextView) row.findViewById(R.id.tvStatus);
            holder.layout = (RelativeLayout) row.findViewById(R.id.taskItemLayout);
            holder.editButton = (ImageButton) row.findViewById(R.id.editTask);
            holder.position = position;
            row.setTag(holder);

        } else {
            holder = (Holder) row.getTag();
        }

        // Get the data item for this position
        final Task task = tasks.get(position);

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

        // Handle click on task
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TimerActivity.class);
                intent.putExtra("selectedTask", task);
                getContext().startActivity(intent);
            }
        });

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onEditButtonClick(position);
                }
            }
        });

        // http://stackoverflow.com/questions/3972945/custom-listview-and-context-menu-how-to-get-it
        row.setOnCreateContextMenuListener(this);

        // Return the completed view to render on screen
        return row;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    static class Holder {
        TextView name;
        TextView timeDone;
        TextView status;
        RelativeLayout layout;
        ImageButton editButton;
        int position;
    }

    public interface Callback {
        void onEditButtonClick(int position);
    }
}
