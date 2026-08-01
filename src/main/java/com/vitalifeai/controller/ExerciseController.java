/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author wutyihlaingwin
 */
package com.vitalifeai.controller;

import com.vitalifeai.entity.ExercisePlan;
import com.vitalifeai.entity.HealthProfile;
import com.vitalifeai.entity.User;
import com.vitalifeai.service.ExerciseService;
import com.vitalifeai.service.HealthProfileService;
import com.vitalifeai.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ExerciseController {

    private final ExerciseService exerciseService;
    private final HealthProfileService healthProfileService;
    private final UserService userService;

    public ExerciseController(
            ExerciseService exerciseService,
            HealthProfileService healthProfileService,
            UserService userService) {

        this.exerciseService = exerciseService;
        this.healthProfileService = healthProfileService;
        this.userService = userService;
    }

    @GetMapping("/exercise-planner")
    public String showExercisePlanner(
            HttpSession session,
            Model model) {

        Long userId =
                (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        Optional<HealthProfile> optionalProfile =
                healthProfileService.getProfileByUserId(userId);

        if (optionalProfile.isEmpty()) {

            model.addAttribute("profileExists", false);
            model.addAttribute("exercisePlanExists", false);

            return "exercise-planner";
        }

        HealthProfile profile = optionalProfile.get();

        List<ExercisePlan> exercisePlans =
                exerciseService.getExercisePlanByUserId(userId);

        model.addAttribute("profileExists", true);
        model.addAttribute("healthProfile", profile);
        model.addAttribute("exercisePlans", exercisePlans);

        if (exercisePlans.isEmpty()) {

            model.addAttribute("exercisePlanExists", false);

        } else {

            model.addAttribute("exercisePlanExists", true);

            addExerciseSummary(
                    exercisePlans,
                    profile,
                    model
            );
        }

        return "exercise-planner";
    }

    @PostMapping("/exercise-planner/generate")
    public String generateExercisePlan(
            HttpSession session) {

        Long userId =
                (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        Optional<HealthProfile> optionalProfile =
                healthProfileService.getProfileByUserId(userId);

        if (optionalProfile.isEmpty()) {
            return "redirect:/health-profile";
        }

        User user = userService.getUserById(userId);

        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }

        HealthProfile profile = optionalProfile.get();

        List<ExercisePlan> newExercisePlans =
                createPersonalisedExercisePlan(
                        user,
                        profile
                );

        exerciseService.replaceExercisePlan(
                userId,
                newExercisePlans
        );

        return "redirect:/exercise-planner?generated=true";
    }

    private List<ExercisePlan> createPersonalisedExercisePlan(
            User user,
            HealthProfile profile) {

        List<ExercisePlan> exercises =
                new ArrayList<>();

        String goal = profile.getGoal();
        String activityLevel =
                profile.getActivityLevel();

        if ("Lose Weight".equalsIgnoreCase(goal)) {

            exercises.add(
                    createExercise(
                            user,
                            "Cardio",
                            "Brisk Walking or Light Jogging",
                            determineIntensity(activityLevel),
                            30,
                            220
                    )
            );

            exercises.add(
                    createExercise(
                            user,
                            "Cardio",
                            "Cycling",
                            "Moderate",
                            25,
                            210
                    )
            );

            exercises.add(
                    createExercise(
                            user,
                            "Strength",
                            "Full-Body Circuit Training",
                            "Moderate",
                            25,
                            180
                    )
            );

            exercises.add(
                    createExercise(
                            user,
                            "Flexibility",
                            "Stretching and Mobility",
                            "Low",
                            15,
                            60
                    )
            );

        } else if ("Gain Muscle".equalsIgnoreCase(goal)) {

            exercises.add(
                    createExercise(
                            user,
                            "Strength",
                            "Upper-Body Strength Training",
                            determineStrengthIntensity(
                                    activityLevel
                            ),
                            40,
                            260
                    )
            );

            exercises.add(
                    createExercise(
                            user,
                            "Strength",
                            "Lower-Body Strength Training",
                            determineStrengthIntensity(
                                    activityLevel
                            ),
                            40,
                            300
                    )
            );

            exercises.add(
                    createExercise(
                            user,
                            "Strength",
                            "Core Strength Training",
                            "Moderate",
                            20,
                            140
                    )
            );

            exercises.add(
                    createExercise(
                            user,
                            "Recovery",
                            "Mobility and Recovery Session",
                            "Low",
                            15,
                            50
                    )
            );

        } else if ("Improve Fitness".equalsIgnoreCase(goal)) {

            exercises.add(
                    createExercise(
                            user,
                            "Cardio",
                            "Interval Running",
                            determineIntensity(activityLevel),
                            30,
                            300
                    )
            );

            exercises.add(
                    createExercise(
                            user,
                            "Strength",
                            "Bodyweight Circuit",
                            "Moderate",
                            30,
                            220
                    )
            );

            exercises.add(
                    createExercise(
                            user,
                            "Cardio",
                            "Cycling or Swimming",
                            "Moderate",
                            30,
                            250
                    )
            );

            exercises.add(
                    createExercise(
                            user,
                            "Flexibility",
                            "Yoga and Stretching",
                            "Low",
                            20,
                            80
                    )
            );

        } else {

            exercises.add(
                    createExercise(
                            user,
                            "Cardio",
                            "Brisk Walking",
                            determineIntensity(activityLevel),
                            30,
                            180
                    )
            );

            exercises.add(
                    createExercise(
                            user,
                            "Strength",
                            "Full-Body Strength Training",
                            "Moderate",
                            30,
                            210
                    )
            );

            exercises.add(
                    createExercise(
                            user,
                            "Cardio",
                            "Cycling or Swimming",
                            "Moderate",
                            25,
                            200
                    )
            );

            exercises.add(
                    createExercise(
                            user,
                            "Flexibility",
                            "Stretching and Mobility",
                            "Low",
                            15,
                            60
                    )
            );
        }

        return exercises;
    }

    private ExercisePlan createExercise(
            User user,
            String exerciseType,
            String exerciseName,
            String intensity,
            int durationMinutes,
            double caloriesBurned) {

        ExercisePlan exercisePlan =
                new ExercisePlan();

        exercisePlan.setUser(user);
        exercisePlan.setExerciseType(exerciseType);
        exercisePlan.setExerciseName(exerciseName);
        exercisePlan.setIntensity(intensity);
        exercisePlan.setDurationMinutes(
                durationMinutes
        );
        exercisePlan.setCaloriesBurned(
                caloriesBurned
        );

        return exercisePlan;
    }

    private String determineIntensity(
            String activityLevel) {

        if (activityLevel == null) {
            return "Low";
        }

        if ("Very Active".equalsIgnoreCase(
                activityLevel)
                || "Extremely Active".equalsIgnoreCase(
                        activityLevel)) {

            return "High";
        }

        if ("Moderately Active".equalsIgnoreCase(
                activityLevel)
                || "Lightly Active".equalsIgnoreCase(
                        activityLevel)) {

            return "Moderate";
        }

        return "Low";
    }

    private String determineStrengthIntensity(
            String activityLevel) {

        if (activityLevel == null) {
            return "Moderate";
        }

        if ("Very Active".equalsIgnoreCase(
                activityLevel)
                || "Extremely Active".equalsIgnoreCase(
                        activityLevel)) {

            return "High";
        }

        return "Moderate";
    }

    private void addExerciseSummary(
            List<ExercisePlan> exercisePlans,
            HealthProfile profile,
            Model model) {

        int totalDuration =
                exercisePlans.stream()
                        .mapToInt(
                                ExercisePlan::getDurationMinutes
                        )
                        .sum();

        double totalCaloriesBurned =
                exercisePlans.stream()
                        .mapToDouble(
                                ExercisePlan::getCaloriesBurned
                        )
                        .sum();

        model.addAttribute(
                "totalDuration",
                totalDuration
        );

        model.addAttribute(
                "totalCaloriesBurned",
                Math.round(totalCaloriesBurned)
        );

        model.addAttribute(
                "exerciseCount",
                exercisePlans.size()
        );

        model.addAttribute(
                "healthGoal",
                profile.getGoal()
        );

        model.addAttribute(
                "activityLevel",
                profile.getActivityLevel()
        );
    }
}