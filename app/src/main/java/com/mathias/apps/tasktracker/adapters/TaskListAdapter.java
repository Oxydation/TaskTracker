package com.mathias.apps.tasktracker.adapters;

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
import com.mathias.apps.tasktracker.activities.NewTaskActivity;
import com.mathias.apps.tasktracker.activities.TimerActivity;
import com.mathias.apps.tasktracker.models.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mathias on 06/02/2016.
 */
public class TaskListAdapter extends ArrayAdapter<Task> implements View.OnCreateContextMenuListener {
    private List<Task> tasks;

    public TaskListAdapter(Context context, int resource, ArrayList<Task> tasks) {
        super(context, resource, tasks);
        this.tasks = tasks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final Task task = tasks.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
            convertView.setClickable(true);
            convertView.setFocusable(false);
        }

        // Lookup view for data population
        final TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        final TextView tvTimeDone = (TextView) convertView.findViewById(R.id.tvTimeDone);
        final TextView tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
        RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.taskItemLayout);

        // Set background color of task
        //relativeLayout.setBackgroundColor(task.getColor());

        // Populate the data into the template view using the data object
        tvName.setText(task.getName());

        // Get time done in hours and minutes
        long hours = TimeUnit.MINUTES.toHours((long) task.getTimeDone());
        long remainMinute = (long) (task.getTimeDone() - TimeUnit.HOURS.toMinutes(hours));
        String result = String.format("%02d", hours) + ":"
                + String.format("%02d", remainMinute) + "h";
        tvTimeDone.setText(result);

        // Get subtask status
        int amountSubTasks = 0;
        int amountFinishedSubtasks = 0;
        if (task.getSubTasks() != null) {
            amountSubTasks = task.getSubTasks().size();

            for (Task subTask : task.getSubTasks()) {
                if (subTask.isDone()) {
                    amountFinishedSubtasks++;
                }
            }
        }

        tvStatus.setText(String.format("%d/%d Subtasks done", amountFinishedSubtasks, amountSubTasks));

        // Set opacity of task
        if (task.isDone()) {
            relativeLayout.setAlpha((float) 0.5);
        } else {
            relativeLayout.setAlpha((float) 1);
        }

        // Handle click on edit button
        ImageButton editImageButton = (ImageButton) convertView.findViewById(R.id.editTask);
        editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewTaskActivity.class);
                intent.putExtra("selectedTask", task);
                getContext().startActivity(intent);
            }
        });

        // Handle click on task
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TimerActivity.class);
                intent.putExtra("selectedTask", task);
                getContext().startActivity(intent);
            }
        });

        // http://stackoverflow.com/questions/3972945/custom-listview-and-context-menu-how-to-get-it
        convertView.setOnCreateContextMenuListener(this);

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }
}
