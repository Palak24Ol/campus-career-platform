package com.campus.notification.kafka;

import com.campus.notification.service.EmailService;
import com.campus.notification.service.NotificationService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;
    private final EmailService emailService;

    @KafkaListener(topics = "application-events", groupId = "notification-group")
    public void onApplicationEvent(JsonNode event) {
        try {
            String type = event.path("@type").asText(null);
            if (type == null) {
                type = inferTypeFromFields(event);
            }
            log.info("Received application event, type={}", type);

            if (type != null && type.contains("ApplicationSubmitted")) {
                UUID studentId = UUID.fromString(event.path("studentId").asText());
                String jobTitle = event.path("jobTitle").asText("a position");
                String companyName = event.path("companyName").asText("the company");
                String studentName = event.path("studentName").asText("Student");

                notificationService.create(
                        studentId,
                        "APPLICATION_SUBMITTED",
                        "Application Submitted",
                        "Your application for " + jobTitle + " at " + companyName + " has been submitted successfully."
                );

                emailService.sendEmail(
                        null,
                        "Application Submitted — " + jobTitle,
                        "Hi " + studentName + ",\n\nYour application for " + jobTitle +
                                " at " + companyName + " has been submitted successfully.\n\nBest regards,\nCampus Career Platform"
                );

            } else if (type != null && type.contains("StudentShortlisted")) {
                UUID studentId = UUID.fromString(event.path("studentId").asText());
                String companyName = event.path("companyName").asText("the company");
                String recruiterNote = event.path("recruiterNote").asText(null);

                notificationService.create(
                        studentId,
                        "SHORTLISTED",
                        "You have been shortlisted!",
                        "Congratulations! You have been shortlisted for a position at " + companyName + "." +
                                (recruiterNote != null ? " Note: " + recruiterNote : "")
                );

            } else if (type != null && type.contains("OfferReleased")) {
                UUID studentId = UUID.fromString(event.path("studentId").asText());
                String companyName = event.path("companyName").asText("the company");
                String ctc = event.path("ctc").asText(null);

                notificationService.create(
                        studentId,
                        "OFFER_RELEASED",
                        "Offer Released!",
                        "Congratulations! " + companyName + " has released an offer for you." +
                                (ctc != null ? " CTC: " + ctc + "K per annum." : "")
                );

            } else if (type != null && type.contains("ApplicationWithdrawn")) {
                log.info("Application withdrawn: applicationId={}", event.path("applicationId").asText());
            } else {
                log.warn("Unknown application event type={}, fields={}", type, event.fieldNames());
            }
        } catch (Exception e) {
            log.error("Error processing application event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "interview-events", groupId = "notification-group")
    public void onInterviewEvent(JsonNode event) {
        try {
            String type = event.path("@type").asText(null);
            if (type == null) {
                type = inferTypeFromFields(event);
            }
            log.info("Received interview event, type={}", type);

            if (type != null && type.contains("InterviewScheduled")) {
                UUID studentId = UUID.fromString(event.path("studentId").asText());
                String companyName = event.path("companyName").asText("the company");
                String mode = event.path("mode").asText("ONLINE");
                String meetLink = event.path("meetLink").asText(null);
                String venue = event.path("venue").asText(null);
                int round = event.path("round").asInt(1);
                String date = event.path("interviewDate").asText("TBD");

                String details = "ONLINE".equals(mode)
                        ? "Mode: Online | Link: " + meetLink
                        : "Mode: Offline | Venue: " + venue;

                notificationService.create(
                        studentId,
                        "INTERVIEW_SCHEDULED",
                        "Interview Scheduled",
                        "Your Round " + round + " interview with " + companyName +
                                " has been scheduled for " + date + ". " + details
                );

            } else if (type != null && type.contains("InterviewRescheduled")) {
                UUID studentId = UUID.fromString(event.path("studentId").asText());
                String newDate = event.path("newDate").asText("TBD");
                String reason = event.path("reason").asText("");

                notificationService.create(
                        studentId,
                        "INTERVIEW_RESCHEDULED",
                        "Interview Rescheduled",
                        "Your interview has been rescheduled to " + newDate + ". Reason: " + reason
                );

            } else if (type != null && type.contains("InterviewCancelled")) {
                UUID studentId = UUID.fromString(event.path("studentId").asText());
                String reason = event.path("reason").asText("");

                notificationService.create(
                        studentId,
                        "INTERVIEW_CANCELLED",
                        "Interview Cancelled",
                        "Your interview has been cancelled. Reason: " + reason
                );
            }
        } catch (Exception e) {
            log.error("Error processing interview event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "job-events", groupId = "notification-group")
    public void onJobEvent(JsonNode event) {
        log.info("Received job event: {}", event.path("jobId").asText());
    }

    private String inferTypeFromFields(JsonNode event) {
        if (event.has("jobTitle") && event.has("studentName")) return "ApplicationSubmitted";
        if (event.has("recruiterNote") && !event.has("ctc")) return "StudentShortlisted";
        if (event.has("ctc") && event.has("joiningDate")) return "OfferReleased";
        if (event.has("interviewDate") && event.has("mode")) return "InterviewScheduled";
        if (event.has("newDate") && event.has("oldDate")) return "InterviewRescheduled";
        if (event.has("reason") && !event.has("newDate")) return "InterviewCancelled";
        return null;
    }
}