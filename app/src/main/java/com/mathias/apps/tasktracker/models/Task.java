package com.mathias.apps.tasktracker.models;

import android.graphics.Color;

import java.util.List;

/**
 * Created by Mathias on 06/02/2016.
 */
public class Task {
    private String name;
    private List<Task> subTasks;
    private Color color;
    private double timeEstaminated;
    private double timeDone;
    private boolean done;

    public Task() {
    }

    public Task(String name) {
        this.name = name;
    }

    public Task(String name, List<Task> subTasks, Color color, double timeEstaminated) {
        this.name = name;
        this.subTasks = subTasks;
        this.color = color;
        this.timeEstaminated = timeEstaminated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getTimeEstaminated() {
        return timeEstaminated;
    }

    public void setTimeEstaminated(double timeEstaminated) {
        this.timeEstaminated = timeEstaminated;
    }

    public List<Task> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<Task> subTasks) {
        this.subTasks = subTasks;
    }

    public double getTimeDone() {
        return timeDone;
    }

    public void setTimeDone(double timeDone) {
        this.timeDone = timeDone;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
