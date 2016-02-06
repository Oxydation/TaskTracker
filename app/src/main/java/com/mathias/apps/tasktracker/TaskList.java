package com.mathias.apps.tasktracker;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TaskList extends AppCompatActivity {
    private ArrayList<Task> tasks;
    private ListView listViewTasks;
    private ArrayAdapter<Task> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Open "AddTaskActivity"
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
//        // https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
//        tasks = new ArrayList<>();
//        listViewTasks = (ListView) findViewById(R.id.listViewTasks);
//
//        // Create the adapter to convert the array to views
//        TaskAdapter adapter = new TaskAdapter(this, tasks);
//
//        listViewTasks.setAdapter(adapter);
//        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // Get name of selected item and show snackbar
//                Task clickedItem = (Task) parent.getItemAtPosition(position);
//                Snackbar.make(parent, "Clicked: " + clickedItem.getName(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
//            }
//        });
//
//        //Add some items to the Arraylist
//        adapter.add(new Task("test"));
//
//        //Add some items to the Arraylist
//        adapter.add(new Task("Second"));

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ObjectAnimator animation = ObjectAnimator.ofInt (progressBar, "progress", 0, 100); // see this max value coming back here, we animale towards that value
        animation.setDuration(5000); //in milliseconds
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
