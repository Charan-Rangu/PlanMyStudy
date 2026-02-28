package com.example.studyplanner.repository;

import com.example.studyplanner.model.StudyPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyPlanRepository extends MongoRepository<StudyPlan, String> {
}
