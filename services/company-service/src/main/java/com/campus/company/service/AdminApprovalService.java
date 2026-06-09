package com.campus.company.service;

import com.campus.company.dto.response.CompanyResponse;
import com.campus.company.dto.response.RecruiterResponse;
import com.campus.company.entity.Company;
import com.campus.company.entity.RecruiterProfile;
import com.campus.company.exception.CompanyNotFoundException;
import com.campus.company.exception.InvalidOperationException;
import com.campus.company.exception.RecruiterNotFoundException;
import com.campus.company.repository.CompanyRepository;
import com.campus.company.repository.RecruiterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminApprovalService {

    private final CompanyRepository companyRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final CompanyService companyService;

    public List<CompanyResponse> getPendingCompanies() {
        return companyRepository.findByStatus(Company.CompanyStatus.PENDING)
                .stream().map(companyService::toResponse).toList();
    }

    @Transactional
    public CompanyResponse approveCompany(UUID companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found: " + companyId));

        if (company.getStatus() != Company.CompanyStatus.PENDING) {
            throw new InvalidOperationException("Company is not in PENDING status");
        }

        company.setStatus(Company.CompanyStatus.APPROVED);
        company.setRejectionReason(null);
        companyService.evictCache(companyId);
        return companyService.toResponse(companyRepository.save(company));
    }

    @Transactional
    public CompanyResponse rejectCompany(UUID companyId, String reason) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found: " + companyId));

        if (company.getStatus() != Company.CompanyStatus.PENDING) {
            throw new InvalidOperationException("Company is not in PENDING status");
        }

        company.setStatus(Company.CompanyStatus.REJECTED);
        company.setRejectionReason(reason);
        companyService.evictCache(companyId);
        return companyService.toResponse(companyRepository.save(company));
    }

    public List<RecruiterResponse> getPendingRecruiters() {
        return recruiterProfileRepository.findByVerified(false)
                .stream().map(this::toRecruiterResponse).toList();
    }

    @Transactional
    public RecruiterResponse verifyRecruiter(UUID recruiterId) {
        RecruiterProfile recruiter = recruiterProfileRepository.findById(recruiterId)
                .orElseThrow(() -> new RecruiterNotFoundException("Recruiter not found: " + recruiterId));

        if (recruiter.isVerified()) {
            throw new InvalidOperationException("Recruiter is already verified");
        }

        recruiter.setVerified(true);
        return toRecruiterResponse(recruiterProfileRepository.save(recruiter));
    }

    public RecruiterResponse toRecruiterResponse(RecruiterProfile r) {
        return RecruiterResponse.builder()
                .id(r.getId())
                .userId(r.getUserId())
                .companyId(r.getCompany().getId())
                .companyName(r.getCompany().getName())
                .name(r.getName())
                .email(r.getEmail())
                .phone(r.getPhone())
                .designation(r.getDesignation())
                .verified(r.isVerified())
                .createdAt(r.getCreatedAt())
                .build();
    }
}