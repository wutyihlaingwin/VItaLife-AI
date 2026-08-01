/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author wutyihlaingwin
 */
package com.vitalifeai.service;

import com.vitalifeai.entity.HealthProfile;
import com.vitalifeai.repository.HealthProfileRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HealthProfileService {

    @Autowired
    private HealthProfileRepository healthProfileRepository;

    public List<HealthProfile> getAllProfiles() {
        return healthProfileRepository.findAll();
    }

    public HealthProfile saveProfile(HealthProfile profile) {
        return healthProfileRepository.save(profile);
    }

    public Optional<HealthProfile> getProfileByUserId(Long userId) {
        return healthProfileRepository.findByUserId(userId);
    }

    public boolean userHasProfile(Long userId) {
        return healthProfileRepository.existsByUserId(userId);
    }
}