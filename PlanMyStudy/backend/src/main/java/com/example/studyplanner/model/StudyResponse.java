package com.example.studyplanner.model;

import java.util.List;

public class StudyResponse {

    private Subtopic subtopic;
    private String content;
    private List<String> videos;

    public StudyResponse(Subtopic subtopic, String content, List<String> videos) {
        this.subtopic = subtopic;
        this.content = content;
        this.videos = videos;
    }

    public StudyResponse() {}

    public Subtopic getSubtopic() {
        return subtopic;
    }

    public void setSubtopic(Subtopic subtopic) {
        this.subtopic = subtopic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getVideos() {
        return videos;
    }

    public void setVideos(List<String> videos) {
        this.videos = videos;
    }
}
