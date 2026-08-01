/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */


/**
 *
 * @author wutyihlaingwin
 */


package com.vitalifeai.repository;

import com.vitalifeai.entity.ExercisePlan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<ExercisePlan, Long> {

    List<ExercisePlan> findByUserId(Long userId);

    void deleteByUserId(Long userId);

}