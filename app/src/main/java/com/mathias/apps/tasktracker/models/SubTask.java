package com.mathias.apps.tasktracker.models;

import java.io.Serializable;

/**
 * Created by Mathias on 11/02/2016.
 */
public class SubTask implements Serializable {
    private String name;
    private boolean done;

    public SubTask(String name) {
        this.name = name;
    }

    public SubTask(String name, boolean done) {
        this.name = name;
        this.done = done;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
