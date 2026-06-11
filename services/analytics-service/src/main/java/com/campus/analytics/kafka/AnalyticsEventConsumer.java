package com.campus.analytics.kafka;

import com.campus.events.ApplicationSubmittedEvent;
import com.campus.events.JobCreatedEvent;
import com.campus.events.OfferReleasedEvent;
import com.campus.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsEventConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(topics = "job-events", groupId = "analytics-group")
    public void onJobCreated(JobCreatedEvent event) {
        log.info("Analytics: JobCreatedEvent jobId={}", event.getJobId());
        analyticsService.onJobCreated();
    }

    @KafkaListener(topics = "application-events", groupId = "analytics-group")
    public void onApplicationEvent(Object rawEvent) {
        if (rawEvent instanceof ApplicationSubmittedEvent event) {
            log.info("Analytics: ApplicationSubmittedEvent studentId={}", event.getStudentId());
            analyticsService.onApplicationSubmitted(
                    event.getStudentId(),
                    event.getCompanyName()
            );
        } else if (rawEvent instanceof OfferReleasedEvent event) {
            log.info("Analytics: OfferReleasedEvent studentId={}", event.getStudentId());
            analyticsService.onOfferReleased(
                    event.getStudentId(),
                    event.getCompanyName(),
                    event.getCtc()
            );
        }
    }
}
