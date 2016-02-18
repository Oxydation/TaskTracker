package com.mathias.apps.tasktracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.mathias.apps.tasktracker.R;
import com.mathias.apps.tasktracker.models.SubTask;
import com.mathias.apps.tasktracker.models.Task;

import java.util.ArrayList;
import java.util.List;

public class NewTaskActivity extends AppCompatActivity {

    private List<String> subTasks = new ArrayList<>();

    // http://stackoverflow.com/questions/15393899/how-to-close-activity-and-go-back-to-previous-activity-in-android
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, subTasks);
       /* final ListView listViewSubtasks = (ListView) findViewById(R.id.listViewSubtasks);
        listViewSubtasks.setAdapter(adapter);
*/
        final EditText taskName = (EditText) findViewById(R.id.editTextTaskName);
        final EditText description = (EditText) findViewById(R.id.editTextDescription);
        final EditText estTime = (EditText) findViewById(R.id.editTextEstTime);
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
                    task.setTimeEstaminated(Double.valueOf(estTime.getText().toString()));
                }

                if (task.getName().equals("")) {
                    taskName.setError("No name set!");
                    return;
                }
                // Create intent and set result
                Intent data = new Intent();
                data.putExtra("createdTask", task);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
