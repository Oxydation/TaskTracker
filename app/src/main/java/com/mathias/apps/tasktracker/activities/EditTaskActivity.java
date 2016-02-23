package com.mathias.apps.tasktracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mathias.apps.tasktracker.R;
import com.mathias.apps.tasktracker.database.TasksDataSource;
import com.mathias.apps.tasktracker.models.SubTask;
import com.mathias.apps.tasktracker.models.Task;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class EditTaskActivity extends AppCompatActivity {
    private TasksDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataSource = new TasksDataSource(this);

        final Task editTask = dataSource.getTask(getIntent().getExtras().getLong("taskId"));

        final EditText taskName = (EditText) findViewById(R.id.editTextTaskName);
        final EditText description = (EditText) findViewById(R.id.editTextDescription);
        final EditText estTime = (EditText) findViewById(R.id.editTextEstTime);
        final EditText estTimeHours = (EditText) findViewById(R.id.editTextEstTimeHours);
        final EditText doneTimeMinutes = (EditText) findViewById(R.id.editTextTimeDone);
        final EditText doneTimeHours = (EditText) findViewById(R.id.editTextTimeDoneHours);

        taskName.setText(editTask.getName());
        description.setText(editTask.getDescription());
        estTime.setText(String.valueOf(editTask.getTimeEstaminated() % 60));
        estTimeHours.setText(String.valueOf((int) editTask.getTimeEstaminated() / 60));

        doneTimeHours.setText(String.valueOf((int) TimeUnit.SECONDS.toHours((long) editTask.getTimeDone())));
        doneTimeMinutes.setText(String.valueOf((long) (editTask.getTimeDone() % 3600)));

        Button btnSave = (Button) findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (taskName.getText().toString() != null && taskName.getText().toString().trim().isEmpty()) {
                    taskName.setError(getString(R.string.error_no_name));
                    return;
                } else {
                    editTask.setName(taskName.getText().toString());
                }

                editTask.setDescription(description.getText().toString());

                editTask.setSubTasks(new ArrayList<SubTask>());
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
                    editTask.setTimeEstaminated(hours * 3600 + minutes * 60);
                } else {
                    editTask.setTimeEstaminated(0);
                }

                // Get time done
                if (!doneTimeMinutes.getText().toString().isEmpty() || !doneTimeHours.getText().toString().isEmpty()) {
                    int minutes = 0;
                    if (!doneTimeMinutes.getText().toString().isEmpty()) {
                        minutes = Integer.valueOf(doneTimeMinutes.getText().toString());
                    }

                    int hours = 0;
                    if (!doneTimeHours.getText().toString().isEmpty()) {
                        hours = Integer.valueOf(doneTimeHours.getText().toString());
                    }
                    editTask.setTimeDone(hours * 3600 + minutes * 60);
                } else {
                    editTask.setTimeDone(0);
                }

                // Update task
                dataSource.updateTask(editTask);

                // Create intent and set result
                Intent data = new Intent();
                data.putExtra("taskId", editTask.getId());
                setResult(RESULT_OK, data);
                finish();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
