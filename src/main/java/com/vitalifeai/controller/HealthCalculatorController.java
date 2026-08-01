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
import com.vitalifeai.service.HealthProfileService;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HealthCalculatorController {

    private final HealthProfileService healthProfileService;

    public HealthCalculatorController(
            HealthProfileService healthProfileService) {

        this.healthProfileService = healthProfileService;
    }

    @GetMapping("/health-calculator")
    public String showCalculator(
            HttpSession session,
            Model model) {

        Long userId =
                (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        Optional<HealthProfile> optionalProfile =
                healthProfileService.getProfileByUserId(userId);

        if (optionalProfile.isPresent()) {

            HealthProfile profile = optionalProfile.get();

            model.addAttribute("profileExists", true);
            model.addAttribute("age", profile.getAge());
            model.addAttribute("gender", profile.getGender());
            model.addAttribute("height", profile.getHeight());
            model.addAttribute("weight", profile.getWeight());

            model.addAttribute(
                    "activityLevel",
                    profile.getActivityLevel()
            );

        } else {

            model.addAttribute("profileExists", false);
        }

        model.addAttribute("resultExists", false);

        return "health-calculator";
    }

    @PostMapping("/health-calculator")
    public String calculateHealthResults(
            @RequestParam int age,
            @RequestParam String gender,
            @RequestParam double height,
            @RequestParam double weight,
            @RequestParam String activityLevel,
            HttpSession session,
            Model model) {

        Long userId =
                (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        model.addAttribute("age", age);
        model.addAttribute("gender", gender);
        model.addAttribute("height", height);
        model.addAttribute("weight", weight);
        model.addAttribute("activityLevel", activityLevel);
        model.addAttribute("profileExists", true);

        if (age < 16 || age > 100) {

            model.addAttribute(
                    "errorMessage",
                    "Age must be between 16 and 100."
            );

            model.addAttribute("resultExists", false);

            return "health-calculator";
        }

        if (height < 100 || height > 250) {

            model.addAttribute(
                    "errorMessage",
                    "Height must be between 100 cm and 250 cm."
            );

            model.addAttribute("resultExists", false);

            return "health-calculator";
        }

        if (weight < 30 || weight > 300) {

            model.addAttribute(
                    "errorMessage",
                    "Weight must be between 30 kg and 300 kg."
            );

            model.addAttribute("resultExists", false);

            return "health-calculator";
        }

        double bmi = calculateBmi(weight, height);

        String bmiCategory =
                determineBmiCategory(bmi);

        long bmr =
                calculateBmr(
                        age,
                        gender,
                        height,
                        weight
                );

        long tdee =
                calculateTdee(
                        bmr,
                        activityLevel
                );

        model.addAttribute("bmi", bmi);
        model.addAttribute("bmiCategory", bmiCategory);
        model.addAttribute("bmr", bmr);
        model.addAttribute("tdee", tdee);
        model.addAttribute("resultExists", true);

        return "health-calculator";
    }

    private double calculateBmi(
            double weight,
            double height) {

        double heightMetres = height / 100.0;

        double bmi =
                weight / (heightMetres * heightMetres);

        return Math.round(bmi * 10.0) / 10.0;
    }

    private String determineBmiCategory(double bmi) {

        if (bmi < 18.5) {
            return "Underweight";
        }

        if (bmi < 25) {
            return "Normal weight";
        }

        if (bmi < 30) {
            return "Overweight";
        }

        return "Obesity range";
    }

    private long calculateBmr(
            int age,
            String gender,
            double height,
            double weight) {

        double baseCalculation =
                (10 * weight)
                + (6.25 * height)
                - (5 * age);

        double bmr;

        if ("Male".equalsIgnoreCase(gender)) {

            bmr = baseCalculation + 5;

        } else if ("Female".equalsIgnoreCase(gender)) {

            bmr = baseCalculation - 161;

        } else {

            bmr = baseCalculation - 78;
        }

        return Math.round(bmr);
    }

    private long calculateTdee(
            long bmr,
            String activityLevel) {

        double multiplier =
                getActivityMultiplier(activityLevel);

        return Math.round(bmr * multiplier);
    }

    private double getActivityMultiplier(
            String activityLevel) {

        if (activityLevel == null) {
            return 1.2;
        }

        return switch (activityLevel) {

            case "Lightly Active" -> 1.375;

            case "Moderately Active" -> 1.55;

            case "Very Active" -> 1.725;

            case "Extremely Active" -> 1.9;

            default -> 1.2;
        };
    }
}