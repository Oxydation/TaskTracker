package com.mathias.apps.tasktracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mathias.apps.tasktracker.R;
import com.mathias.apps.tasktracker.models.SubTask;
import com.mathias.apps.tasktracker.models.Task;

import java.util.ArrayList;

public class EditTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Task editTask = (Task) getIntent().getExtras().get("editTask");
        final int position = getIntent().getIntExtra("position", 0);

        final EditText taskName = (EditText) findViewById(R.id.editTextTaskName);
        final EditText description = (EditText) findViewById(R.id.editTextDescription);
        final EditText estTime = (EditText) findViewById(R.id.editTextEstTime);
        final EditText estTimeHours = (EditText) findViewById(R.id.editTextEstTimeHours);

        taskName.setText(editTask.getName());
        description.setText(editTask.getDescription());
        estTime.setText(String.valueOf(editTask.getTimeEstaminated() % 60));
        estTimeHours.setText(String.valueOf((int) editTask.getTimeEstaminated() / 60));

        Button btnSave = (Button) findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get data
                Task task = editTask;
                task.setName(taskName.getText().toString());
                task.setDescription(description.getText().toString());

                task.setSubTasks(new ArrayList<SubTask>());
                /*for (String subtask : subTasks) {
                    task.getSubTasks().add(new SubTask(subtask));
                }*/
                if (!estTime.getText().toString().isEmpty() || !estTimeHours.getText().toString().isEmpty()) {
                    int minutes = 0;
                    if (!estTime.getText().toString().isEmpty()) {
                        minutes = Integer.valueOf(estTime.getText().toString());
                    }

                    int hours = 0;
                    if (!estTime.getText().toString().isEmpty()) {
                        hours = Integer.valueOf(estTimeHours.getText().toString());
                    }
                    task.setTimeEstaminated(hours * 60 + minutes);
                } else {
                    task.setTimeEstaminated(0);
                }

                if (task.getName().equals("")) {
                    taskName.setError("No name set!");
                    return;
                }
                // Create intent and set result
                Intent data = new Intent();
                data.putExtra("updatedTask", task);
                data.putExtra("position", position);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
