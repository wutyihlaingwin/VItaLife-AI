/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */


/**
 *
 * @author wutyihlaingwin
 */


package com.vitalifeai.repository;

import com.vitalifeai.entity.SupplementRecommendation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplementRepository extends JpaRepository<SupplementRecommendation, Long> {

    List<SupplementRecommendation> findByUserId(Long userId);

    void deleteByUserId(Long userId);

}