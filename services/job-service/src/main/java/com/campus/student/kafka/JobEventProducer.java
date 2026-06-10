package com.campus.job.kafka;

import com.campus.events.JobCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobEventProducer {

    private static final String TOPIC = "job-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishJobCreated(JobCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event.getCompanyId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish JobCreatedEvent for jobId={}: {}",
                                event.getJobId(), ex.getMessage());
                    } else {
                        log.info("Published JobCreatedEvent: jobId={}, partition={}",
                                event.getJobId(),
                                result.getRecordMetadata().partition());
                    }
                });
    }
}
