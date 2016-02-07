package com.mathias.apps.tasktracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mathias.apps.tasktracker.R;
import com.mathias.apps.tasktracker.models.Task;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mathias on 06/02/2016.
 */
public class TaskAdapter extends ArrayAdapter<Task> {
    public TaskAdapter(Context context, ArrayList<Task> tasks) {
        super(context, 0, tasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Task task = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvTimeDone = (TextView) convertView.findViewById(R.id.tvTimeDone);
        RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.taskItemLayout);

        // TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);
        // Populate the data into the template view using the data object
        tvName.setText(task.getName());
        relativeLayout.setBackgroundColor(task.getColor());


        long hours = TimeUnit.MINUTES.toHours((long) task.getTimeDone());
        long remainMinute = (long) (task.getTimeDone() - TimeUnit.HOURS.toMinutes(hours));
        String result = String.format("%02d", hours) + ":"
                + String.format("%02d", remainMinute) + "h";
        tvTimeDone.setText(result);

        if (task.isDone()) {
            relativeLayout.setAlpha((float) 0.5);
        } else {
            relativeLayout.setAlpha((float) 1);
        }


        // tvHome.setText(user.hometown);
        // Return the completed view to render on screen
        return convertView;
    }
}
