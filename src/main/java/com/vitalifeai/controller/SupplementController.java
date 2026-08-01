package com.vitalifeai.controller;

import com.vitalifeai.entity.HealthProfile;
import com.vitalifeai.entity.SupplementRecommendation;
import com.vitalifeai.entity.User;
import com.vitalifeai.service.HealthProfileService;
import com.vitalifeai.service.SupplementService;
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
public class SupplementController {

    private final SupplementService supplementService;
    private final HealthProfileService healthProfileService;
    private final UserService userService;

    public SupplementController(
            SupplementService supplementService,
            HealthProfileService healthProfileService,
            UserService userService) {

        this.supplementService = supplementService;
        this.healthProfileService = healthProfileService;
        this.userService = userService;
    }

    @GetMapping("/supplements")
    public String showSupplements(HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        Optional<HealthProfile> optionalProfile =
                healthProfileService.getProfileByUserId(userId);

        if (optionalProfile.isEmpty()) {
            model.addAttribute("profileExists", false);
            model.addAttribute("supplementExists", false);
            return "supplements";
        }

        HealthProfile profile = optionalProfile.get();

        List<SupplementRecommendation> supplements =
                supplementService.getSupplementRecommendationsByUserId(userId);

        model.addAttribute("profileExists", true);
        model.addAttribute("healthProfile", profile);
        model.addAttribute("supplements", supplements);

        if (supplements.isEmpty()) {

            model.addAttribute("supplementExists", false);

        } else {

            model.addAttribute("supplementExists", true);
            model.addAttribute("healthGoal", profile.getGoal());
        }

        return "supplements";
    }

    @PostMapping("/supplements/generate")
    public String generateSupplements(HttpSession session) {

        Long userId = (Long) session.getAttribute("loggedInUserId");

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

        List<SupplementRecommendation> recommendations =
                createRecommendations(user, profile.getGoal());

        supplementService.replaceSupplements(userId, recommendations);

        return "redirect:/supplements?generated=true";
    }

    private List<SupplementRecommendation> createRecommendations(
            User user,
            String goal) {

        List<SupplementRecommendation> list = new ArrayList<>();

        if ("Lose Weight".equalsIgnoreCase(goal)) {

            list.add(create(user, "Green Tea Extract",
                    "Supports fat metabolism",
                    "500 mg daily",
                    "Morning",
                    "Consult a healthcare professional before use."));

            list.add(create(user, "Omega-3",
                    "Supports heart health",
                    "1000 mg daily",
                    "With lunch",
                    "Take with food."));

            list.add(create(user, "Multivitamin",
                    "Daily nutritional support",
                    "1 tablet",
                    "Breakfast",
                    "Do not exceed the recommended dose."));

        } else if ("Gain Muscle".equalsIgnoreCase(goal)) {

            list.add(create(user, "Whey Protein",
                    "Supports muscle growth",
                    "30 g",
                    "After workout",
                    "Mix with water or milk."));

            list.add(create(user, "Creatine Monohydrate",
                    "Improves strength and power",
                    "5 g daily",
                    "After workout",
                    "Drink plenty of water."));

            list.add(create(user, "Vitamin D",
                    "Supports muscle and bone health",
                    "1000 IU daily",
                    "Breakfast",
                    "Follow medical advice if deficient."));

        } else {

            list.add(create(user, "Multivitamin",
                    "General health support",
                    "1 tablet",
                    "Breakfast",
                    "Use as directed."));

            list.add(create(user, "Omega-3",
                    "Supports cardiovascular health",
                    "1000 mg daily",
                    "Lunch",
                    "Take with food."));

            list.add(create(user, "Electrolytes",
                    "Supports hydration",
                    "1 serving",
                    "After exercise",
                    "Useful after intense activity."));
        }

        return list;
    }

    private SupplementRecommendation create(
            User user,
            String name,
            String purpose,
            String dosage,
            String time,
            String notes) {

        SupplementRecommendation s = new SupplementRecommendation();

        s.setUser(user);
        s.setSupplementName(name);
        s.setPurpose(purpose);
        s.setDosage(dosage);
        s.setBestTimeToTake(time);
        s.setNotes(notes);

        return s;
    }
}