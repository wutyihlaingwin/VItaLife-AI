/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author wutyihlaingwin
 */
package com.vitalifeai.repository;

import com.vitalifeai.entity.MealPlan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealPlanRepository
        extends JpaRepository<MealPlan, Long> {

    List<MealPlan> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}