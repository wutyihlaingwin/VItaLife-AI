/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author wutyihlaingwin
 */
package com.vitalifeai.controller;

import com.vitalifeai.entity.User;
import com.vitalifeai.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute(
                    "errorMessage",
                    "Passwords do not match."
            );

            model.addAttribute("fullName", fullName);
            model.addAttribute("email", email);

            return "register";
        }

        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPassword(password);

        String result = userService.registerUser(user);

        if ("Success".equals(result)) {
            return "redirect:/login?registered=true";
        }

        model.addAttribute(
                "errorMessage",
                "An account with this email already exists."
        );

        model.addAttribute("fullName", fullName);
        model.addAttribute("email", email);

        return "register";
    }

    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(required = false) Boolean registered,
            @RequestParam(required = false) Boolean logout,
            Model model) {

        if (Boolean.TRUE.equals(registered)) {
            model.addAttribute(
                    "successMessage",
                    "Registration successful. Please log in."
            );
        }

        if (Boolean.TRUE.equals(logout)) {
            model.addAttribute(
                    "successMessage",
                    "You have logged out successfully."
            );
        }

        return "login";
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        User user = userService.login(
                email.trim().toLowerCase(),
                password
        );

        if (user == null) {
            model.addAttribute(
                    "errorMessage",
                    "Incorrect email address or password."
            );

            model.addAttribute("email", email);

            return "login";
        }

        session.setAttribute(
                "loggedInUserId",
                user.getId()
        );

        session.setAttribute(
                "loggedInUserName",
                user.getFullName()
        );

        session.setAttribute(
                "loggedInUserEmail",
                user.getEmail()
        );

        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login?logout=true";
    }
}