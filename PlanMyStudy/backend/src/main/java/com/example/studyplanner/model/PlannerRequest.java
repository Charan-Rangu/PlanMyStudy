package com.example.studyplanner.model;

import java.time.LocalDate;

public class PlannerRequest {

    private String subject;
    private String topic;
    private double dailyTimeLimit; // hours per day
    private LocalDate deadline;

    public PlannerRequest() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public double getDailyTimeLimit() {
        return dailyTimeLimit;
    }

    public void setDailyTimeLimit(double dailyTimeLimit) {
        this.dailyTimeLimit = dailyTimeLimit;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}
