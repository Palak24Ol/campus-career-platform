package com.campus.job.service;

import com.campus.events.JobCreatedEvent;
import com.campus.job.client.StudentServiceClient;
import com.campus.job.dto.request.CreateJobRequest;
import com.campus.job.dto.request.UpdateJobRequest;
import com.campus.job.dto.response.EligibilityResponse;
import com.campus.job.dto.response.JobResponse;
import com.campus.job.dto.response.StudentProfileDto;
import com.campus.job.entity.Job;
import com.campus.job.exception.AccessDeniedException;
import com.campus.job.exception.JobNotFoundException;
import com.campus.job.kafka.JobEventProducer;
import com.campus.job.repository.JobRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobEventProducer jobEventProducer;
    private final StudentServiceClient studentServiceClient;
    private final EligibilityEngine eligibilityEngine;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_PREFIX = "job_feed:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    @Transactional
    public JobResponse createJob(UUID recruiterId, CreateJobRequest request) {
        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .companyId(request.getCompanyId())
                .companyName(request.getCompanyName())
                .location(request.getLocation())
                .ctc(request.getCtc())
                .type(request.getType())
                .minCgpa(request.getMinCgpa())
                .eligibleBranches(request.getEligibleBranches() != null
                        ? request.getEligibleBranches().toArray(new String[0])
                        : new String[0])
                .backlogsAllowed(request.getBacklogsAllowed() != null
                        ? request.getBacklogsAllowed() : true)
                .graduationYear(request.getGraduationYear())
                .deadline(request.getDeadline())
                .createdBy(recruiterId)
                .build();

        job = jobRepository.save(job);
        evictFeedCache();

        JobCreatedEvent event = JobCreatedEvent.builder()
                .jobId(job.getId())
                .title(job.getTitle())
                .companyId(job.getCompanyId())
                .companyName(job.getCompanyName())
                .location(job.getLocation())
                .type(job.getType().name())
                .minCgpa(job.getMinCgpa() != null ? job.getMinCgpa().doubleValue() : 0.0)
                .eligibleBranches(job.getEligibleBranches() != null
                        ? java.util.Arrays.asList(job.getEligibleBranches())
                        : java.util.List.of())
                .backlogsAllowed(job.isBacklogsAllowed())
                .graduationYear(job.getGraduationYear())
                .timestamp(Instant.now())
                .build();

        jobEventProducer.publishJobCreated(event);

        return toResponse(job);
    }

    public Page<JobResponse> getOpenJobs(Pageable pageable) {
        return jobRepository.findByStatus(Job.JobStatus.OPEN, pageable)
                .map(this::toResponse);
    }

    public JobResponse getById(UUID jobId) {
        return toResponse(findById(jobId));
    }

    @Transactional
    public JobResponse updateJob(UUID jobId, UUID requestingUserId,
                                  String role, UpdateJobRequest request) {
        Job job = findById(jobId);

        if (!role.equals("ADMIN") && !job.getCreatedBy().equals(requestingUserId)) {
            throw new AccessDeniedException();
        }

        if (request.getTitle() != null) job.setTitle(request.getTitle());
        if (request.getDescription() != null) job.setDescription(request.getDescription());
        if (request.getLocation() != null) job.setLocation(request.getLocation());
        if (request.getCtc() != null) job.setCtc(request.getCtc());
        if (request.getStatus() != null) job.setStatus(request.getStatus());
        if (request.getMinCgpa() != null) job.setMinCgpa(request.getMinCgpa());
        if (request.getBacklogsAllowed() != null) job.setBacklogsAllowed(request.getBacklogsAllowed());
        if (request.getGraduationYear() != null) job.setGraduationYear(request.getGraduationYear());
        if (request.getDeadline() != null) job.setDeadline(request.getDeadline());
        if (request.getEligibleBranches() != null) {
            job.setEligibleBranches(request.getEligibleBranches().toArray(new String[0]));
        }

        job = jobRepository.save(job);
        evictFeedCache();
        return toResponse(job);
    }

    @Transactional
    public void deleteJob(UUID jobId, UUID requestingUserId, String role) {
        Job job = findById(jobId);
        if (!role.equals("ADMIN") && !job.getCreatedBy().equals(requestingUserId)) {
            throw new AccessDeniedException();
        }
        jobRepository.delete(job);
        evictFeedCache();
    }

    public EligibilityResponse checkEligibility(UUID jobId, UUID studentUserId) {
        Job job = findById(jobId);
        StudentProfileDto student = studentServiceClient.getStudentByUserId(studentUserId);
        return eligibilityEngine.check(job, student);
    }

    private Job findById(UUID jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found: " + jobId));
    }

    private void evictFeedCache() {
        try {
            var keys = redisTemplate.keys(CACHE_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.warn("Failed to evict job feed cache: {}", e.getMessage());
        }
    }

    public JobResponse toResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .companyId(job.getCompanyId())
                .companyName(job.getCompanyName())
                .location(job.getLocation())
                .ctc(job.getCtc())
                .type(job.getType())
                .status(job.getStatus())
                .minCgpa(job.getMinCgpa())
                .eligibleBranches(job.getEligibleBranches())
                .backlogsAllowed(job.isBacklogsAllowed())
                .graduationYear(job.getGraduationYear())
                .deadline(job.getDeadline())
                .createdBy(job.getCreatedBy())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
