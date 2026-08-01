/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */


/**
 *
 * @author wutyihlaingwin
 */
package com.vitalifeai.repository;

import com.vitalifeai.entity.HealthProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthProfileRepository
        extends JpaRepository<HealthProfile, Long> {

    Optional<HealthProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}