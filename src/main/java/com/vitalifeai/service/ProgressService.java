/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author wutyihlaingwin
 */
package com.vitalifeai.service;

import com.vitalifeai.entity.Progress;
import com.vitalifeai.repository.ProgressRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProgressService {

    private final ProgressRepository progressRepository;

    public ProgressService(
            ProgressRepository progressRepository) {

        this.progressRepository = progressRepository;
    }

    public List<Progress> getProgressByUserId(Long userId) {

        return progressRepository
                .findByUserIdOrderByProgressDateAsc(userId);
    }

    public Progress saveProgress(Progress progress) {

        return progressRepository.save(progress);
    }
}