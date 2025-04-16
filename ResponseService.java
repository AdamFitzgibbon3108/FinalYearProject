package com.example.service;

import com.example.model.Question;
import com.example.model.QuizResult;
import com.example.model.Response;
import com.example.model.User;
import com.example.repository.QuestionRepository;
import com.example.repository.QuizResultRepository;
import com.example.repository.ResponseRepository;
import com.example.repository.UserRepository;
import dto.SubmittedAnswerDTO;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResponseService {

    private static final Logger log = LoggerFactory.getLogger(ResponseService.class);

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public QuizResult saveQuizResultAndResponses(User user, List<SubmittedAnswerDTO> responses, List<Question> questions) {
        int totalQuestions = responses.size();
        int totalScore = 0;

        String category = responses.get(0).getCategory();
        String role = responses.get(0).getRole();
        String recommendation = getCategoryRecommendation(category);

        QuizResult quizResult = new QuizResult();
        quizResult.setUser(user);
        quizResult.setCategory(category);
        quizResult.setRole(role);
        quizResult.setCompletedAt(LocalDateTime.now());
        quizResult.setTotalQuestions(totalQuestions);
        quizResult.setRecommendations(recommendation);

        // Save early to assign ID for foreign key references in responses
        quizResult = quizResultRepository.saveAndFlush(quizResult);

        for (int i = 0; i < responses.size(); i++) {
            SubmittedAnswerDTO dto = responses.get(i);
            Question question = questions.get(i);

            String normalizedSelected = normalize(dto.getSelectedAnswer());
            String normalizedCorrect = normalize(dto.getCorrectAnswer());
            boolean isCorrect = normalizedSelected.equalsIgnoreCase(normalizedCorrect);

            log.debug("🔍 Q{}: {}", i + 1, question.getQuestionText());
            log.debug("   - Raw Selected: '{}'", dto.getSelectedAnswer());
            log.debug("   - Raw Correct : '{}'", dto.getCorrectAnswer());
            log.debug("   - Normalized Selected: '{}'", normalizedSelected);
            log.debug("   - Normalized Correct : '{}'", normalizedCorrect);
            log.debug("   - Is Correct? {}", isCorrect);

            if (isCorrect) {
                totalScore++;
            }

            Response response = new Response();
            response.setUser(user);
            response.setQuizResult(quizResult); // Ensure relation is set
            response.setQuestion(question);
            response.setSelectedAnswer(normalizedSelected);
            response.setCorrectAnswer(normalizedCorrect);
            response.setCorrect(isCorrect);
            response.setScore(isCorrect ? 1 : 0);
            response.setTimestamp(dto.getTimestamp());
            response.setRole(role);
            response.setCategory(category);

            responseRepository.save(response);
        }

        quizResult.setTotalScore(totalScore);
        quizResult.setPassed(totalScore >= 12);
        quizResultRepository.save(quizResult);

        log.info("✅ Quiz saved for user '{}' | Category: '{}' | Score: {}/{}",
                user.getUsername(), category, totalScore, totalQuestions);

        return quizResult;
    }

    private String normalize(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFKC);

        normalized = normalized.replaceAll("&nbsp;", " ");
        normalized = normalized.replace("\u00A0", " ");
        normalized = normalized.replaceAll("[\\p{C}]", "");
        normalized = normalized.replaceAll("\\p{Pd}", "-");
        normalized = normalized.replaceAll("[\\u2018\\u2019\\u201A\\u201B]", "'");
        normalized = normalized.replaceAll("[\\u201C\\u201D\\u201E\\u201F]", "\"");

        return normalized.trim().replaceAll("\\s+", " ");
    }

    private String bytes(String s) {
        if (s == null) return "null";
        byte[] b = s.getBytes();
        StringBuilder sb = new StringBuilder();
        for (byte value : b) {
            sb.append(String.format("%02X ", value));
        }
        return sb.toString().trim();
    }

    private String getCategoryRecommendation(String category) {
        return switch (category) {
            case "Access Control" -> "Review access provisioning, privilege separation, and least privilege principles.";
            case "Secure Development" -> "Continue enhancing your secure coding skills with OWASP resources.";
            case "Web Security" -> "Review OWASP Top 10 and practice mitigating XSS and CSRF threats.";
            case "Database Security" -> "Focus on encryption, access control, and SQL injection prevention.";
            case "Security Governance" -> "Understand frameworks like ISO 27001 and NIST RMF.";
            case "Authentication" -> "Strengthen knowledge in MFA, SSO, and secure session handling.";
            case "Security Awareness" -> "Revisit phishing defense strategies and social engineering risks.";
            case "Network Security" -> "Brush up on firewall configuration, IDS/IPS systems, and VPN best practices.";
            case "Incident Response" -> "Learn from NIST’s incident response lifecycle and improve response planning.";
            case "Risk Management" -> "Improve risk analysis and alignment with business objectives.";
            case "Penetration Testing" -> "Explore tools like Burp Suite and methodologies like PTES.";
            case "Security Monitoring" -> "Study SIEM systems and anomaly detection techniques.";
            case "System Security" -> "Practice securing endpoints, patching, and OS hardening.";
            default -> "Keep building foundational knowledge across all security domains.";
        };
    }

    public List<Response> getResponsesByUser(Long userId) {
        return responseRepository.findByUserId(userId);
    }

    public List<Response> getResponsesByQuestion(Long questionId) {
        return responseRepository.findByQuestionId(questionId);
    }

    public List<Response> getResponsesByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return responseRepository.findByUserId(user.getId());
    }

    public List<Response> getResponsesByQuizResultId(Long quizResultId) {
        return responseRepository.findByQuizResultId(quizResultId);
    }
}

