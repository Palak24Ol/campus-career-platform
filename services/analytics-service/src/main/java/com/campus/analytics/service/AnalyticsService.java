package com.campus.analytics.service;

import com.campus.analytics.dto.response.CompanyStatsResponse;
import com.campus.analytics.dto.response.MonthlyTrendResponse;
import com.campus.analytics.dto.response.PlacementSummaryResponse;
import com.campus.analytics.entity.CompanyStats;
import com.campus.analytics.entity.MonthlyTrend;
import com.campus.analytics.entity.PlacementStats;
import com.campus.analytics.exception.AccessDeniedException;
import com.campus.analytics.repository.CompanyStatsRepository;
import com.campus.analytics.repository.MonthlyTrendRepository;
import com.campus.analytics.repository.PlacementStatsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final PlacementStatsRepository placementStatsRepository;
    private final MonthlyTrendRepository monthlyTrendRepository;
    private final CompanyStatsRepository companyStatsRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String SUMMARY_CACHE_KEY = "analytics:summary";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    public PlacementSummaryResponse getSummary(String role) {
        assertAdminOrRecruiter(role);

        try {
            String cached = redisTemplate.opsForValue().get(SUMMARY_CACHE_KEY);
            if (cached != null) {
                return objectMapper.readValue(cached, PlacementSummaryResponse.class);
            }
        } catch (Exception e) {
            log.warn("Cache read failed for summary");
        }

        PlacementStats stats = placementStatsRepository.findFirstByOrderByUpdatedAtDesc()
                .orElseGet(() -> PlacementStats.builder().build());

        PlacementSummaryResponse response = PlacementSummaryResponse.builder()
                .totalApplications(stats.getTotalApplications())
                .totalOffers(stats.getTotalOffers())
                .totalActiveJobs(stats.getTotalActiveJobs())
                .averagePackage(stats.getAveragePackage())
                .highestPackage(stats.getHighestPackage())
                .updatedAt(stats.getUpdatedAt())
                .build();

        try {
            redisTemplate.opsForValue().set(SUMMARY_CACHE_KEY,
                    objectMapper.writeValueAsString(response), CACHE_TTL);
        } catch (Exception e) {
            log.warn("Cache write failed for summary");
        }

        return response;
    }

    public List<MonthlyTrendResponse> getTrends(String role) {
        assertAdminOrRecruiter(role);
        return monthlyTrendRepository.findAllByOrderByYearAscMonthAsc()
                .stream().map(this::toTrendResponse).toList();
    }

    public CompanyStatsResponse getCompanyStats(UUID companyId, String role) {
        assertAdminOrRecruiter(role);
        CompanyStats stats = companyStatsRepository.findByCompanyId(companyId)
                .orElseGet(() -> CompanyStats.builder()
                        .companyId(companyId)
                        .build());
        return toCompanyResponse(stats);
    }

    @Transactional
    public void onApplicationSubmitted(UUID companyId, String companyName) {
        PlacementStats stats = getOrCreateStats();
        stats.setTotalApplications(stats.getTotalApplications() + 1);
        placementStatsRepository.save(stats);

        updateMonthlyApplications();
        updateCompanyApplications(companyId, companyName);
        evictSummaryCache();
    }

    @Transactional
    public void onJobCreated() {
        PlacementStats stats = getOrCreateStats();
        stats.setTotalActiveJobs(stats.getTotalActiveJobs() + 1);
        placementStatsRepository.save(stats);
        evictSummaryCache();
    }

    @Transactional
    public void onOfferReleased(UUID companyId, String companyName, Long ctc) {
        PlacementStats stats = getOrCreateStats();
        stats.setTotalOffers(stats.getTotalOffers() + 1);

        if (ctc != null) {
            BigDecimal ctcLpa = BigDecimal.valueOf(ctc).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (ctcLpa.compareTo(stats.getHighestPackage()) > 0) {
                stats.setHighestPackage(ctcLpa);
            }

            long offerCount = stats.getTotalOffers();
            BigDecimal currentTotal = stats.getAveragePackage()
                    .multiply(BigDecimal.valueOf(offerCount - 1));
            stats.setAveragePackage(currentTotal.add(ctcLpa)
                    .divide(BigDecimal.valueOf(offerCount), 2, RoundingMode.HALF_UP));
        }

        placementStatsRepository.save(stats);
        updateMonthlyOffers(ctc);
        updateCompanyOffers(companyId, companyName, ctc);
        evictSummaryCache();
    }

    private PlacementStats getOrCreateStats() {
        return placementStatsRepository.findFirstByOrderByUpdatedAtDesc()
                .orElseGet(() -> placementStatsRepository.save(
                        PlacementStats.builder().build()));
    }

    private void updateMonthlyApplications() {
        LocalDateTime now = LocalDateTime.now();
        MonthlyTrend trend = monthlyTrendRepository
                .findByYearAndMonth(now.getYear(), now.getMonthValue())
                .orElseGet(() -> MonthlyTrend.builder()
                        .year(now.getYear())
                        .month(now.getMonthValue())
                        .build());
        trend.setApplications(trend.getApplications() + 1);
        monthlyTrendRepository.save(trend);
    }

    private void updateMonthlyOffers(Long ctc) {
        LocalDateTime now = LocalDateTime.now();
        MonthlyTrend trend = monthlyTrendRepository
                .findByYearAndMonth(now.getYear(), now.getMonthValue())
                .orElseGet(() -> MonthlyTrend.builder()
                        .year(now.getYear())
                        .month(now.getMonthValue())
                        .build());
        trend.setOffers(trend.getOffers() + 1);
        if (ctc != null) {
            BigDecimal ctcLpa = BigDecimal.valueOf(ctc).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            long offerCount = trend.getOffers();
            BigDecimal currentTotal = trend.getAverageCtc()
                    .multiply(BigDecimal.valueOf(offerCount - 1));
            trend.setAverageCtc(currentTotal.add(ctcLpa)
                    .divide(BigDecimal.valueOf(offerCount), 2, RoundingMode.HALF_UP));
        }
        monthlyTrendRepository.save(trend);
    }

    private void updateCompanyApplications(UUID companyId, String companyName) {
        CompanyStats stats = companyStatsRepository.findByCompanyId(companyId)
                .orElseGet(() -> CompanyStats.builder()
                        .companyId(companyId)
                        .companyName(companyName)
                        .build());
        stats.setTotalApplications(stats.getTotalApplications() + 1);
        companyStatsRepository.save(stats);
    }

    private void updateCompanyOffers(UUID companyId, String companyName, Long ctc) {
        CompanyStats stats = companyStatsRepository.findByCompanyId(companyId)
                .orElseGet(() -> CompanyStats.builder()
                        .companyId(companyId)
                        .companyName(companyName)
                        .build());
        stats.setTotalOffers(stats.getTotalOffers() + 1);
        if (ctc != null) {
            BigDecimal ctcLpa = BigDecimal.valueOf(ctc).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            long offerCount = stats.getTotalOffers();
            BigDecimal currentTotal = stats.getAverageCtc()
                    .multiply(BigDecimal.valueOf(offerCount - 1));
            stats.setAverageCtc(currentTotal.add(ctcLpa)
                    .divide(BigDecimal.valueOf(offerCount), 2, RoundingMode.HALF_UP));
        }
        companyStatsRepository.save(stats);
    }

    private void evictSummaryCache() {
        redisTemplate.delete(SUMMARY_CACHE_KEY);
    }

    private void assertAdminOrRecruiter(String role) {
        if (!"ADMIN".equals(role) && !"RECRUITER".equals(role)) {
            throw new AccessDeniedException();
        }
    }

    private MonthlyTrendResponse toTrendResponse(MonthlyTrend t) {
        return MonthlyTrendResponse.builder()
                .year(t.getYear())
                .month(t.getMonth())
                .applications(t.getApplications())
                .offers(t.getOffers())
                .averageCtc(t.getAverageCtc())
                .build();
    }

    private CompanyStatsResponse toCompanyResponse(CompanyStats s) {
        return CompanyStatsResponse.builder()
                .companyId(s.getCompanyId())
                .companyName(s.getCompanyName())
                .totalApplications(s.getTotalApplications())
                .totalOffers(s.getTotalOffers())
                .averageCtc(s.getAverageCtc())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
