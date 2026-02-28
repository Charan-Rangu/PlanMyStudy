package com.example.studyplanner.service;

import com.example.studyplanner.model.PlannerRequest;
import com.example.studyplanner.model.StudyPlan;
import com.example.studyplanner.model.Subtopic;
import com.example.studyplanner.repository.StudyPlanRepository;
import com.example.studyplanner.repository.SubtopicRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class StudyPlannerService {

    private final OpenAIClient openAIClient;
    private final StudyPlanRepository studyPlanRepository;
    private final SubtopicRepository subtopicRepository;

    public StudyPlannerService(OpenAIClient openAIClient,
            StudyPlanRepository studyPlanRepository,
            SubtopicRepository subtopicRepository) {
        this.openAIClient = openAIClient;
        this.studyPlanRepository = studyPlanRepository;
        this.subtopicRepository = subtopicRepository;
    }

    public List<Subtopic> getSubtopics(String studyPlanId) {
        return subtopicRepository.findByStudyPlanId(studyPlanId);
    }

    public Subtopic toggleSubtopicCompletion(String subtopicId) {
        Subtopic subtopic = subtopicRepository.findById(subtopicId)
                .orElseThrow(() -> new IllegalArgumentException("Subtopic not found"));
        subtopic.setCompleted(!subtopic.isCompleted());
        return subtopicRepository.save(subtopic);
    }

    public StudyPlan createStudyPlan(PlannerRequest request) {
        validateRequest(request);

        long daysUntilDeadline = ChronoUnit.DAYS.between(LocalDate.now(), request.getDeadline());

        if (daysUntilDeadline <= 0) {
            throw new IllegalArgumentException("Deadline must be in the future.");
        }

        // Generate subtopics using OpenAI
        List<Subtopic> subtopics = openAIClient.generateSubtopics(request.getTopic(), request.getSubject());

        if (subtopics.isEmpty()) {
            throw new IllegalStateException("AI did not return any subtopics.");
        }

        // Schedule subtopics
        List<Subtopic> scheduledSubtopics = distributeSubtopics(subtopics, request.getDailyTimeLimit(),
                daysUntilDeadline);

        StudyPlan studyPlan = new StudyPlan();
        studyPlan.setSubject(request.getSubject());
        studyPlan.setTopic(request.getTopic());
        studyPlan.setDailyTimeLimit(request.getDailyTimeLimit());
        studyPlan.setDeadline(request.getDeadline());
        studyPlan.setSubtopics(scheduledSubtopics);

        studyPlan = studyPlanRepository.save(studyPlan);

        for (Subtopic subtopic : scheduledSubtopics) {
            subtopic.setStudyPlanId(studyPlan.getId());
            subtopicRepository.save(subtopic);
        }

        return studyPlan;
    }

    private List<Subtopic> distributeSubtopics(List<Subtopic> subtopics, double dailyLimit, long daysAvailable) {
        List<Subtopic> scheduled = new java.util.ArrayList<>();
        int currentDay = 1;
        double currentDayHours = 0;

        for (Subtopic subtopic : subtopics) {
            double remainingHoursForSubtopic = subtopic.getEstimatedHours();

            while (remainingHoursForSubtopic > 0 && currentDay <= daysAvailable) {
                double spaceOnCurrentDay = dailyLimit - currentDayHours;

                if (spaceOnCurrentDay <= 0.1) { // Move to next day if full
                    currentDay++;
                    currentDayHours = 0;
                    continue;
                }

                if (remainingHoursForSubtopic <= spaceOnCurrentDay) {
                    // Fits in current day
                    Subtopic s = new Subtopic(subtopic.getName(), remainingHoursForSubtopic);
                    s.setDayNumber(currentDay);
                    scheduled.add(s);
                    currentDayHours += remainingHoursForSubtopic;
                    remainingHoursForSubtopic = 0;
                } else {
                    // Split needed
                    Subtopic part1 = new Subtopic(subtopic.getName() + " (Part 1)", spaceOnCurrentDay);
                    part1.setDayNumber(currentDay);
                    scheduled.add(part1);

                    remainingHoursForSubtopic -= spaceOnCurrentDay;
                    currentDay++;
                    currentDayHours = 0;

                    // Rename remaining part for next iteration
                    subtopic.setName(subtopic.getName() + " (Part 2)");
                }
            }
        }

        return scheduled;
    }

    public Subtopic getSubtopicWithContent(String subtopicId, String studyPlanId) {

        Subtopic subtopic = subtopicRepository.findById(subtopicId)
                .orElseThrow(() -> new IllegalArgumentException("Subtopic not found"));

        if (!subtopic.getStudyPlanId().equals(studyPlanId)) {
            throw new IllegalArgumentException("Subtopic does not belong to this plan.");
        }

        if (subtopic.getContent() == null || subtopic.getContent().isBlank()) {
            StudyPlan studyPlan = getStudyPlan(studyPlanId);

            subtopic = openAIClient.generateSubtopicContent(
                    subtopic,
                    studyPlan.getTopic(),
                    studyPlan.getSubject());

            subtopicRepository.save(subtopic);
        }

        return subtopic;
    }

    public StudyPlan getStudyPlan(String id) {
        return studyPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Study plan not found"));
    }

    private void validateRequest(PlannerRequest request) {
        if (request.getTopic() == null || request.getTopic().isBlank())
            throw new IllegalArgumentException("Topic is required");

        if (request.getDailyTimeLimit() <= 0)
            throw new IllegalArgumentException("Daily time must be > 0");

        if (request.getDeadline() == null)
            throw new IllegalArgumentException("Deadline is required");
    }
}
