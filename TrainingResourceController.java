package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.model.TrainingResource;
import com.example.service.TrainingResourceService;
import com.example.service.UserPerformanceService;

@Controller
@RequestMapping("/training")
public class TrainingResourceController {

	private final TrainingResourceService trainingResourceService;
	private final UserPerformanceService userPerformanceService;

	@Autowired
	public TrainingResourceController(TrainingResourceService trainingResourceService,
			UserPerformanceService userPerformanceService) {
		this.trainingResourceService = trainingResourceService;
		this.userPerformanceService = userPerformanceService;
	}

	// Load training page with resources for a given category
	@GetMapping("/{category}")
	public String getTrainingByCategory(@PathVariable("category") String category, Model model) {
		List<TrainingResource> resources = trainingResourceService.getResourcesByCategory(category);
		model.addAttribute("category", category);
		model.addAttribute("resources", resources);
		return "training-resources";
	}

	// Show all available resources
	@GetMapping("/all")
	public String getAllTrainingResources(Model model) {
		List<TrainingResource> allResources = trainingResourceService.getAllResources();
		model.addAttribute("category", "All Categories");
		model.addAttribute("resources", allResources);
		return "training-resources";
	}

	// Show personalized training recommendations based on performance
	@GetMapping("/personalized")
	public String getPersonalizedTraining(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		List<String> weakestCategories = userPerformanceService.getWeakestCategories(username, 3);
		List<TrainingResource> recommendedResources = trainingResourceService
				.getResourcesForWeakCategories(weakestCategories);

		model.addAttribute("category", "Recommended For You");
		model.addAttribute("resources", recommendedResources);
		model.addAttribute("weakestCategories", weakestCategories);
		return "training-resources";
	}
}
