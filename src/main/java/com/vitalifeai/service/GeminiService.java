/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author wutyihlaingwin
 */
package com.vitalifeai.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final String apiKey;

    public GeminiService(
            @Value("${gemini.api.key:}") String apiKey) {

        this.apiKey = apiKey;
    }

    public String generateRecommendation(String prompt) {

        if (apiKey == null || apiKey.isBlank()) {
            return "Gemini API key is not configured. "
                    + "Please set the GEMINI_API_KEY environment variable.";
        }

        try {

            Client client = Client.builder()
                    .apiKey(apiKey)
                    .build();

            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-3.6-flash",
                            prompt,
                            null
                    );

            String generatedText = response.text();

            if (generatedText == null
                    || generatedText.isBlank()) {

                return "Gemini did not return a recommendation.";
            }

            return generatedText;

        } catch (Exception exception) {

            System.err.println(
                    "Gemini API error: "
                    + exception.getMessage()
            );

            return "The AI recommendation service is temporarily unavailable. "
                    + "Please try again later.";
        }
    }
}