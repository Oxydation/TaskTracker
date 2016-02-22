package com.mathias.apps.tasktracker.activities;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mathias.apps.tasktracker.R;
import com.mathias.apps.tasktracker.TimerSelectionDialogFragment;
import com.mathias.apps.tasktracker.database.TasksDataSource;
import com.mathias.apps.tasktracker.models.Task;
import com.mathias.apps.tasktracker.models.TimerMode;
import com.mathias.apps.tasktracker.models.TimerStatus;

import java.util.concurrent.TimeUnit;

public class TimerActivity extends AppCompatActivity implements TimerSelectionDialogFragment.TimerSelectionDialogListener {
    private static final long BLINK_DURATION = 600;
    private static final long TIMER_INTERVAL = 1000;
    private static final long VIBRATE_DURATION = 500;
    private static final String LOGTAG = "TimerActivity";

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
    private TimerMode currentSelectedTimerMode = TimerMode.ASK;
    private TimerMode timerMode = TimerMode.ASK;
    private TimerStatus status = TimerStatus.WAIT_FOR_WORK;

    private Chronometer tvTimeChrono;
    private TextView tvTimeSubtitle;
    private TextView tvTaskStatus;
    private FloatingActionButton fabStartPause;
    private FloatingActionButton fabStop;
    private ObjectAnimator progressBarAnimation;

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
        tvTimeChrono = (Chronometer) findViewById(R.id.tvTimeChrono);
        tvTimeSubtitle = (TextView) findViewById(R.id.tvTimeSubtitle);
        tvTaskStatus = (TextView) findViewById(R.id.tvTaskStatus);
        final TextView tvTaskName = (TextView) findViewById(R.id.tvTaskName);
        final TextView tvTaskDescription = (TextView) findViewById(R.id.tvTaskDescription);
        if (task != null) {
            tvTaskName.setText(task.getName());
            tvTaskDescription.setText(task.getDescription());
            tvTaskStatus.setText(getStatusText(task));
        }

        if (timerMode != null && timerMode.equals("pomodoro")) {
            // Set time remaining
            long millis = TimeUnit.MINUTES.toMillis(workDuration);
            tvTimeChrono.setText(getTimeString(millis));
        }

