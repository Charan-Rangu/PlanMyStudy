package com.example.studyplanner.model;

import java.util.ArrayList;
import java.util.List;

public class DayPlan {

    private int day;
    private List<String> tasks = new ArrayList<>();

    public DayPlan() {
    }

    public DayPlan(int day) {
        this.day = day;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public List<String> getTasks() {
        return tasks;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }
}

