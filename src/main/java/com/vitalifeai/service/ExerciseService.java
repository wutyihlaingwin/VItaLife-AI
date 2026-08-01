/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author wutyihlaingwin
 */
package com.vitalifeai.service;

import com.vitalifeai.entity.ExercisePlan;
import com.vitalifeai.repository.ExerciseRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public List<ExercisePlan> getExercisePlanByUserId(Long userId) {
        return exerciseRepository.findByUserId(userId);
    }

    public ExercisePlan saveExercise(ExercisePlan exercisePlan) {
        return exerciseRepository.save(exercisePlan);
    }

    public List<ExercisePlan> saveAllExercises(List<ExercisePlan> exercisePlans) {
        return exerciseRepository.saveAll(exercisePlans);
    }

    @Transactional
    public void deleteExercisePlanByUserId(Long userId) {
        exerciseRepository.deleteByUserId(userId);
    }

    @Transactional
    public List<ExercisePlan> replaceExercisePlan(Long userId,
                                                  List<ExercisePlan> newExercisePlans) {

        exerciseRepository.deleteByUserId(userId);

        return exerciseRepository.saveAll(newExercisePlans);
    }
}