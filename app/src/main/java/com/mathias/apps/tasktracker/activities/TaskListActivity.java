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
import com.mathias.apps.tasktracker.database.TasksDataSource;
import com.mathias.apps.tasktracker.models.Task;

import java.util.List;

public class TaskListActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_SETTINGS = 1002;
    public static final int REQUEST_CODE_NEW_TASK = 100;
    private List<Task> tasks;
    private ListView listViewTasks;
    private ArrayAdapter<Task> adapter;

    private TasksDataSource dataSource;

    // Think about using recycling view: http://developer.android.com/training/material/lists-cards.html

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Open datas source and retrieve tasks
        dataSource = new TasksDataSource(this);
        dataSource.open();

        // https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
        // tasks = new ArrayList<>();
        tasks = dataSource.findAllTasks();

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

                //TODO: Is that a good way to retrieve new created objects?
                Task result = (Task) data.getExtras().get("createdTask");
                dataSource.open();
                result = dataSource.createTask(result);
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
        //Task clickedItem = (Task) listViewTasks.getItemAtPosition(info.position);

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo itemInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.context_menu_delete_item:

                //Remove the item from the list
                Task removedTask = tasks.remove(itemInfo.position);
                dataSource.deleteTask(removedTask);

                //Update the adapter to reflect the list change
                adapter.notifyDataSetChanged();
                return true;

            case R.id.context_menu_set_done:
                Task changedTask = tasks.get(itemInfo.position);
                changedTask.setDone(true);
                dataSource.updateTask(changedTask);

                //Update the adapter to reflect the list change
                adapter.notifyDataSetChanged();
                return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }
}
