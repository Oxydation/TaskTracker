package com.mathias.apps.tasktracker.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.mathias.apps.tasktracker.R;
import com.mathias.apps.tasktracker.activities.TimerActivity;
import com.mathias.apps.tasktracker.database.DataSource;
import com.mathias.apps.tasktracker.models.StatisticLog;

/**
 * Created by Mathias on 20/02/2016.
 */
public class StatisticsCursorAdapter extends CursorAdapter implements View.OnCreateContextMenuListener {
    private static final String LOGTAG = "TASKTRACKER";
    private LayoutInflater cursorInflater;

    public StatisticsCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public StatisticsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.statistics_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ViewHolder holder;
        final StatisticLog statisticLog = DataSource.cursorToStatisticLog(cursor);

        final long id;
        if (statisticLog != null) {
            id = statisticLog.getId();
        } else {
            Log.e(LOGTAG, "Not able to show task because log is null.");
            return;
        }

        if (view.getTag() == null) {
            // Lookup view for data population
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.populateRow(statisticLog);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TimerActivity.class);
                intent.putExtra("taskId", statisticLog.getTask().getId());
                context.startActivity(intent);
            }
        });

        view.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }

    static class ViewHolder {
        TextView taskName;
        TextView action;
        TextView message;
        TextView workTime;
        TextView breakTime;
        TextView timeStamp;

        /**
         * Initialize the holder.
         *
         * @param view The view to populate the objects from.
         */
        public ViewHolder(View view) {
            action = (TextView) view.findViewById(R.id.tvAction);
            // message = (TextView) view.findViewById(R.id.mes);
            workTime = (TextView) view.findViewById(R.id.tvWorkTime);
            breakTime = (TextView) view.findViewById(R.id.tvBreakTime);
            taskName = (TextView) view.findViewById(R.id.tvTaskName);
            timeStamp = (TextView) view.findViewById(R.id.tvTimeStamp);
        }

        public void populateRow(StatisticLog statisticLog) {
            action.setText(statisticLog.getAction());
            taskName.setText(statisticLog.getTask().getName());
            timeStamp.setText(statisticLog.getTime().toString());

//            // Populate the data into the template view using the data object
//            name.setText(statisticLog.getName());
//
//            // Get time done in hours and minutes
//            long hours = TimeUnit.SECONDS.toHours((long) statisticLog.getTimeDone());
//            long remainMinute = (long) (TimeUnit.SECONDS.toMinutes((long) statisticLog.getTimeDone()) - TimeUnit.HOURS.toMinutes(hours));
//            String result = String.format("%01d", hours) + "h " + String.format("%01d", remainMinute) + "m";
//            timeDone.setText(result);
//
//            if (statisticLog.getDescription() == null || statisticLog.getDescription().isEmpty()) {
//                status.setText(R.string.task_item_description_empty);
//            } else {
//                status.setText(statisticLog.getDescription());
//            }
//            // Set opacity of task
//            if (statisticLog.isDone()) {
//                layout.setAlpha((float) 0.5);
//                layout.setBackgroundColor(0);
//            } else {
//                layout.setAlpha((float) 1);
//                if (statisticLog.getColor() != 0) {
//                    layout.setBackgroundColor(statisticLog.getColor());
//                } else {
//                    layout.setBackgroundResource(R.color.bg1_task_grey);
//                }
//            }
        }
    }
}
