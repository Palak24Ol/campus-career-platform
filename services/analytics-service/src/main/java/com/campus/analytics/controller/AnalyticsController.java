package com.campus.analytics.controller;

import com.campus.analytics.dto.response.CompanyStatsResponse;
import com.campus.analytics.dto.response.MonthlyTrendResponse;
import com.campus.analytics.dto.response.PlacementSummaryResponse;
import com.campus.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    public ResponseEntity<PlacementSummaryResponse> getSummary(
            @RequestHeader("X-User-Role") String role) {
        return ResponseEntity.ok(analyticsService.getSummary(role));
    }

    @GetMapping("/trends")
    public ResponseEntity<List<MonthlyTrendResponse>> getTrends(
            @RequestHeader("X-User-Role") String role) {
        return ResponseEntity.ok(analyticsService.getTrends(role));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<CompanyStatsResponse> getCompanyStats(
            @PathVariable UUID companyId,
            @RequestHeader("X-User-Role") String role) {
        return ResponseEntity.ok(analyticsService.getCompanyStats(companyId, role));
    }
}
