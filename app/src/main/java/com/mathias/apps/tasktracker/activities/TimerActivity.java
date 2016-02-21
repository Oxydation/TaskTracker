package com.mathias.apps.tasktracker.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mathias.apps.tasktracker.R;
import com.mathias.apps.tasktracker.models.Task;

import java.util.concurrent.TimeUnit;

public class TimerActivity extends AppCompatActivity {
    private boolean timerRunning = false;

    private int workDuration;
    private int breakDuration;
    private int longBreakDuration;
    private int longBreakInterval;
    private boolean longBreakEnabled;
    private boolean vibrationEnabled;
    private boolean notificationEnabled;
    private String timerMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences prefs = getSharedPreferences(SettingsActivityFragment.SETTINGS_SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        timerMode = prefs.getString("timer_mode", null);
        workDuration = prefs.getInt("work_duration", 25);
        breakDuration = prefs.getInt("break_duration", 5);
        longBreakInterval = prefs.getInt("long_break_interval", 4);
        longBreakDuration = prefs.getInt("long_break_duration", 15);
        longBreakEnabled = prefs.getBoolean("long_break_enabled", false);
        vibrationEnabled = prefs.getBoolean("vibration_enabled", true);
        notificationEnabled = prefs.getBoolean("notification_enabled", false);

        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                // react to break and work duration
                switch (key) {
                    case "work_duration":
                        workDuration = sharedPreferences.getInt("work_duration", 25);
                        break;
                    case "break_duration":
                        breakDuration = sharedPreferences.getInt("break_duration", 5);
                        break;
                }
            }
        });

        // Get the task to work with
        final Task task = (Task) getIntent().getExtras().get("selectedTask");
        final TextView tvTaskName = (TextView) findViewById(R.id.tvTaskName);
        if (task != null) {
            tvTaskName.setText(task.getName());
        }

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final TextView tvTime = (TextView) findViewById(R.id.tvTime);

        if (timerMode != null && timerMode.equals("pomodoro")) {
            // Set time remaining
            long millis = TimeUnit.MINUTES.toMillis(workDuration);
            tvTime.setText(getTimeString(millis));
        }

        final ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 500, 0);
        final CountDownTimer countDownBreakTimer = new CountDownTimer(1000 * 60 * breakDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTime.setText(getTimeString(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                tvTime.setText(R.string.timer_finished_text);

                if (vibrationEnabled) {
                    // Vibrate on countdown finished
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(500);
                }

                timerRunning = false;
                // Restart working timer as we are finished with break

            }
        };
        final CountDownTimer countDownWorkTimer = new CountDownTimer(1000 * 60 * workDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTime.setText(getTimeString(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                tvTime.setText(R.string.timer_finished_text);

                if (vibrationEnabled) {
                    // Vibrate on countdown finished
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(500);
                }

                // Do not start break timer automatically, blink with circle
                animation.setDuration(1000 * 60 * breakDuration); //in milliseconds
                animation.setInterpolator(new LinearInterpolator());
                animation.start();
                countDownBreakTimer.start();
            }
        };

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timerRunning) {
                    animation.setDuration(1000 * 60 * workDuration); //in milliseconds
                    animation.setInterpolator(new LinearInterpolator());
                    animation.start();
                    countDownWorkTimer.start();
                } else {
                    Toast.makeText(TimerActivity.this, "Timer already running.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

//    private static String getTimeRemaining(long millisUntilFinished) {
//        String ms = String.format("%02d:%02d",
//                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
//                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
//        return ms;
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, TaskListActivity.REQUEST_CODE_SETTINGS);
                return true;
            case R.id.action_exit:
                this.finish();
                System.exit(0);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private static String getTimeString(long millis) {

        String hms;
        if (TimeUnit.MILLISECONDS.toHours(millis) >= 1) {
            hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        } else {
            hms = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        }

        return hms;
    }
}
