package com.mathias.apps.tasktracker.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Mathias on 06/02/2016.
 */
public class Task implements Serializable {
    private String name;
    private List<SubTask> subTasks;
    private int color;
    private double timeEstaminated;
    private double timeDone;
    private boolean done;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Task() {
    }

    public Task(String name) {
        this.name = name;
    }

    public Task(String name, List<SubTask> subTasks, int color, double timeEstaminated) {
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public double getTimeEstaminated() {
        return timeEstaminated;
    }

    public void setTimeEstaminated(double timeEstaminated) {
        this.timeEstaminated = timeEstaminated;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
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
