/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author wutyihlaingwin
 */
package com.vitalifeai.controller;

import com.vitalifeai.entity.HealthProfile;
import com.vitalifeai.entity.User;
import com.vitalifeai.service.HealthProfileService;
import com.vitalifeai.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HealthProfileController {

    private final HealthProfileService healthProfileService;
    private final UserService userService;

    public HealthProfileController(
            HealthProfileService healthProfileService,
            UserService userService) {

        this.healthProfileService = healthProfileService;
        this.userService = userService;
    }

    @GetMapping("/health-profile")
    public String showHealthProfile(
            HttpSession session,
            Model model) {

        Long userId =
                (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        Optional<HealthProfile> existingProfile =
                healthProfileService.getProfileByUserId(userId);

        if (existingProfile.isPresent()) {
            model.addAttribute(
                    "healthProfile",
                    existingProfile.get()
            );
        } else {
            model.addAttribute(
                    "healthProfile",
                    new HealthProfile()
            );
        }

        return "health-profile";
    }

    @PostMapping("/health-profile")
    public String saveHealthProfile(
            @RequestParam int age,
            @RequestParam String gender,
            @RequestParam double height,
            @RequestParam double weight,
            @RequestParam String activityLevel,
            @RequestParam String goal,
            HttpSession session,
            Model model) {

        Long userId =
                (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        if (age < 16 || age > 100) {
            model.addAttribute(
                    "errorMessage",
                    "Age must be between 16 and 100."
            );

            return loadFormAfterError(
                    userId,
                    model
            );
        }

        if (height < 100 || height > 250) {
            model.addAttribute(
                    "errorMessage",
                    "Height must be between 100 cm and 250 cm."
            );

            return loadFormAfterError(
                    userId,
                    model
            );
        }

        if (weight < 30 || weight > 300) {
            model.addAttribute(
                    "errorMessage",
                    "Weight must be between 30 kg and 300 kg."
            );

            return loadFormAfterError(
                    userId,
                    model
            );
        }

        User user = userService.getUserById(userId);

        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }

        HealthProfile profile =
                healthProfileService
                        .getProfileByUserId(userId)
                        .orElse(new HealthProfile());

        profile.setUser(user);
        profile.setAge(age);
        profile.setGender(gender);
        profile.setHeight(height);
        profile.setWeight(weight);
        profile.setActivityLevel(activityLevel);
        profile.setGoal(goal);

        healthProfileService.saveProfile(profile);

        return "redirect:/health-profile?saved=true";
    }

    private String loadFormAfterError(
            Long userId,
            Model model) {

        HealthProfile profile =
                healthProfileService
                        .getProfileByUserId(userId)
                        .orElse(new HealthProfile());

        model.addAttribute(
                "healthProfile",
                profile
        );

        return "health-profile";
    }
}