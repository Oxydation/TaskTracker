package com.mathias.apps.tasktracker.models;

import android.os.SystemClock;
import android.widget.Chronometer;

/**
 * Represents a stopwatch using a chronometer.
 * Created by Mathias on 22/02/2016.
 */
public class StopWatch {
    private Chronometer chronometer;
    private long lastStopTime;
    private String format;
    private Chronometer.OnChronometerTickListener onChronometerTickListener;

    public StopWatch(Chronometer chronometer) {
        this.chronometer = chronometer;
        init();
    }

    private void init() {
        // http://stackoverflow.com/questions/4897665/android-chronometer-format
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer c) {
                long elapsedMillis = SystemClock.elapsedRealtime() - c.getBase();
                if (elapsedMillis > 3600000L) {
                    c.setFormat("0%s");
                } else {
                    c.setFormat("00:%s");
                }
            }
        });
    }

    public void start() {
        // on first start
        if (lastStopTime == 0) {
            chronometer.setFormat("00:%s");
            chronometer.setBase(SystemClock.elapsedRealtime());
            // on resume after pause
        } else {
            long intervalOnPause = (SystemClock.elapsedRealtime() - lastStopTime);
            chronometer.setBase(chronometer.getBase() + intervalOnPause);
        }

        chronometer.start();
    }

    public void pause() {
        chronometer.stop();
        lastStopTime = SystemClock.elapsedRealtime();
    }

    public void stop() {
        chronometer.stop();
        lastStopTime = 0;
    }

    public Chronometer getChronometer() {
        return chronometer;
    }

    public void setChronometer(Chronometer chronometer) {
        this.chronometer = chronometer;
    }

    public long getLastStopTime() {
        return lastStopTime;
    }

    public void setLastStopTime(long lastStopTime) {
        this.lastStopTime = lastStopTime;
    }

    public Chronometer.OnChronometerTickListener getOnChronometerTickListener() {
        return onChronometerTickListener;
    }

    public void setOnChronometerTickListener(Chronometer.OnChronometerTickListener onChronometerTickListener) {
        this.onChronometerTickListener = onChronometerTickListener;
    }
}