        // Initialize timers
        // http://stackoverflow.com/questions/4897665/android-chronometer-format
        tvTimeChrono.setBase(SystemClock.elapsedRealtime());
        tvTimeChrono.setFormat("00:%s");
        tvTimeChrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer c) {
                long elapsedMillis = SystemClock.elapsedRealtime() - c.getBase();
                if (elapsedMillis > 3600000L) {
                    c.setFormat("0%s");
                } else {
                    c.setFormat("00:%s");
                }
            }
        });

        countDownBreakTimer = getCountDownBreakTimer(1000 * 60 * breakDuration);
        countDownWorkTimer = getCountdownWorkTimer((long) 1000 * 60 * workDuration);
        progressBarAnimation = ObjectAnimator.ofInt(progressBar, "progress", 500, 0);

        fabStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentSelectedTimerMode) {
                    case ASK:
                        break;
                    case STOP_WATCH:
                        tvTimeChrono.stop();
                        long elapsed = SystemClock.elapsedRealtime() - tvTimeChrono.getBase();
                        tvTimeChrono.setBase(SystemClock.elapsedRealtime());
                        break;
                    case POMODORO:
                        if (status == TimerStatus.BREAK || status == TimerStatus.WORK || status == TimerStatus.WAIT_FOR_BREAK || status == TimerStatus.WAIT_FOR_WORK) {
                            status = TimerStatus.WAIT_FOR_WORK;
                            tvTimeChrono.setText(R.string.timer_finished_text);
                            tvTimeSubtitle.setText(R.string.activity_timer_subtitle_work);
                            countDownWorkTimer.cancel();
                            countDownBreakTimer.cancel();
                            progressBarAnimation.end();
                            setFABIcon(fabStartPause, R.drawable.ic_play_arrow_white_48dp);
                            tvTimeChrono.setAnimation(null);
                            updateTask(task);
                        } else {
                            Toast.makeText(TimerActivity.this, "No timer running.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                currentSelectedTimerMode = timerMode;
            }
        });

        fabStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentSelectedTimerMode) {
                    case ASK:
                        showNoticeDialog();
                        break;
                    case STOP_WATCH:
                        handleStartPauseStopWatch();
                        break;
                    case POMODORO:
                        handleStartPausePomodoro();
                        break;
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void handleStartPauseStopWatch() {
        if (status == TimerStatus.WAIT_FOR_WORK) {
            status = TimerStatus.WORK;
            tvTimeChrono.setBase(SystemClock.elapsedRealtime());
            tvTimeChrono.setFormat("00:%s");
            tvTimeChrono.start();
        } else if (status == TimerStatus.WORK) {
            status = TimerStatus.PAUSED_WORK;
            tvTimeChrono.stop();
        } else if (status == TimerStatus.PAUSED_WORK) {
            status = TimerStatus.WORK;
            tvTimeChrono.start();
        }
    }

    private void handleStartPausePomodoro() {
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
            dataSource.updateTask(task);
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
        tvTimeChrono.setAnimation(null);
    }

    public static String getStatusText(Task task) {
        return String.format("%s spent", getFriendlyTimeString(TimeUnit.SECONDS.toMillis((long) (task.getTimeDone() * 60)), false, true));
    }

    // TODO: Create a better approach to convert from string to enum
    // http://stackoverflow.com/questions/9742050/is-there-an-enum-string-resource-lookup-pattern-for-android
    private TimerMode fromString(String mode) {
        if (mode.equals("ask")) {
            timerMode = TimerMode.ASK;
        } else if (mode.equals("stopwatch")) {
            timerMode = TimerMode.STOP_WATCH;
        } else if (mode.equals("pomodoro")) {
            timerMode = TimerMode.POMODORO;
        } else {
            timerMode = TimerMode.ASK;
        }
        return timerMode;
    }

    private void loadSharedPreferences() {
        sharedPreferences = getSharedPreferences(SettingsActivityFragment.SETTINGS_SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        timerMode = fromString(sharedPreferences.getString("timer_mode", null));
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
                    case "time_mode":
                        timerMode = fromString(sharedPreferences.getString("timer_mode", null));
                }
            }
        });
    }

    private CountDownTimer getCountDownBreakTimer(long duration) {
        return new CountDownTimer(duration, TIMER_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimeChrono.setText(getTimeString(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                tvTimeChrono.setText(R.string.timer_finished_text);
                tvTimeSubtitle.setText("Break is up.");

                setFABIcon(fabStartPause, R.drawable.ic_play_arrow_white_48dp);
                if (vibrationEnabled) {
                    // Vibrate on countdown finished
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(VIBRATE_DURATION);
                }
                tvTimeChrono.setAnimation(getBlinkAnimation());
                status = TimerStatus.WAIT_FOR_WORK;
            }
        };
    }

    private CountDownTimer getCountdownWorkTimer(long duration) {
        return new CountDownTimer(duration, TIMER_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
                lastTimerValue = millisUntilFinished;
                tvTimeChrono.setText(getTimeString(millisUntilFinished));

                task.setTimeDone(task.getTimeDone() + 0.016666666666666666);
                updateTask(task);
            }

            @Override
            public void onFinish() {
                updateTask(task);
                tvTimeChrono.setText(R.string.timer_finished_text);
                tvTimeSubtitle.setText("Worktime is up.");
                notifiyTimerFinished("Work time up.", "The work time of your current assignment is finished.", task.getId());
                setFABIcon(fabStartPause, R.drawable.ic_free_breakfast_white_48dp);
                if (vibrationEnabled) {
                    // Vibrate on countdown finished
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(VIBRATE_DURATION);
                }

                tvTimeChrono.setAnimation(getBlinkAnimation());
                status = TimerStatus.WAIT_FOR_BREAK;
            }
        };
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void notifiyTimerFinished(String title, String description, long taskId) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(description);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, TimerActivity.class);
        resultIntent.putExtra("taskId", taskId);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(TimerActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        int mId = 1;
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(mId, mBuilder.build());
        Log.i(LOGTAG, "Throwed notification.");
    }

    private void updateTask(Task task) {
        tvTaskStatus.setText(getStatusText(task));
        dataSource.updateTask(task);
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

    private static String getFriendlyTimeString(long millis, boolean showAll, boolean showSeconds) {
        String hms;
        if (TimeUnit.MILLISECONDS.toHours(millis) >= 1 || showAll && !showSeconds) {
            hms = String.format("%02dh %02dm %02ds", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        } else if (!showSeconds) {
            hms = String.format("%02dh %02dm", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1));
        } else {
            hms = String.format("%01dm %01ds",
                    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        }

        return hms;
    }

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new TimerSelectionDialogFragment();
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    @Override
    public void onDialogItemSelect(DialogFragment dialog, String selectedTimerMode) {
        currentSelectedTimerMode = fromString(selectedTimerMode);
        switch (currentSelectedTimerMode) {
            case ASK:
                break;
            case STOP_WATCH:
                handleStartPauseStopWatch();
                break;
            case POMODORO:
                handleStartPausePomodoro();
                break;
        }
    }
}
