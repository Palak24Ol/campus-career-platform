package com.campus.application.kafka;

import com.campus.events.ApplicationSubmittedEvent;
import com.campus.events.ApplicationWithdrawnEvent;
import com.campus.events.OfferReleasedEvent;
import com.campus.events.StudentShortlistedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationEventProducer {

    private static final String TOPIC = "application-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishSubmitted(ApplicationSubmittedEvent event) {
        send(event.getStudentId().toString(), event, "ApplicationSubmittedEvent");
    }

    public void publishWithdrawn(ApplicationWithdrawnEvent event) {
        send(event.getStudentId().toString(), event, "ApplicationWithdrawnEvent");
    }

    public void publishShortlisted(StudentShortlistedEvent event) {
        send(event.getStudentId().toString(), event, "StudentShortlistedEvent");
    }

    public void publishOfferReleased(OfferReleasedEvent event) {
        send(event.getStudentId().toString(), event, "OfferReleasedEvent");
    }

    private void send(String key, Object event, String eventType) {
        kafkaTemplate.send(TOPIC, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish {}: {}", eventType, ex.getMessage());
                    } else {
                        log.info("Published {}: partition={}",
                                eventType, result.getRecordMetadata().partition());
                    }
                });
    }
}