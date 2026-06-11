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
                UUID companyId = UUID.fromString(event.path("companyId").asText());
                String companyName = event.path("companyName").asText("Unknown");
                analyticsService.onApplicationSubmitted(companyId, companyName);
            } else if ("OfferReleased".equals(type)) {
                UUID companyId = UUID.fromString(event.path("companyId").asText());
                String companyName = event.path("companyName").asText("Unknown");
                Long ctc = event.path("ctc").asLong(0);
                analyticsService.onOfferReleased(companyId, companyName, ctc);

            }
        } catch (Exception e) {
            log.error("Error processing application event: {}", e.getMessage(), e);
        }
    }

    private String inferType(JsonNode event) {
        if (event.has("jobTitle") && event.has("studentName")) return "ApplicationSubmitted";
        if (event.has("ctc") && event.has("joiningDate")) return "OfferReleased";
        if (event.has("recruiterNote") && !event.has("ctc")) return "StudentShortlisted";
        return "Unknown";
    }
}