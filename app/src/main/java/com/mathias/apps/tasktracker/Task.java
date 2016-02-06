package com.mathias.apps.tasktracker;

import java.util.List;

/**
 * Created by Mathias on 06/02/2016.
 */
public class Task {
    private String name;
    private List<Task> subTasks;

    public Task(){
    }

    public Task(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
