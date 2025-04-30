package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.SecurityReport;
import com.example.repository.SecurityReportRepository;

@Service
public class SecurityReportService {

	private final SecurityReportRepository securityReportRepository;

	@Autowired
	public SecurityReportService(SecurityReportRepository securityReportRepository) {
		this.securityReportRepository = securityReportRepository;
	}

	public List<SecurityReport> getAllReports() {
		return securityReportRepository.findAll();
	}

	public Optional<SecurityReport> getReportById(Long id) {
		return securityReportRepository.findById(id);
	}

	public SecurityReport saveReport(SecurityReport report) {
		return securityReportRepository.save(report);
	}

	public void deleteReport(Long id) {
		securityReportRepository.deleteById(id);
	}
}
