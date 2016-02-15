package com.mathias.apps.tasktracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mathias.apps.tasktracker.R;
import com.mathias.apps.tasktracker.adapters.TaskListAdapter;
import com.mathias.apps.tasktracker.models.Task;

import java.util.ArrayList;

public class TaskListActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_SETTINGS = 1002;
    public static final int REQUEST_CODE_NEW_TASK = 100;
    private ArrayList<Task> tasks;
    private ListView listViewTasks;
    private ArrayAdapter<Task> adapter;

    // Think about using recycling view: http://developer.android.com/training/material/lists-cards.html

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
        tasks = new ArrayList<>();

        // Create the adapter to convert the array to views
        adapter = new TaskListAdapter(this, R.layout.task_item, tasks);

        listViewTasks = (ListView) findViewById(R.id.listViewTasks);
        listViewTasks.setAdapter(adapter);

//        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(TaskListActivity.this, "bla", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(TaskListActivity.this, TimerActivity.class);
//                intent.putExtra("selectedTask", tasks.get(position));
//                startActivity(intent);
//            }
//        });

        registerForContextMenu(listViewTasks);

        // Add some items to the Arraylist
        adapter.add(new Task("Project Setup"));
        adapter.add(new Task("Project Setup"));
        adapter.add(new Task("Project Setup"));
        Task second = new Task("Wordpress Post", null, 0, 123);
        second.setDone(true);
        second.setTimeDone(120);
        adapter.add(second);
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
                startActivityForResult(intent, REQUEST_CODE_SETTINGS);
                return true;
            case R.id.action_exit:
                this.finish();
                System.exit(0);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addNewTask(View view) {
        // Open new activity and add new task
        Intent intent = new Intent(this, NewTaskActivity.class);
        startActivityForResult(intent, REQUEST_CODE_NEW_TASK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_NEW_TASK) {
                Task result = (Task) data.getExtras().get("createdTask");
                adapter.add(result);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // Get the clicked item
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        // Inflate the context menu from the resource file
        getMenuInflater().inflate(R.menu.menu_task_item, menu);

        // Get the name of the clicked item
        Task clickedItem = (Task) listViewTasks.getItemAtPosition(info.position);

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo itemInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.context_menu_delete_item:

                //Remove the item from the list
                tasks.remove(itemInfo.position);

                //Update the adapter to reflect the list change
                adapter.notifyDataSetChanged();
                return true;

            case R.id.context_menu_set_done:
                //Remove the item from the list
                tasks.get(itemInfo.position).setDone(true);

                //Update the adapter to reflect the list change
                adapter.notifyDataSetChanged();
                return true;
        }

        return super.onContextItemSelected(item);
    }
}
