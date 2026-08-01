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
import com.vitalifeai.entity.MealPlan;
import com.vitalifeai.entity.User;
import com.vitalifeai.service.HealthProfileService;
import com.vitalifeai.service.MealPlanService;
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
public class MealController {

    private final MealPlanService mealPlanService;
    private final HealthProfileService healthProfileService;
    private final UserService userService;

    public MealController(
            MealPlanService mealPlanService,
            HealthProfileService healthProfileService,
            UserService userService) {

        this.mealPlanService = mealPlanService;
        this.healthProfileService = healthProfileService;
        this.userService = userService;
    }

    @GetMapping("/meal-planner")
    public String showMealPlanner(
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
            model.addAttribute("mealPlanExists", false);

            return "meal-planner";
        }

        HealthProfile profile = optionalProfile.get();

        List<MealPlan> mealPlans =
                mealPlanService.getMealPlanByUserId(userId);

        model.addAttribute("profileExists", true);
        model.addAttribute("healthProfile", profile);
        model.addAttribute("mealPlans", mealPlans);

        if (mealPlans.isEmpty()) {

            model.addAttribute("mealPlanExists", false);

        } else {

            model.addAttribute("mealPlanExists", true);

            addMealPlanSummary(
                    mealPlans,
                    profile,
                    model
            );
        }

        return "meal-planner";
    }

    @PostMapping("/meal-planner/generate")
    public String generateMealPlan(
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

        int targetCalories =
                calculateTargetCalories(profile);

        List<MealPlan> newMealPlans =
                createPersonalisedMealPlan(
                        user,
                        profile.getGoal(),
                        targetCalories
                );

        mealPlanService.replaceMealPlan(
                userId,
                newMealPlans
        );

        return "redirect:/meal-planner?generated=true";
    }

    private List<MealPlan> createPersonalisedMealPlan(
            User user,
            String goal,
            int targetCalories) {

        List<MealPlan> meals = new ArrayList<>();

        int breakfastCalories =
                (int) Math.round(targetCalories * 0.25);

        int lunchCalories =
                (int) Math.round(targetCalories * 0.35);

        int dinnerCalories =
                (int) Math.round(targetCalories * 0.30);

        int snackCalories =
                targetCalories
                - breakfastCalories
                - lunchCalories
                - dinnerCalories;

        String breakfastName;
        String lunchName;
        String dinnerName;
        String snackName;

        if ("Lose Weight".equalsIgnoreCase(goal)) {

            breakfastName =
                    "Oatmeal with berries and boiled egg";

            lunchName =
                    "Grilled chicken salad with brown rice";

            dinnerName =
                    "Baked salmon with steamed vegetables";

            snackName =
                    "Greek yogurt with fresh fruit";

        } else if ("Gain Muscle".equalsIgnoreCase(goal)) {

            breakfastName =
                    "Protein oatmeal with banana and eggs";

            lunchName =
                    "Chicken breast with rice and avocado";

            dinnerName =
                    "Lean beef with sweet potato and vegetables";

            snackName =
                    "Protein shake with peanut butter";

        } else if ("Improve Fitness".equalsIgnoreCase(goal)) {

            breakfastName =
                    "Wholegrain toast, eggs and fresh fruit";

            lunchName =
                    "Turkey quinoa bowl with vegetables";

            dinnerName =
                    "Grilled fish with rice and mixed salad";

            snackName =
                    "Greek yogurt with nuts and banana";

        } else {

            breakfastName =
                    "Oatmeal, banana and boiled eggs";

            lunchName =
                    "Chicken rice bowl with vegetables";

            dinnerName =
                    "Salmon with potatoes and vegetables";

            snackName =
                    "Greek yogurt with mixed nuts";
        }

        meals.add(
                createMeal(
                        user,
                        "Breakfast",
                        breakfastName,
                        breakfastCalories
                )
        );

        meals.add(
                createMeal(
                        user,
                        "Lunch",
                        lunchName,
                        lunchCalories
                )
        );

        meals.add(
                createMeal(
                        user,
                        "Dinner",
                        dinnerName,
                        dinnerCalories
                )
        );

        meals.add(
                createMeal(
                        user,
                        "Snack",
                        snackName,
                        snackCalories
                )
        );

        return meals;
    }

    private MealPlan createMeal(
            User user,
            String mealType,
            String mealName,
            int calories) {

        MealPlan meal = new MealPlan();

        meal.setUser(user);
        meal.setMealType(mealType);
        meal.setMealName(mealName);
        meal.setCalories(calories);

        double protein =
                (calories * 0.30) / 4.0;

        double carbohydrates =
                (calories * 0.40) / 4.0;

        double fat =
                (calories * 0.30) / 9.0;

        meal.setProtein(roundOneDecimal(protein));
        meal.setCarbohydrates(
                roundOneDecimal(carbohydrates)
        );
        meal.setFat(roundOneDecimal(fat));

        return meal;
    }

    private int calculateTargetCalories(
            HealthProfile profile) {

        double baseBmr =
                (10 * profile.getWeight())
                + (6.25 * profile.getHeight())
                - (5 * profile.getAge());

        double bmr;

        if ("Male".equalsIgnoreCase(
                profile.getGender())) {

            bmr = baseBmr + 5;

        } else if ("Female".equalsIgnoreCase(
                profile.getGender())) {

            bmr = baseBmr - 161;

        } else {

            bmr = baseBmr - 78;
        }

        double tdee =
                bmr * getActivityMultiplier(
                        profile.getActivityLevel()
                );

        String goal = profile.getGoal();

        if ("Lose Weight".equalsIgnoreCase(goal)) {
            tdee = tdee - 500;
        }

        if ("Gain Muscle".equalsIgnoreCase(goal)) {
            tdee = tdee + 300;
        }

        /*
         * Prevents an excessively low calorie target.
         */
        return (int) Math.max(
                1200,
                Math.round(tdee)
        );
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

    private void addMealPlanSummary(
            List<MealPlan> mealPlans,
            HealthProfile profile,
            Model model) {

        int totalCalories =
                mealPlans.stream()
                        .mapToInt(
                                MealPlan::getCalories
                        )
                        .sum();

        double totalProtein =
                mealPlans.stream()
                        .mapToDouble(
                                MealPlan::getProtein
                        )
                        .sum();

        double totalCarbohydrates =
                mealPlans.stream()
                        .mapToDouble(
                                MealPlan::getCarbohydrates
                        )
                        .sum();

        double totalFat =
                mealPlans.stream()
                        .mapToDouble(
                                MealPlan::getFat
                        )
                        .sum();

        model.addAttribute(
                "targetCalories",
                totalCalories
        );

        model.addAttribute(
                "totalProtein",
                roundOneDecimal(totalProtein)
        );

        model.addAttribute(
                "totalCarbohydrates",
                roundOneDecimal(totalCarbohydrates)
        );

        model.addAttribute(
                "totalFat",
                roundOneDecimal(totalFat)
        );

        model.addAttribute(
                "healthGoal",
                profile.getGoal()
        );
    }

    private double roundOneDecimal(double value) {

        return Math.round(value * 10.0) / 10.0;
    }
}