package com.example.studyplanner.repository;

import com.example.studyplanner.model.Subtopic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubtopicRepository extends MongoRepository<Subtopic, String> {
    List<Subtopic> findByStudyPlanId(String studyPlanId);
}
