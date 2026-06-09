package com.campus.company.service;

import com.campus.company.dto.request.CreateCompanyRequest;
import com.campus.company.dto.request.UpdateCompanyRequest;
import com.campus.company.dto.response.CompanyResponse;
import com.campus.company.entity.Company;
import com.campus.company.entity.RecruiterProfile;
import com.campus.company.exception.AccessDeniedException;
import com.campus.company.exception.CompanyNotFoundException;
import com.campus.company.exception.InvalidOperationException;
import com.campus.company.repository.CompanyRepository;
import com.campus.company.repository.RecruiterProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_PREFIX = "company:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    @Transactional
    public CompanyResponse createCompany(UUID recruiterId, String recruiterEmail,
                                         CreateCompanyRequest request) {
        if (recruiterProfileRepository.existsByUserId(recruiterId)) {
            throw new InvalidOperationException("Recruiter already belongs to a company");
        }

        Company company = Company.builder()
                .name(request.getName())
                .website(request.getWebsite())
                .description(request.getDescription())
                .industry(request.getIndustry())
                .logoUrl(request.getLogoUrl())
                .createdBy(recruiterId)
                .build();

        company = companyRepository.save(company);

        RecruiterProfile recruiter = RecruiterProfile.builder()
                .userId(recruiterId)
                .company(company)
                .email(recruiterEmail)
                .build();

        recruiterProfileRepository.save(recruiter);

        return toResponse(company);
    }

    public List<CompanyResponse> getAllApprovedCompanies() {
        return companyRepository.findByStatus(Company.CompanyStatus.APPROVED)
                .stream().map(this::toResponse).toList();
    }

    public CompanyResponse getById(UUID companyId) {
        String cacheKey = CACHE_PREFIX + companyId;
        try {
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return objectMapper.readValue(cached, CompanyResponse.class);
            }
        } catch (Exception e) {
            log.warn("Cache read failed for company {}", companyId);
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found: " + companyId));

        CompanyResponse response = toResponse(company);

        try {
            redisTemplate.opsForValue().set(cacheKey,
                    objectMapper.writeValueAsString(response), CACHE_TTL);
        } catch (Exception e) {
            log.warn("Cache write failed for company {}", companyId);
        }

        return response;
    }

    @Transactional
    public CompanyResponse updateCompany(UUID companyId, UUID requestingUserId,
                                         String role, UpdateCompanyRequest request) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found: " + companyId));

        if (!role.equals("ADMIN") && !company.getCreatedBy().equals(requestingUserId)) {
            throw new AccessDeniedException();
        }

        if (request.getWebsite() != null) company.setWebsite(request.getWebsite());
        if (request.getDescription() != null) company.setDescription(request.getDescription());
        if (request.getIndustry() != null) company.setIndustry(request.getIndustry());
        if (request.getLogoUrl() != null) company.setLogoUrl(request.getLogoUrl());

        company = companyRepository.save(company);
        evictCache(companyId);
        return toResponse(company);
    }

    public CompanyResponse toResponse(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .website(company.getWebsite())
                .description(company.getDescription())
                .industry(company.getIndustry())
                .logoUrl(company.getLogoUrl())
                .status(company.getStatus())
                .rejectionReason(company.getRejectionReason())
                .createdBy(company.getCreatedBy())
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .build();
    }

    public void evictCache(UUID companyId) {
        redisTemplate.delete(CACHE_PREFIX + companyId);
    }
}