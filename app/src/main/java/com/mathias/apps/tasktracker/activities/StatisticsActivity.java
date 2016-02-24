package com.mathias.apps.tasktracker.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;

import com.mathias.apps.tasktracker.R;
import com.mathias.apps.tasktracker.adapters.StatisticsCursorAdapter;
import com.mathias.apps.tasktracker.database.DataSource;

public class StatisticsActivity extends AppCompatActivity {

    private DataSource dataSource;
    private StatisticsCursorAdapter cursorAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataSource = new DataSource(this);
        // Get list and set empty view
        listView = (ListView) findViewById(R.id.listViewStatistics);
        TextView emptyView = (TextView) findViewById(R.id.statistic_list_empty);
        listView.setEmptyView(emptyView);

        registerForContextMenu(listView);

        dataSource = new DataSource(this);
        cursorAdapter = new StatisticsCursorAdapter(this, dataSource.getAllStatisticLogsCursor(), 0);
        listView.setAdapter(cursorAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
