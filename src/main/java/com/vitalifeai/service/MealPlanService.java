/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author wutyihlaingwin
 */
package com.vitalifeai.service;

import com.vitalifeai.entity.MealPlan;
import com.vitalifeai.repository.MealPlanRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;

    public MealPlanService(MealPlanRepository mealPlanRepository) {
        this.mealPlanRepository = mealPlanRepository;
    }

    public List<MealPlan> getMealPlanByUserId(Long userId) {
        return mealPlanRepository.findByUserId(userId);
    }

    public MealPlan saveMeal(MealPlan mealPlan) {
        return mealPlanRepository.save(mealPlan);
    }

    public List<MealPlan> saveAllMeals(List<MealPlan> mealPlans) {
        return mealPlanRepository.saveAll(mealPlans);
    }

    @Transactional
    public void deleteMealPlanByUserId(Long userId) {
        mealPlanRepository.deleteByUserId(userId);
    }

    @Transactional
    public List<MealPlan> replaceMealPlan(Long userId,
                                          List<MealPlan> newMealPlans) {

        mealPlanRepository.deleteByUserId(userId);

        return mealPlanRepository.saveAll(newMealPlans);
    }
}
