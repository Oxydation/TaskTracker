package com.mathias.apps.tasktracker.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Mathias on 06/02/2016.
 */
public class Task implements Serializable {
    private long id;
    private String name;
    private List<SubTask> subTasks;
    private int color;
    private long timeEstaminated;
    private long timeDone;
    private boolean done;
    private String description;

    //TODO: add created and modified datetime

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

    public Task(String name, List<SubTask> subTasks, int color, long timeEstaminated) {
        this.name = name;
        this.subTasks = subTasks;
        this.color = color;
        this.timeEstaminated = timeEstaminated;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getTimeEstaminated() {
        return timeEstaminated;
    }

    public void setTimeEstaminated(long timeEstaminated) {
        this.timeEstaminated = timeEstaminated;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public long getTimeDone() {
        return timeDone;
    }

    public void setTimeDone(long timeDone) {
        this.timeDone = timeDone;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return name;
    }
}
