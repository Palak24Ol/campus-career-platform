package com.campus.analytics.kafka;

import com.campus.analytics.service.AnalyticsService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsEventConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(topics = "job-events", groupId = "analytics-group")
    public void onJobEvent(JsonNode event) {
        try {
            log.info("Analytics: job event received jobId={}", event.path("jobId").asText());
            analyticsService.onJobCreated();
        } catch (Exception e) {
            log.error("Error processing job event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "application-events", groupId = "analytics-group")
    public void onApplicationEvent(JsonNode event) {
        try {
            String type = inferType(event);
            log.info("Analytics: application event type={}", type);

            if ("ApplicationSubmitted".equals(type)) {
                String companyIdStr = event.path("companyId").asText(null);
                if (companyIdStr == null || companyIdStr.equals("null")) {
                    log.warn("ApplicationSubmitted event missing companyId, skipping");
                    return;
                }
                UUID companyId = UUID.fromString(companyIdStr);
                String companyName = event.path("companyName").asText("Unknown");
                analyticsService.onApplicationSubmitted(companyId, companyName);

            } else if ("OfferReleased".equals(type)) {
                String companyIdStr = event.path("companyId").asText(null);
                if (companyIdStr == null || companyIdStr.equals("null")) {
                    log.warn("OfferReleased event missing companyId, skipping");
                    return;
                }
                UUID companyId = UUID.fromString(companyIdStr);
                String companyName = event.path("companyName").asText("Unknown");
                Long ctc = event.hasNonNull("ctc") ? event.path("ctc").asLong() : null;
                analyticsService.onOfferReleased(companyId, companyName, ctc);
            }
        } catch (Exception e) {
            log.error("Error processing application event: {}", e.getMessage(), e);
        }
    }

    private String inferType(JsonNode event) {
        // joiningDate can be null/absent, so check companyId + ctc presence for offer detection
        if (event.has("ctc") && event.has("companyId") && event.has("studentId")
                && !event.has("recruiterNote")) return "OfferReleased";
        if (event.has("jobTitle") && event.has("studentName") && !event.has("ctc")) return "ApplicationSubmitted";
        if (event.has("recruiterNote") && !event.has("ctc")) return "StudentShortlisted";
        return "Unknown";
    }
}