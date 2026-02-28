package com.example.studyplanner.controller;

import com.example.studyplanner.model.PlannerRequest;
import com.example.studyplanner.model.StudyPlan;
import com.example.studyplanner.model.Subtopic;
import com.example.studyplanner.service.StudyPlannerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS })
public class StudyPlannerController {

    private static final Logger logger = LoggerFactory.getLogger(StudyPlannerController.class);

    private final StudyPlannerService studyPlannerService;

    public StudyPlannerController(StudyPlannerService studyPlannerService) {
        this.studyPlannerService = studyPlannerService;
    }

    @PostMapping("/plan")
    public ResponseEntity<?> creatwePlan(@RequestBody PlannerRequest request) {
        try {
            StudyPlan plan = studyPlannerService.createStudyPlan(request);
            return ResponseEntity.ok(plan);
        } catch (IllegalArgumentException e) {
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(body);
        }
    }

    @GetMapping("/plan/{id}")
    public ResponseEntity<StudyPlan> getPlan(@PathVariable("id") String id) {
        logger.info("Received request for plan ID: {}", id);
        try {
            StudyPlan plan = studyPlannerService.getStudyPlan(id);
            return ResponseEntity.ok(plan);
        } catch (IllegalArgumentException e) {
            logger.warn("Plan not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error fetching plan: ", e);
            throw e;
        }
    }

    @GetMapping("/plan/{id}/subtopics")
    public ResponseEntity<List<Subtopic>> getSubtopics(@PathVariable("id") String id) {
        logger.info("Received request for subtopics of plan ID: {}", id);
        try {
            List<Subtopic> subtopics = studyPlannerService.getSubtopics(id);
            return ResponseEntity.ok(subtopics);
        } catch (Exception e) {
            logger.error("Error fetching subtopics: ", e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/plan/{studyPlanId}/subtopic/{subtopicId}")
    public ResponseEntity<Subtopic> getSubtopicContent(
            @PathVariable("studyPlanId") String studyPlanId,
            @PathVariable("subtopicId") String subtopicId) {
        try {
            Subtopic subtopic = studyPlannerService.getSubtopicWithContent(subtopicId, studyPlanId);
            return ResponseEntity.ok(subtopic);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/subtopic/{id}/toggle")
    public ResponseEntity<Subtopic> toggleSubtopic(@PathVariable("id") String id) {
        try {
            Subtopic subtopic = studyPlannerService.toggleSubtopicCompletion(id);
            return ResponseEntity.ok(subtopic);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleState(IllegalStateException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleOther(RuntimeException ex) {
        ex.printStackTrace(); // Log the error
        Map<String, String> body = new HashMap<>();
        body.put("error", "Unexpected error: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
