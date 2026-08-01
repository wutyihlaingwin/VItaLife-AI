/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author wutyihlaingwin
 */
package com.vitalifeai.controller;

import com.vitalifeai.entity.Progress;
import com.vitalifeai.entity.User;
import com.vitalifeai.service.ProgressService;
import com.vitalifeai.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProgressController {

    private final ProgressService progressService;
    private final UserService userService;

    public ProgressController(
            ProgressService progressService,
            UserService userService) {

        this.progressService = progressService;
        this.userService = userService;
    }

    @GetMapping("/progress")
    public String showProgressDashboard(
            HttpSession session,
            Model model) {

        Long userId =
                (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        List<Progress> progressRecords =
                progressService.getProgressByUserId(userId);

        model.addAttribute(
                "userName",
                session.getAttribute("loggedInUserName")
        );

        model.addAttribute(
                "progressRecords",
                progressRecords
        );

        model.addAttribute(
                "progressExists",
                !progressRecords.isEmpty()
        );

        if (!progressRecords.isEmpty()) {

            Progress latestRecord =
                    progressRecords.get(
                            progressRecords.size() - 1
                    );

            model.addAttribute(
                    "latestProgress",
                    latestRecord
            );

            model.addAttribute(
                    "recordCount",
                    progressRecords.size()
            );
        }

        return "progress-dashboard";
    }

    @PostMapping("/progress")
    public String saveProgress(
            @RequestParam String progressDate,
            @RequestParam double weight,
            @RequestParam double bmi,
            @RequestParam int calorieIntake,
            @RequestParam int exerciseMinutes,
            HttpSession session,
            Model model) {

        Long userId =
                (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        if (weight < 30 || weight > 300) {
            model.addAttribute(
                    "errorMessage",
                    "Weight must be between 30 kg and 300 kg."
            );

            return loadProgressPage(
                    userId,
                    session,
                    model
            );
        }

        if (bmi < 10 || bmi > 80) {
            model.addAttribute(
                    "errorMessage",
                    "BMI must be between 10 and 80."
            );

            return loadProgressPage(
                    userId,
                    session,
                    model
            );
        }

        if (calorieIntake < 0 || calorieIntake > 10000) {
            model.addAttribute(
                    "errorMessage",
                    "Calorie intake must be between 0 and 10,000."
            );

            return loadProgressPage(
                    userId,
                    session,
                    model
            );
        }

        if (exerciseMinutes < 0 || exerciseMinutes > 1440) {
            model.addAttribute(
                    "errorMessage",
                    "Exercise minutes must be between 0 and 1,440."
            );

            return loadProgressPage(
                    userId,
                    session,
                    model
            );
        }

        User user = userService.getUserById(userId);

        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }

        Progress progress = new Progress();

        progress.setUser(user);
        progress.setProgressDate(
                LocalDate.parse(progressDate)
        );
        progress.setWeight(weight);
        progress.setBmi(bmi);
        progress.setCalorieIntake(calorieIntake);
        progress.setExerciseMinutes(exerciseMinutes);

        progressService.saveProgress(progress);

        return "redirect:/progress?saved=true";
    }

    private String loadProgressPage(
            Long userId,
            HttpSession session,
            Model model) {

        List<Progress> progressRecords =
                progressService.getProgressByUserId(userId);

        model.addAttribute(
                "userName",
                session.getAttribute("loggedInUserName")
        );

        model.addAttribute(
                "progressRecords",
                progressRecords
        );

        model.addAttribute(
                "progressExists",
                !progressRecords.isEmpty()
        );

        if (!progressRecords.isEmpty()) {

            Progress latestRecord =
                    progressRecords.get(
                            progressRecords.size() - 1
                    );

            model.addAttribute(
                    "latestProgress",
                    latestRecord
            );

            model.addAttribute(
                    "recordCount",
                    progressRecords.size()
            );
        }

        return "progress-dashboard";
    }
}