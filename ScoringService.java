package com.example.service;

import com.example.model.Response;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScoringService {

    private static final double PASS_THRESHOLD = 80.0;

    // Beginner-friendly category-specific recommendations
    private static final Map<String, String> CATEGORY_RECOMMENDATIONS = new HashMap<>() {{
        put("Secure Coding", "Make sure you never hardcode passwords and always check user input to prevent attacks.");
        put("Access Control", "Only give users access to what they need. Use roles and permissions wisely.");
        put("Web Security", "Learn about the OWASP Top 10 – it's a great starting point for common website risks.");
        put("Authentication", "Use two-factor login and encourage strong, unique passwords for each account.");
        put("System Security", "Keep your systems updated, and disable any unnecessary features to reduce risks.");
        put("Incident Response", "Have a step-by-step plan for what to do if something goes wrong, and practice using it.");
        put("Penetration Testing", "Learn how ethical hackers test for security holes and practice using common tools safely.");
        put("Security Monitoring", "Set up alerts and logs so you can quickly catch anything unusual happening on your systems.");
        put("Risk Management", "Identify your biggest risks and make simple plans to avoid or reduce them.");
        put("Security Awareness", "Be cautious with emails and links. Regular training helps avoid phishing scams.");
        put("Database Security", "Protect sensitive data by encrypting it and only allowing trusted users to access it.");
        put("Secure Development", "Follow a checklist when writing code to ensure you’re building secure software.");
        put("Network Security", "Separate critical systems and monitor who is connecting to your network.");
        put("Social Engineering", "Watch out for tricks like fake emails or calls asking for sensitive info.");
        put("Security Governance", "Set clear security rules and review them regularly to make sure everyone follows them.");
    }};

    public int calculateScore(List<Response> responses) {
        return (int) responses.stream()
                .filter(response ->
                        response.getSelectedAnswer() != null &&
                        response.getCorrectAnswer() != null &&
                        response.getSelectedAnswer().equalsIgnoreCase(response.getCorrectAnswer()))
                .count();
    }

    public double calculatePercentage(int totalScore, int totalQuestions) {
        if (totalQuestions == 0) return 0.0;
        return ((double) totalScore / totalQuestions) * 100.0;
    }

    public boolean isPassed(double percentage) {
        return percentage >= PASS_THRESHOLD;
    }

    // Fallback if no category-specific logic is used
    public String generateRecommendation(double percentage) {
        if (percentage >= 90) {
            return "Fantastic! You're well on your way to mastering cybersecurity.";
        } else if (percentage >= 80) {
            return "Great work! You’ve got a strong foundation. Keep exploring advanced topics.";
        } else if (percentage >= 60) {
            return "You’re making progress! Review key areas and try again for a higher score.";
        } else {
            return "Don’t worry – focus on basics like password safety, secure coding, and common attacks.";
        }
    }

    public String suggestTrainingProgram(double percentage) {
        if (percentage < 60) {
            return "Cybersecurity Essentials Program";
        } else if (percentage < 80) {
            return "Intermediate Secure Coding & Threat Awareness";
        } else {
            return "Advanced Threat Detection & Risk Management";
        }
    }

    public String getCategoryRecommendation(String categoryName) {
    	System.out.println(">> [DEBUG] Fallback used. Category: " + categoryName);
        return CATEGORY_RECOMMENDATIONS.getOrDefault(
                categoryName,
                "Review general cybersecurity principles and explore more learning materials."
        );
    }
}
