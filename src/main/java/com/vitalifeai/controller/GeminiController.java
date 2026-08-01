/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vitalifeai.controller;

/**
 *
 * @author wutyihlaingwin
 */


import com.vitalifeai.entity.HealthProfile;
import com.vitalifeai.service.GeminiService;
import com.vitalifeai.service.HealthProfileService;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class GeminiController {

    private final GeminiService geminiService;
    private final HealthProfileService healthProfileService;

    public GeminiController(
            GeminiService geminiService,
            HealthProfileService healthProfileService) {

        this.geminiService = geminiService;
        this.healthProfileService = healthProfileService;
    }

    @GetMapping("/ai-recommendation")
    public String showAiRecommendationPage(
            HttpSession session,
            Model model) {

        Long userId =
                (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        Optional<HealthProfile> optionalProfile =
                healthProfileService.getProfileByUserId(userId);

        model.addAttribute(
                "userName",
                session.getAttribute("loggedInUserName")
        );

        if (optionalProfile.isEmpty()) {

            model.addAttribute("profileExists", false);

            return "ai-recommendation";
        }

        model.addAttribute("profileExists", true);
        model.addAttribute(
                "healthProfile",
                optionalProfile.get()
        );

        return "ai-recommendation";
    }

    @PostMapping("/ai-recommendation/generate")
    public String generateAiRecommendation(
            HttpSession session,
            Model model) {

        Long userId =
                (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        Optional<HealthProfile> optionalProfile =
                healthProfileService.getProfileByUserId(userId);

        model.addAttribute(
                "userName",
                session.getAttribute("loggedInUserName")
        );

        if (optionalProfile.isEmpty()) {

            model.addAttribute("profileExists", false);

            return "ai-recommendation";
        }

        HealthProfile profile = optionalProfile.get();

        String prompt = buildHealthPrompt(profile);

        String aiRecommendation =
                geminiService.generateRecommendation(prompt);

        model.addAttribute("profileExists", true);
        model.addAttribute("healthProfile", profile);
        model.addAttribute(
                "aiRecommendation",
                aiRecommendation
        );

        model.addAttribute(
                "recommendationGenerated",
                true
        );

        return "ai-recommendation";
    }

    private String buildHealthPrompt(
            HealthProfile profile) {

        return """
               You are a wellness recommendation assistant
               for the VitaLife AI application.

               Create a personalised but general wellness plan
               using the following user information:

               Age: %d
               Gender: %s
               Height: %.1f cm
               Weight: %.1f kg
               Activity level: %s
               Health goal: %s

               Structure the response using these headings:

               1. Health Summary
               2. Nutrition Recommendations
               3. Exercise Recommendations
               4. Supplement Considerations
               5. Lifestyle Recommendations
               6. Weekly Action Plan

               Requirements:
               - Use clear and supportive language.
               - Keep the response practical and concise.
               - Do not diagnose medical conditions.
               - Do not claim that supplements treat diseases.
               - Recommend consulting a qualified healthcare
                 professional before starting supplements or
                 making major health changes.
               - Base the advice on general wellness guidance.
               """
                .formatted(
                        profile.getAge(),
                        profile.getGender(),
                        profile.getHeight(),
                        profile.getWeight(),
                        profile.getActivityLevel(),
                        profile.getGoal()
                );
    }
}