package com.mathias.apps.tasktracker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.sileria.android.view.SeekBarPreference;

/**
 * Created by Mathias on 21/02/2016.
 */
public class SeekBarPreferenceAutoSummary extends SeekBarPreference {
    private int max;

    public int getMax() {
        return max;
    }

    @Override
    public void setMax(int max) {
        super.setMax(max);
        this.max = max;
    }

    public SeekBarPreferenceAutoSummary(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public SeekBarPreferenceAutoSummary(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SeekBarPreferenceAutoSummary(Context context) {
        super(context);
    }

    @Override
    public CharSequence getSummary() {
        String summary = (String) super.getSummary();
        if (summary != null) {
            return String.format(summary, String.valueOf(getPersistedInt(1)));
        } else {
            return String.valueOf(String.valueOf(getPersistedInt(1)));
        }
    }

    public CharSequence getSummary(int value) {
        String summary = (String) super.getSummary();
        if (summary != null) {
            return String.format(summary, String.valueOf(value));
        } else {
            return String.valueOf(String.valueOf(value));
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        super.onProgressChanged(seekBar, i, b);
        //setSummary(getSummary(i));

        onStopTrackingTouch(seekBar);
    }
}
