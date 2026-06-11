package com.campus.application.service;

import com.campus.application.client.JobServiceClient;
import com.campus.application.dto.request.SubmitApplicationRequest;
import com.campus.application.dto.request.UpdateStatusRequest;
import com.campus.application.dto.response.ApplicationResponse;
import com.campus.application.dto.response.EligibilityResponse;
import com.campus.application.entity.Application;
import com.campus.application.exception.*;
import com.campus.application.kafka.ApplicationEventProducer;
import com.campus.application.repository.ApplicationRepository;
import com.campus.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobServiceClient jobServiceClient;
    private final ApplicationEventProducer eventProducer;

    @Transactional
    public ApplicationResponse submit(UUID studentId, SubmitApplicationRequest request) {
        if (applicationRepository.existsByStudentIdAndJobId(studentId, request.getJobId())) {
            throw new DuplicateApplicationException();
        }

        EligibilityResponse eligibility = jobServiceClient.checkEligibility(
                request.getJobId(), studentId);

        if (!eligibility.isEligible()) {
            throw new EligibilityFailedException(eligibility.getReason());
        }

        Application application = Application.builder()
                .studentId(studentId)
                .jobId(request.getJobId())
                .studentName(request.getStudentName())
                .jobTitle(request.getJobTitle())
                .companyName(request.getCompanyName())
                .companyId(request.getCompanyId())
                .build();

        application = applicationRepository.save(application);

        eventProducer.publishSubmitted(ApplicationSubmittedEvent.builder()
                .applicationId(application.getId())
                .studentId(studentId)
                .jobId(request.getJobId())
                .studentName(request.getStudentName())
                .jobTitle(request.getJobTitle())
                .companyName(request.getCompanyName())
                .timestamp(Instant.now())
                .build());

        return toResponse(application);
    }

    public List<ApplicationResponse> getByStudentId(UUID studentId) {
        return applicationRepository.findByStudentId(studentId)
                .stream().map(this::toResponse).toList();
    }

    public List<ApplicationResponse> getByJobId(UUID jobId) {
        return applicationRepository.findByJobId(jobId)
                .stream().map(this::toResponse).toList();
    }

    public ApplicationResponse getById(UUID applicationId) {
        return toResponse(findById(applicationId));
    }

    @Transactional
    public ApplicationResponse updateStatus(UUID applicationId, UUID requestingUserId,
                                            String role, UpdateStatusRequest request) {
        Application application = findById(applicationId);

        if (!role.equals("RECRUITER") && !role.equals("ADMIN")) {
            throw new AccessDeniedException();
        }

        validateTransition(application.getStatus(), request.getStatus());

        application.setStatus(request.getStatus());
        if (request.getRecruiterNote() != null) {
            application.setRecruiterNote(request.getRecruiterNote());
        }

        application = applicationRepository.save(application);

        publishStatusEvent(application, request);

        return toResponse(application);
    }

    @Transactional
    public void withdraw(UUID applicationId, UUID studentId) {
        Application application = findById(applicationId);

        if (!application.getStudentId().equals(studentId)) {
            throw new AccessDeniedException();
        }

        if (application.getStatus() != Application.ApplicationStatus.APPLIED) {
            throw new InvalidStatusTransitionException(
                    application.getStatus().name(), "WITHDRAWN");
        }

        eventProducer.publishWithdrawn(ApplicationWithdrawnEvent.builder()
                .applicationId(application.getId())
                .studentId(application.getStudentId())
                .jobId(application.getJobId())
                .timestamp(Instant.now())
                .build());

        applicationRepository.delete(application);
    }

    private void validateTransition(Application.ApplicationStatus current,
                                    Application.ApplicationStatus next) {
        boolean valid = switch (current) {
            case APPLIED -> next == Application.ApplicationStatus.SHORTLISTED
                    || next == Application.ApplicationStatus.REJECTED;
            case SHORTLISTED -> next == Application.ApplicationStatus.OFFERED
                    || next == Application.ApplicationStatus.REJECTED;
            default -> false;
        };

        if (!valid) {
            throw new InvalidStatusTransitionException(current.name(), next.name());
        }
    }

    private void publishStatusEvent(Application application, UpdateStatusRequest request) {
        switch (application.getStatus()) {
            case SHORTLISTED -> eventProducer.publishShortlisted(
                    StudentShortlistedEvent.builder()
                            .applicationId(application.getId())
                            .studentId(application.getStudentId())
                            .jobId(application.getJobId())
                            .companyName(application.getCompanyName())
                            .recruiterNote(application.getRecruiterNote())
                            .timestamp(Instant.now())
                            .build());

            case OFFERED -> eventProducer.publishOfferReleased(
                    OfferReleasedEvent.builder()
                            .applicationId(application.getId())
                            .studentId(application.getStudentId())
                            .jobId(application.getJobId())
                            .companyName(application.getCompanyName())
                            .ctc(request.getCtc())
                            .joiningDate(request.getJoiningDate())
                            .timestamp(Instant.now())
                            .build());

            default -> log.debug("No event published for status: {}", application.getStatus());
        }
    }

    private Application findById(UUID id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "Application not found: " + id));
    }

    public ApplicationResponse toResponse(Application app) {
        return ApplicationResponse.builder()
                .id(app.getId())
                .studentId(app.getStudentId())
                .jobId(app.getJobId())
                .studentName(app.getStudentName())
                .jobTitle(app.getJobTitle())
                .companyName(app.getCompanyName())
                .companyId(app.getCompanyId())
                .status(app.getStatus())
                .recruiterNote(app.getRecruiterNote())
                .appliedAt(app.getAppliedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
    }
}