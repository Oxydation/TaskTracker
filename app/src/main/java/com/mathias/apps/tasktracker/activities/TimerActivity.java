package com.mathias.apps.tasktracker.activities;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mathias.apps.tasktracker.R;
import com.mathias.apps.tasktracker.models.Task;

import java.util.concurrent.TimeUnit;

public class TimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the task to work with
        final Task task = (Task) getIntent().getExtras().get("selectedTask");
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final TextView timeLeft = (TextView) findViewById(R.id.tvTimeLeft);

        final ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 500, 0);
        final CountDownTimer countDownWorkTimer = new CountDownTimer(1000 * 60 * 1, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String ms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                timeLeft.setText(ms);
            }

            @Override
            public void onFinish() {
                timeLeft.setText("00:00");

                // Vibrate on countdown finished
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(500);
            }
        };

        final CountDownTimer countDownBreakTimer = new CountDownTimer(1000 * 60 * 5, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String ms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                timeLeft.setText(ms);
            }

            @Override
            public void onFinish() {
                timeLeft.setText("00:00");

                // Vibrate on countdown finished
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(500);
            }
        };

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animation.setDuration(1000 * 60 * 25); //in milliseconds
                animation.setInterpolator(new LinearInterpolator());
                animation.start();

                countDownWorkTimer.start();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
