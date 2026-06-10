package com.campus.interview.kafka;

import com.campus.events.InterviewCancelledEvent;
import com.campus.events.InterviewRescheduledEvent;
import com.campus.events.InterviewScheduledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterviewEventProducer {

    private static final String TOPIC = "interview-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishScheduled(InterviewScheduledEvent event) {
        send(event.getApplicationId().toString(), event, "InterviewScheduledEvent");
    }

    public void publishRescheduled(InterviewRescheduledEvent event) {
        send(event.getApplicationId().toString(), event, "InterviewRescheduledEvent");
    }

    public void publishCancelled(InterviewCancelledEvent event) {
        send(event.getApplicationId().toString(), event, "InterviewCancelledEvent");
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
