package com.example.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.model.SecurityReport;
import com.example.service.SecurityReportService;
import com.example.service.UserPerformanceService;
import com.example.service.UserService;

import dto.UserPerformanceDTO;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/reports")
public class SecurityReportController {

	private final SecurityReportService securityReportService;
	private final UserPerformanceService userPerformanceService;
	private final UserService userService;

	@Autowired
	public SecurityReportController(SecurityReportService securityReportService,
			UserPerformanceService userPerformanceService, UserService userService) {
		this.securityReportService = securityReportService;
		this.userPerformanceService = userPerformanceService;
		this.userService = userService;
	}

	// View all manually created security reports
	@GetMapping
	public String viewAllReports(Model model) {
		List<SecurityReport> reports = securityReportService.getAllReports();
		model.addAttribute("reports", reports);
		return "reports"; // reports.html
	}

	// Form to create a manual report (optional)
	@GetMapping("/create")
	public String createReportForm(Model model) {
		model.addAttribute("report", new SecurityReport());
		return "createReport"; // createReport.html
	}

	@PostMapping("/create")
	public String saveReport(@ModelAttribute("report") SecurityReport report) {
		securityReportService.saveReport(report);
		return "redirect:/reports";
	}

	@GetMapping("/delete/{id}")
	public String deleteReport(@PathVariable Long id) {
		securityReportService.deleteReport(id);
		return "redirect:/reports";
	}

	// Download Performance Overview Report
	@GetMapping("/performance")
	public ResponseEntity<InputStreamResource> downloadPerformanceReport() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		UserPerformanceDTO performance = userPerformanceService.getUserPerformance(username);

		String reportContent = generatePerformanceReportContent(username, performance);

		ByteArrayInputStream bis = new ByteArrayInputStream(reportContent.getBytes(StandardCharsets.UTF_8));

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=performance_report.html");

		return ResponseEntity.ok().headers(headers).contentType(MediaType.TEXT_HTML).body(new InputStreamResource(bis));
	}

	// Download Recent Quiz Results Report
	@GetMapping("/recent-quizzes")
	public ResponseEntity<InputStreamResource> downloadRecentQuizzesReport() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		List<String> recentQuizSummaries = userPerformanceService.getRecentQuizSummaries(username);

		String reportContent = generateRecentQuizzesReportContent(username, recentQuizSummaries);

		ByteArrayInputStream bis = new ByteArrayInputStream(reportContent.getBytes(StandardCharsets.UTF_8));

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=recent_quizzes_report.html");

		return ResponseEntity.ok().headers(headers).contentType(MediaType.TEXT_HTML).body(new InputStreamResource(bis));
	}

	// Internal Helper to build the Performance Report

	private String generatePerformanceReportContent(String username, UserPerformanceDTO performance) {
		return "<html><head><style>"
				+ "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #0a0a12; color: #e0e0e0; padding: 30px; }"
				+ "h1 { color: #00f0ff; text-align: center; }"
				+ "p { font-size: 1.2rem; text-align: center; margin: 10px 0; }"
				+ ".highlight { color: #00f0ff; font-weight: bold; }" + "</style></head><body>"
				+ "<h1>Performance Overview - " + username + "</h1>"
				+ "<p><span class='highlight'>Total Quizzes Taken:</span> " + performance.getTotalQuizzes() + "</p>"
				+ "<p><span class='highlight'>Average Score:</span> "
				+ String.format("%.2f", performance.getAverageScore()) + "</p>"
				+ "<p><span class='highlight'>Top Category:</span> " + performance.getTopCategory() + "</p>"
				+ "<p><span class='highlight'>Most Active Day:</span> " + performance.getMostActiveDay() + "</p>"
				+ "<br><p style='font-size:0.9rem; color:#888; text-align:center;'>Generated by Secure Portal</p>"
				+ "</body></html>";
	}

	// Internal Helper to build the Recent Quizzes Report
	// Update the method that generates the recent quizzes report
	private String generateRecentQuizzesReportContent(String username, List<String> quizSummaries) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><head><style>");
		sb.append(
				"body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #0a0a12; color: #e0e0e0; }");
		sb.append("h1 { color: #00f0ff; text-align: center; }");
		sb.append("table { width: 80%; margin: 0 auto; border-collapse: collapse; background-color: #1a1a2e; }");
		sb.append("th, td { border: 1px solid #00f0ff; padding: 10px; text-align: center; }");
		sb.append("th { background-color: #00f0ff; color: #0a0a12; }");
		sb.append("</style></head><body>");

		sb.append("<h1>Full Quiz History - ").append(username).append("</h1>");
		sb.append("<table>");
		sb.append("<tr><th>Date</th><th>Category (Role)</th><th>Score</th></tr>");

		for (String summary : quizSummaries) {

			String[] parts = summary.split(" - ");
			if (parts.length == 3) {
				String date = parts[0];
				String categoryRole = parts[1];
				String score = parts[2].replace("Score: ", "");
				sb.append("<tr><td>").append(date).append("</td><td>").append(categoryRole).append("</td><td>")
						.append(score).append("</td></tr>");
			}
		}

		sb.append("</table>");
		sb.append("</body></html>");
		return sb.toString();
	}

	@GetMapping("/download-performance")
	public void downloadPerformanceReport(HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment; filename=performance_report.txt");

		try (PrintWriter writer = response.getWriter()) {
			writer.println("=== User Performance Overview ===");
			writer.println("• Total Quizzes Taken: [Sample]");
			writer.println("• Average Score: [Sample]");
			writer.println("• Top Category: [Sample]");
			writer.println("• Most Used Role: [Sample]");
			writer.println();
			writer.println("Generated by Secure Portal.");
		}
	}

	@GetMapping("/download-recent")
	public void downloadRecentQuizzes(HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment; filename=recent_quizzes.txt");

		try (PrintWriter writer = response.getWriter()) {
			writer.println("=== Recent Quiz Attempts ===");
			writer.println("1. Web Security - Developer Role - Score: 8/10");
			writer.println("2. Risk Management - Analyst Role - Score: 7/10");
			writer.println("3. Authentication - General Role - Score: 9/10");
			writer.println();
			writer.println("Generated by Secure Portal.");
		}
	}
}
