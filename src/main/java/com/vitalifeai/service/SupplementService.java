/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author wutyihlaingwin
 */
package com.vitalifeai.service;

import com.vitalifeai.entity.SupplementRecommendation;
import com.vitalifeai.repository.SupplementRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplementService {

    private final SupplementRepository supplementRepository;

    public SupplementService(SupplementRepository supplementRepository) {
        this.supplementRepository = supplementRepository;
    }

    public List<SupplementRecommendation> getSupplementRecommendationsByUserId(Long userId) {
        return supplementRepository.findByUserId(userId);
    }

    public SupplementRecommendation saveSupplement(SupplementRecommendation supplement) {
        return supplementRepository.save(supplement);
    }

    public List<SupplementRecommendation> saveAllSupplements(List<SupplementRecommendation> supplements) {
        return supplementRepository.saveAll(supplements);
    }

    @Transactional
    public void deleteSupplementsByUserId(Long userId) {
        supplementRepository.deleteByUserId(userId);
    }

    @Transactional
    public List<SupplementRecommendation> replaceSupplements(
            Long userId,
            List<SupplementRecommendation> newSupplements) {

        supplementRepository.deleteByUserId(userId);

        return supplementRepository.saveAll(newSupplements);
    }
}
