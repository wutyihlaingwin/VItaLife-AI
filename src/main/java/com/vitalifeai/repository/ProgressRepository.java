/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author wutyihlaingwin
 */
package com.vitalifeai.repository;

import com.vitalifeai.entity.Progress;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgressRepository extends JpaRepository<Progress, Long> {

    List<Progress> findByUserIdOrderByProgressDateAsc(Long userId);

}