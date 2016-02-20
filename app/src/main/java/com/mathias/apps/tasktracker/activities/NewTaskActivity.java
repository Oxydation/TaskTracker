package com.mathias.apps.tasktracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.mathias.apps.tasktracker.R;
import com.mathias.apps.tasktracker.database.TasksDataSource;
import com.mathias.apps.tasktracker.models.SubTask;
import com.mathias.apps.tasktracker.models.Task;

import java.util.ArrayList;
import java.util.List;

public class NewTaskActivity extends AppCompatActivity {

    private TasksDataSource dataSource;
    private List<String> subTasks = new ArrayList<>();

    // http://stackoverflow.com/questions/15393899/how-to-close-activity-and-go-back-to-previous-activity-in-android
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataSource = new TasksDataSource(this);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, subTasks);
       /* final ListView listViewSubtasks = (ListView) findViewById(R.id.listViewSubtasks);
        listViewSubtasks.setAdapter(adapter);
*/
        final EditText taskName = (EditText) findViewById(R.id.editTextTaskName);
        final EditText description = (EditText) findViewById(R.id.editTextDescription);
        final EditText estTime = (EditText) findViewById(R.id.editTextEstTime);
        final Spinner bgColor = (Spinner) findViewById(R.id.spinnerColor);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.background_colors, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        bgColor.setAdapter(adapter);


        //final EditText subTaskName = (EditText) findViewById(R.id.subtaskName);

        /*ImageButton btnAddSubtask = (ImageButton) findViewById(R.id.buttonAddSubTask);
        btnAddSubtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.add(subTaskName.getText().toString());
                subTaskName.setText("");
            }
        });*/

        Button btnSave = (Button) findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get data
                Task task = new Task();
                task.setName(taskName.getText().toString());
                task.setDescription(description.getText().toString());

                task.setSubTasks(new ArrayList<SubTask>());
                /*for (String subtask : subTasks) {
                    task.getSubTasks().add(new SubTask(subtask));
                }*/
                if (!estTime.getText().toString().isEmpty()) {
                    task.setTimeEstaminated(Long.valueOf(estTime.getText().toString()));
                }

                if (task.getName().equals("")) {
                    taskName.setError("No name set!");
                    return;
                }

                // Create task in db
                task = dataSource.createTask(task);

                // Create intent and set result
                Intent data = new Intent();
                data.putExtra("taskId", task.getId());
                setResult(RESULT_OK, data);
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
