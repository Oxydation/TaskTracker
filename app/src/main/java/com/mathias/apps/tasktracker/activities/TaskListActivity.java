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
import android.widget.ListView;
import android.widget.TextView;

import com.mathias.apps.tasktracker.R;
import com.mathias.apps.tasktracker.adapters.TaskListAdapter;
import com.mathias.apps.tasktracker.database.TasksDataSource;
import com.mathias.apps.tasktracker.models.Task;

import java.util.List;

public class TaskListActivity extends AppCompatActivity implements TaskListAdapter.Callback {
    public static final int REQUEST_CODE_SETTINGS = 1002;
    public static final int REQUEST_CODE_NEW_TASK = 100;
    private static final int REQUEST_CODE_UDPATE_TASK = 1003;

    private List<Task> tasks;
    private ListView listViewTasks;
    private TaskListAdapter adapter;

    private TasksDataSource dataSource;

    // Think about using recycling view: http://developer.android.com/training/material/lists-cards.html
    // https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get list and set empty view
        listViewTasks = (ListView) findViewById(R.id.listViewTasks);
        TextView emptyView = (TextView) findViewById(R.id.main_list_empty);
        listViewTasks.setEmptyView(emptyView);

        // Open datas source and retrieve tasks
        dataSource = new TasksDataSource(this);
        dataSource.open();

        // tasks = new ArrayList<>();
        tasks = dataSource.findAllTasks();

        // Create the adapter to convert the array to views
        adapter = new TaskListAdapter(this, R.layout.task_item, tasks);
        adapter.setCallback(this);

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
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_NEW_TASK) {

                //TODO: Is that a good way to retrieve new created objects?
                Task result = (Task) data.getExtras().get("createdTask");
                dataSource.open();
                result = dataSource.createTask(result);
                adapter.add(result);
            } else if (requestCode == REQUEST_CODE_UDPATE_TASK) {
                Task result = (Task) data.getExtras().get("updatedTask");
                int position = data.getIntExtra("position", 0);
                tasks.set(position, result);
                dataSource.open();
                dataSource.updateTask(result);
                adapter.notifyDataSetChanged();
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

//        item.setVisible(false);
//        if (clickedItem.isDone()) {
//            v.findViewById(R.id.context_menu_set_done).setVisibility(View.INVISIBLE);
//            v.findViewById(R.id.context_menu_set_undone).setVisibility(View.VISIBLE);
//        } else {
//            v.findViewById(R.id.context_menu_set_done).setVisibility(View.VISIBLE);
//            v.findViewById(R.id.context_menu_set_undone).setVisibility(View.INVISIBLE);
//        }

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

            case R.id.context_menu_set_undone:
                Task changedTask1 = tasks.get(itemInfo.position);
                changedTask1.setDone(false);
                dataSource.updateTask(changedTask1);

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

    @Override
    public void onEditButtonClick(int position) {
        Intent intent = new Intent(this, EditTaskActivity.class);
        intent.putExtra("editTask", tasks.get(position));
        intent.putExtra("position", position);
        startActivityForResult(intent, REQUEST_CODE_UDPATE_TASK);
    }
}
