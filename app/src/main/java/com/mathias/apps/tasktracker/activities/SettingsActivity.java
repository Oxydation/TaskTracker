package com.mathias.apps.tasktracker.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mathias.apps.tasktracker.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//
//
//        getFragmentManager().beginTransaction()
//                .replace(R.id.frame, new SettingsFragment(), "pref")
//                .commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
