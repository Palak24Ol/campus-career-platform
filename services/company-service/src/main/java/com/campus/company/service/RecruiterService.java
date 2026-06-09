package com.campus.company.service;

import com.campus.company.dto.request.UpdateRecruiterRequest;
import com.campus.company.dto.response.RecruiterResponse;
import com.campus.company.entity.RecruiterProfile;
import com.campus.company.exception.AccessDeniedException;
import com.campus.company.exception.RecruiterNotFoundException;
import com.campus.company.repository.RecruiterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecruiterService {

    private final RecruiterProfileRepository recruiterProfileRepository;
    private final AdminApprovalService adminApprovalService;

    public RecruiterResponse getMyProfile(UUID userId) {
        RecruiterProfile recruiter = recruiterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RecruiterNotFoundException(
                        "Recruiter profile not found for userId: " + userId));
        return adminApprovalService.toRecruiterResponse(recruiter);
    }

    @Transactional
    public RecruiterResponse updateProfile(UUID recruiterId, UUID requestingUserId,
                                           String role, UpdateRecruiterRequest request) {
        RecruiterProfile recruiter = recruiterProfileRepository.findById(recruiterId)
                .orElseThrow(() -> new RecruiterNotFoundException(
                        "Recruiter not found: " + recruiterId));

        if (!role.equals("ADMIN") && !recruiter.getUserId().equals(requestingUserId)) {
            throw new AccessDeniedException();
        }

        if (request.getName() != null) recruiter.setName(request.getName());
        if (request.getPhone() != null) recruiter.setPhone(request.getPhone());
        if (request.getDesignation() != null) recruiter.setDesignation(request.getDesignation());

        return adminApprovalService.toRecruiterResponse(
                recruiterProfileRepository.save(recruiter));
    }
}