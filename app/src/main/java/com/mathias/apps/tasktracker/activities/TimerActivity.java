package com.mathias.apps.tasktracker.activities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mathias.apps.tasktracker.R;
import com.mathias.apps.tasktracker.database.TasksDataSource;
import com.mathias.apps.tasktracker.models.Task;
import com.mathias.apps.tasktracker.models.TimerStatus;

import java.util.concurrent.TimeUnit;

public class TimerActivity extends AppCompatActivity {
    private static final long BLINK_DURATION = 600;

    private int workDuration;
    private int breakDuration;
    private int longBreakDuration;
    private int longBreakInterval;
    private boolean longBreakEnabled;
    private boolean vibrationEnabled;
    private boolean notificationEnabled;
    private TasksDataSource dataSource;
    private SharedPreferences sharedPreferences;

    private CountDownTimer countDownWorkTimer;
    private CountDownTimer countDownBreakTimer;
    private long lastTimerValue;
    private Task task;
    private String timerMode;
    private TimerStatus status = TimerStatus.WAIT_FOR_WORK;

    private TextView tvTime;
    private TextView tvTimeSubtitle;
    private FloatingActionButton fabStartPause;
    private FloatingActionButton fabStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get datasource
        dataSource = new TasksDataSource(this);
        fabStartPause = (FloatingActionButton) findViewById(R.id.fabStartPause);
        fabStop = (FloatingActionButton) findViewById(R.id.fabStop);

        loadSharedPreferences();

        // Get the task to work with
        task = dataSource.getTask(getIntent().getExtras().getLong("taskId"));

        // Load view items
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvTimeSubtitle = (TextView) findViewById(R.id.tvTimeSubtitle);
        final TextView tvTaskName = (TextView) findViewById(R.id.tvTaskName);
        final TextView tvTaskDescription = (TextView) findViewById(R.id.tvTaskDescription);
        if (task != null) {
            tvTaskName.setText(task.getName());
            tvTaskDescription.setText(task.getDescription());
        }

        if (timerMode != null && timerMode.equals("pomodoro")) {
            // Set time remaining
            long millis = TimeUnit.MINUTES.toMillis(workDuration);
            tvTime.setText(getTimeString(millis));
        }


        // Initialize timers
        countDownBreakTimer = getCountDownBreakTimer(1000 * 60 * breakDuration);
        countDownWorkTimer = getCountdownWorkTimer((long) 1000 * 60 * workDuration);
        final ObjectAnimator progressBarAnimation = ObjectAnimator.ofInt(progressBar, "progress", 500, 0);

        fabStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == TimerStatus.BREAK || status == TimerStatus.WORK || status == TimerStatus.WAIT_FOR_BREAK) {
                    status = TimerStatus.WAIT_FOR_WORK;
                    tvTime.setText(R.string.timer_finished_text);
                    countDownWorkTimer.cancel();
                    countDownBreakTimer.cancel();
                    progressBarAnimation.end();
                    setFABIcon(fabStartPause, R.drawable.ic_play_arrow_white_48dp);
                    tvTime.setAnimation(null);
                } else {
                    Toast.makeText(TimerActivity.this, "No timer running.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fabStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == TimerStatus.WAIT_FOR_WORK) {
                    status = TimerStatus.WORK;
                    tvTimeSubtitle.setText(R.string.activity_timer_subtitle_work);
                    setFABIcon(fabStartPause, R.drawable.ic_pause_white_48dp);
                    progressBarAnimation.setDuration(1000 * 60 * workDuration); //in milliseconds
                    progressBarAnimation.setInterpolator(new LinearInterpolator());
                    progressBarAnimation.start();

                    // Set new countdowntimer
                    countDownWorkTimer = getCountdownWorkTimer(1000 * 60 * workDuration);
                    countDownWorkTimer.start();
                } else if (status == TimerStatus.WAIT_FOR_BREAK) {
                    status = TimerStatus.BREAK;
                    tvTimeSubtitle.setText("It's break time.");
                    setFABIcon(fabStartPause, R.drawable.ic_skip_next_white_48dp);
                    progressBarAnimation.setDuration(1000 * 60 * breakDuration); //in milliseconds
                    progressBarAnimation.setInterpolator(new LinearInterpolator());
                    progressBarAnimation.start();
                    countDownBreakTimer.start();
                } else if (status == TimerStatus.BREAK) {
                    // Skip break
                    countDownBreakTimer.cancel();
                    progressBarAnimation.end();

                    // Snackbar.make(v,"Skipped break.", Snackbar.LENGTH_LONG);
                    // Restart work timer
                    status = TimerStatus.WORK;
                    tvTimeSubtitle.setText(R.string.activity_timer_subtitle_work);
                    setFABIcon(fabStartPause, R.drawable.ic_pause_white_48dp);
                    progressBarAnimation.setDuration(1000 * 60 * workDuration); //in milliseconds
                    progressBarAnimation.setInterpolator(new LinearInterpolator());
                    progressBarAnimation.start();
                    countDownWorkTimer = getCountdownWorkTimer(1000 * 60 * workDuration);
                    countDownWorkTimer.start();
                } else if (status == TimerStatus.WORK) {
                    status = TimerStatus.PAUSED_WORK;
                    setFABIcon(fabStartPause, R.drawable.ic_play_arrow_white_48dp);
                    countDownWorkTimer.cancel();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        progressBarAnimation.pause();
                    } else {
                        progressBarAnimation.cancel();
                    }
                } else if (status == TimerStatus.PAUSED_WORK) {
                    status = TimerStatus.WORK;
                    setFABIcon(fabStartPause, R.drawable.ic_pause_white_48dp);
                    countDownWorkTimer = getCountdownWorkTimer(lastTimerValue);
                    countDownWorkTimer.start();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        progressBarAnimation.resume();
                    } else {
                        progressBarAnimation.start();
                    }
                } else {
                    Toast.makeText(TimerActivity.this, "Timer already running.", Toast.LENGTH_SHORT).show();
                }
                tvTime.setAnimation(null);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadSharedPreferences() {
        sharedPreferences = getSharedPreferences(SettingsActivityFragment.SETTINGS_SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        timerMode = sharedPreferences.getString("timer_mode", null);
        workDuration = sharedPreferences.getInt("work_duration", 25);
        breakDuration = sharedPreferences.getInt("break_duration", 5);
        longBreakInterval = sharedPreferences.getInt("long_break_interval", 4);
        longBreakDuration = sharedPreferences.getInt("long_break_duration", 15);
        longBreakEnabled = sharedPreferences.getBoolean("long_break_enabled", false);
        vibrationEnabled = sharedPreferences.getBoolean("vibration_enabled", true);
        notificationEnabled = sharedPreferences.getBoolean("notification_enabled", false);

        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
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
    }

    private CountDownTimer getCountDownBreakTimer(long duration) {
        return new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTime.setText(getTimeString(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                tvTime.setText(R.string.timer_finished_text);
                tvTimeSubtitle.setText("Break is up.");

                setFABIcon(fabStartPause, R.drawable.ic_play_arrow_white_48dp);
                if (vibrationEnabled) {
                    // Vibrate on countdown finished
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(500);
                }
                tvTime.setAnimation(getBlinkAnimation());
                status = TimerStatus.WAIT_FOR_WORK;
            }
        };
    }

    private CountDownTimer getCountdownWorkTimer(long duration) {
        return new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                lastTimerValue = millisUntilFinished;
                tvTime.setText(getTimeString(millisUntilFinished));

                // set every 15 seconds a new value, via modulo
                task.setTimeDone(TimeUnit.SECONDS.toMinutes(TimeUnit.MINUTES.toSeconds((long) task.getTimeDone()) + 1));
                dataSource.updateTask(task);
            }

            @Override
            public void onFinish() {
                tvTime.setText(R.string.timer_finished_text);
                tvTimeSubtitle.setText("Worktime is up.");
                setFABIcon(fabStartPause, R.drawable.ic_free_breakfast_white_48dp);
                if (vibrationEnabled) {
                    // Vibrate on countdown finished
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(500);
                }

                tvTime.setAnimation(getBlinkAnimation());
                status = TimerStatus.WAIT_FOR_BREAK;
            }
        };
    }

    private void setFABIcon(FloatingActionButton fab, int resourceId) {
        setFABIcon(fab, resourceId, getApplicationContext());
    }

    private void setFABIcon(FloatingActionButton fab, int resourceId, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fab.setImageDrawable(getResources().getDrawable(resourceId, context.getTheme()));
        } else {
            fab.setImageDrawable(getResources().getDrawable(resourceId));
        }
    }

    private void startTimer(String timerMode) {

    }

    private void pauseTimer() {

    }

    private void stopTimer() {

    }

    // http://stackoverflow.com/questions/23426201/flashing-textview-background-in-android-for-1-second-only-once
    public Animation getBlinkAnimation() {
        Animation animation = new AlphaAnimation(1, 0);         // Change alpha from fully visible to invisible
        animation.setDuration(BLINK_DURATION);                             // duration - half a second
        animation.setInterpolator(new LinearInterpolator());    // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE);                            // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE);             // Reverse animation at the end so the button will fade back in
        return animation;
    }

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
