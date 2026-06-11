package com.campus.notification.kafka;

import com.campus.events.*;
import com.campus.notification.service.EmailService;
import com.campus.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;
    private final EmailService emailService;

    @KafkaListener(topics = "job-events", groupId = "notification-group")
    public void onJobCreated(JobCreatedEvent event) {
        log.info("Received JobCreatedEvent: jobId={}", event.getJobId());
    }

    @KafkaListener(topics = "application-events", groupId = "notification-group")
    public void onApplicationEvent(Object rawEvent) {
        if (rawEvent instanceof ApplicationSubmittedEvent event) {
            handleApplicationSubmitted(event);
        } else if (rawEvent instanceof StudentShortlistedEvent event) {
            handleStudentShortlisted(event);
        } else if (rawEvent instanceof OfferReleasedEvent event) {
            handleOfferReleased(event);
        } else if (rawEvent instanceof ApplicationWithdrawnEvent event) {
            log.info("Application withdrawn: applicationId={}", event.getApplicationId());
        }
    }

    @KafkaListener(topics = "interview-events", groupId = "notification-group")
    public void onInterviewEvent(Object rawEvent) {
        if (rawEvent instanceof InterviewScheduledEvent event) {
            handleInterviewScheduled(event);
        } else if (rawEvent instanceof InterviewRescheduledEvent event) {
            handleInterviewRescheduled(event);
        } else if (rawEvent instanceof InterviewCancelledEvent event) {
            handleInterviewCancelled(event);
        }
    }

    private void handleApplicationSubmitted(ApplicationSubmittedEvent event) {
        log.info("Application submitted: studentId={}, jobId={}",
                event.getStudentId(), event.getJobId());

        notificationService.create(
                event.getStudentId(),
                "APPLICATION_SUBMITTED",
                "Application Submitted",
                "Your application for " + event.getJobTitle()
                        + " at " + event.getCompanyName() + " has been submitted successfully."
        );

        emailService.sendEmail(
                null,
                "Application Submitted — " + event.getJobTitle(),
                "Hi " + event.getStudentName() + ",\n\n"
                        + "Your application for " + event.getJobTitle()
                        + " at " + event.getCompanyName()
                        + " has been submitted successfully.\n\n"
                        + "We will notify you of any updates.\n\n"
                        + "Best regards,\nCampus Career Platform"
        );
    }

    private void handleStudentShortlisted(StudentShortlistedEvent event) {
        log.info("Student shortlisted: studentId={}, jobId={}",
                event.getStudentId(), event.getJobId());

        notificationService.create(
                event.getStudentId(),
                "SHORTLISTED",
                "You have been shortlisted!",
                "Congratulations! You have been shortlisted for a position at "
                        + event.getCompanyName() + "."
                        + (event.getRecruiterNote() != null
                                ? " Note from recruiter: " + event.getRecruiterNote()
                                : "")
        );
    }

    private void handleOfferReleased(OfferReleasedEvent event) {
        log.info("Offer released: studentId={}, jobId={}",
                event.getStudentId(), event.getJobId());

        notificationService.create(
                event.getStudentId(),
                "OFFER_RELEASED",
                "Offer Released!",
                "Congratulations! " + event.getCompanyName()
                        + " has released an offer for you."
                        + (event.getCtc() != null
                                ? " CTC: " + event.getCtc() + "K per annum."
                                : "")
        );
    }

    private void handleInterviewScheduled(InterviewScheduledEvent event) {
        log.info("Interview scheduled: studentId={}, interviewId={}",
                event.getStudentId(), event.getInterviewId());

        String details = event.getMode().equals("ONLINE")
                ? "Mode: Online | Link: " + event.getMeetLink()
                : "Mode: Offline | Venue: " + event.getVenue();

        notificationService.create(
                event.getStudentId(),
                "INTERVIEW_SCHEDULED",
                "Interview Scheduled",
                "Your Round " + event.getRound() + " interview with "
                        + event.getCompanyName() + " has been scheduled for "
                        + event.getInterviewDate() + ". " + details
        );
    }

    private void handleInterviewRescheduled(InterviewRescheduledEvent event) {
        log.info("Interview rescheduled: studentId={}", event.getStudentId());

        notificationService.create(
                event.getStudentId(),
                "INTERVIEW_RESCHEDULED",
                "Interview Rescheduled",
                "Your interview has been rescheduled to " + event.getNewDate()
                        + ". Reason: " + event.getReason()
        );
    }

    private void handleInterviewCancelled(InterviewCancelledEvent event) {
        log.info("Interview cancelled: studentId={}", event.getStudentId());

        notificationService.create(
                event.getStudentId(),
                "INTERVIEW_CANCELLED",
                "Interview Cancelled",
                "Your interview has been cancelled. Reason: " + event.getReason()
        );
    }
}
