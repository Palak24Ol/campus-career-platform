package com.campus.interview.service;

import com.campus.events.InterviewCancelledEvent;
import com.campus.events.InterviewRescheduledEvent;
import com.campus.events.InterviewScheduledEvent;
import com.campus.interview.client.ApplicationServiceClient;
import com.campus.interview.dto.request.CancelInterviewRequest;
import com.campus.interview.dto.request.RescheduleInterviewRequest;
import com.campus.interview.dto.request.ScheduleInterviewRequest;
import com.campus.interview.dto.response.ApplicationStatusDto;
import com.campus.interview.dto.response.InterviewResponse;
import com.campus.interview.entity.Interview;
import com.campus.interview.exception.AccessDeniedException;
import com.campus.interview.exception.InterviewNotFoundException;
import com.campus.interview.exception.InvalidOperationException;
import com.campus.interview.kafka.InterviewEventProducer;
import com.campus.interview.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationServiceClient applicationServiceClient;
    private final InterviewEventProducer eventProducer;

    @Transactional
    public InterviewResponse schedule(UUID recruiterId, ScheduleInterviewRequest request) {
        ApplicationStatusDto application = applicationServiceClient
                .getApplication(request.getApplicationId());

        if (application == null) {
            throw new InvalidOperationException(
                    "Application not found or application service unavailable");
        }

        if (!"SHORTLISTED".equals(application.getStatus())) {
            throw new InvalidOperationException(
                    "Interview can only be scheduled for SHORTLISTED applications. " +
                    "Current status: " + application.getStatus());
        }

        Interview interview = Interview.builder()
                .applicationId(request.getApplicationId())
                .studentId(request.getStudentId())
                .jobId(request.getJobId())
                .companyId(request.getCompanyId())
                .companyName(request.getCompanyName())
                .scheduledAt(request.getScheduledAt())
                .mode(request.getMode())
                .meetLink(request.getMeetLink())
                .venue(request.getVenue())
                .round(request.getRound() != null ? request.getRound() : 1)
                .description(request.getDescription())
                .build();

        interview = interviewRepository.save(interview);

        eventProducer.publishScheduled(InterviewScheduledEvent.builder()
                .interviewId(interview.getId())
                .applicationId(interview.getApplicationId())
                .studentId(interview.getStudentId())
                .jobId(interview.getJobId())
                .companyName(interview.getCompanyName())
                .interviewDate(interview.getScheduledAt().toInstant(java.time.ZoneOffset.UTC))
                .mode(interview.getMode().name())
                .meetLink(interview.getMeetLink())
                .venue(interview.getVenue())
                .round(interview.getRound())
                .timestamp(Instant.now())
                .build());

        return toResponse(interview);
    }

    public InterviewResponse getById(UUID interviewId) {
        return toResponse(findById(interviewId));
    }

    public List<InterviewResponse> getByStudentId(UUID studentId) {
        return interviewRepository.findByStudentId(studentId)
                .stream().map(this::toResponse).toList();
    }

    public List<InterviewResponse> getByApplicationId(UUID applicationId) {
        return interviewRepository.findByApplicationId(applicationId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public InterviewResponse reschedule(UUID interviewId, UUID requestingUserId,
                                         String role, RescheduleInterviewRequest request) {
        Interview interview = findById(interviewId);
        assertActive(interview);
        assertRecruiterOrAdmin(role);

        LocalDateTime oldDate = interview.getScheduledAt();

        interview.setScheduledAt(request.getNewScheduledAt());
        interview.setStatus(Interview.InterviewStatus.RESCHEDULED);
        interview.setRescheduleReason(request.getReason());
        if (request.getMeetLink() != null) interview.setMeetLink(request.getMeetLink());
        if (request.getVenue() != null) interview.setVenue(request.getVenue());

        interview = interviewRepository.save(interview);

        eventProducer.publishRescheduled(InterviewRescheduledEvent.builder()
                .interviewId(interview.getId())
                .applicationId(interview.getApplicationId())
                .studentId(interview.getStudentId())
                .newDate(request.getNewScheduledAt().toInstant(java.time.ZoneOffset.UTC))
                .oldDate(oldDate.toInstant(java.time.ZoneOffset.UTC))
                .reason(request.getReason())
                .timestamp(Instant.now())
                .build());

        return toResponse(interview);
    }

    @Transactional
    public InterviewResponse cancel(UUID interviewId, UUID requestingUserId,
                                     String role, CancelInterviewRequest request) {
        Interview interview = findById(interviewId);
        assertActive(interview);
        assertRecruiterOrAdmin(role);

        interview.setStatus(Interview.InterviewStatus.CANCELLED);
        interview.setRescheduleReason(request.getReason());
        interview = interviewRepository.save(interview);

        eventProducer.publishCancelled(InterviewCancelledEvent.builder()
                .interviewId(interview.getId())
                .applicationId(interview.getApplicationId())
                .studentId(interview.getStudentId())
                .reason(request.getReason())
                .timestamp(Instant.now())
                .build());

        return toResponse(interview);
    }

    @Transactional
    public InterviewResponse complete(UUID interviewId, String role) {
        Interview interview = findById(interviewId);
        assertRecruiterOrAdmin(role);

        if (interview.getStatus() == Interview.InterviewStatus.CANCELLED) {
            throw new InvalidOperationException("Cannot complete a cancelled interview");
        }

        interview.setStatus(Interview.InterviewStatus.COMPLETED);
        return toResponse(interviewRepository.save(interview));
    }

    private Interview findById(UUID id) {
        return interviewRepository.findById(id)
                .orElseThrow(() -> new InterviewNotFoundException(
                        "Interview not found: " + id));
    }

    private void assertActive(Interview interview) {
        if (interview.getStatus() == Interview.InterviewStatus.CANCELLED ||
            interview.getStatus() == Interview.InterviewStatus.COMPLETED) {
            throw new InvalidOperationException(
                    "Cannot modify a " + interview.getStatus().name().toLowerCase() + " interview");
        }
    }

    private void assertRecruiterOrAdmin(String role) {
        if (!"RECRUITER".equals(role) && !"ADMIN".equals(role)) {
            throw new AccessDeniedException();
        }
    }

    public InterviewResponse toResponse(Interview i) {
        return InterviewResponse.builder()
                .id(i.getId())
                .applicationId(i.getApplicationId())
                .studentId(i.getStudentId())
                .jobId(i.getJobId())
                .companyId(i.getCompanyId())
                .companyName(i.getCompanyName())
                .scheduledAt(i.getScheduledAt())
                .mode(i.getMode())
                .meetLink(i.getMeetLink())
                .venue(i.getVenue())
                .round(i.getRound())
                .status(i.getStatus())
                .description(i.getDescription())
                .rescheduleReason(i.getRescheduleReason())
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .build();
    }
}
