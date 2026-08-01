package com.vitalifeai.controller;

import com.vitalifeai.entity.HealthProfile;
import com.vitalifeai.service.HealthProfileService;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final HealthProfileService healthProfileService;

    public HomeController(
            HealthProfileService healthProfileService) {

        this.healthProfileService = healthProfileService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(
            HttpSession session,
            Model model) {

        Long loggedInUserId =
                (Long) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "userName",
                session.getAttribute("loggedInUserName")
        );

        model.addAttribute(
                "userEmail",
                session.getAttribute("loggedInUserEmail")
        );

        Optional<HealthProfile> optionalProfile =
                healthProfileService
                        .getProfileByUserId(loggedInUserId);

        if (optionalProfile.isPresent()) {

            HealthProfile profile = optionalProfile.get();

            double bmi = calculateBmi(
                    profile.getWeight(),
                    profile.getHeight()
            );

            String bmiCategory =
                    determineBmiCategory(bmi);

            long dailyCalories =
                    calculateDailyCalories(profile);

            int exerciseGoal =
                    determineExerciseGoal(
                            profile.getActivityLevel()
                    );

            model.addAttribute("profileExists", true);
            model.addAttribute("healthProfile", profile);
            model.addAttribute("bmi", bmi);
            model.addAttribute("bmiCategory", bmiCategory);
            model.addAttribute("dailyCalories", dailyCalories);
            model.addAttribute("exerciseGoal", exerciseGoal);

        } else {

            model.addAttribute("profileExists", false);
        }

        return "dashboard";
    }

    private double calculateBmi(
            double weightKg,
            double heightCm) {

        double heightMetres = heightCm / 100.0;

        double bmi =
                weightKg / (heightMetres * heightMetres);

        return Math.round(bmi * 10.0) / 10.0;
    }

    private String determineBmiCategory(double bmi) {

        if (bmi < 18.5) {
            return "Underweight range";
        }

        if (bmi < 25) {
            return "Normal weight range";
        }

        if (bmi < 30) {
            return "Overweight range";
        }

        return "Obesity range";
    }

    private long calculateDailyCalories(
            HealthProfile profile) {

        double baseBmr =
                (10 * profile.getWeight())
                + (6.25 * profile.getHeight())
                - (5 * profile.getAge());

        double bmr;

        if ("Male".equalsIgnoreCase(profile.getGender())) {

            bmr = baseBmr + 5;

        } else if ("Female".equalsIgnoreCase(
                profile.getGender())) {

            bmr = baseBmr - 161;

        } else {

            bmr = baseBmr - 78;
        }

        double activityMultiplier =
                getActivityMultiplier(
                        profile.getActivityLevel()
                );

        double tdee = bmr * activityMultiplier;

        return Math.round(tdee);
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

    private int determineExerciseGoal(
            String activityLevel) {

        if (activityLevel == null) {
            return 30;
        }

        return switch (activityLevel) {

            case "Lightly Active" -> 35;

            case "Moderately Active" -> 45;

            case "Very Active" -> 60;

            case "Extremely Active" -> 75;

            default -> 30;
        };
    }
}