package com.example.studyplanner.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "subtopics")
public class Subtopic {

    @Id
    private String id;
    private String name;
    private double estimatedHours;
    private String content;
    private List<String> youtubeLinks = new ArrayList<>();
    private List<String> videoSearchQueries = new ArrayList<>();
    private String studyPlanId;
    private Integer dayNumber;
    private boolean completed = false;

    public Subtopic() {
    }

    public Subtopic(String name, double estimatedHours) {
        this.name = name;
        this.estimatedHours = estimatedHours;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public double getHours() {
        return estimatedHours;
    }

    public void setHours(double hours) {
        this.estimatedHours = hours;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getYoutubeLinks() {
        return youtubeLinks;
    }

    public void setYoutubeLinks(List<String> youtubeLinks) {
        this.youtubeLinks = youtubeLinks;
    }

    public List<String> getVideoSearchQueries() {
        return videoSearchQueries;
    }

    public void setVideoSearchQueries(List<String> videoSearchQueries) {
        this.videoSearchQueries = videoSearchQueries;
    }

    public String getStudyPlanId() {
        return studyPlanId;
    }

    public void setStudyPlanId(String studyPlanId) {
        this.studyPlanId = studyPlanId;
    }

    public Integer getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(Integer dayNumber) {
        this.dayNumber = dayNumber;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }
}
