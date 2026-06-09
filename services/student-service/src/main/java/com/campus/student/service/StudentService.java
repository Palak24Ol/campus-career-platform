package com.campus.student.service;

import com.campus.student.dto.request.EducationRequest;
import com.campus.student.dto.request.UpdateStudentRequest;
import com.campus.student.dto.response.EducationResponse;
import com.campus.student.dto.response.StudentResponse;
import com.campus.student.entity.Education;
import com.campus.student.entity.StudentProfile;
import com.campus.student.exception.AccessDeniedException;
import com.campus.student.exception.StudentNotFoundException;
import com.campus.student.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentProfileRepository studentProfileRepository;

    @Transactional
    public StudentResponse getOrCreateProfile(UUID userId, String email, String name) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    StudentProfile newProfile = StudentProfile.builder()
                            .userId(userId)
                            .email(email)
                            .name(name != null ? name : email)
                            .build();
                    return studentProfileRepository.save(newProfile);
                });
        return toResponse(profile);
    }

    public StudentResponse getByUserId(UUID userId) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new StudentNotFoundException("Student profile not found for userId: " + userId));
        return toResponse(profile);
    }

    public StudentResponse getById(UUID profileId) {
        StudentProfile profile = studentProfileRepository.findById(profileId)
                .orElseThrow(() -> new StudentNotFoundException("Student profile not found: " + profileId));
        return toResponse(profile);
    }

    @Transactional
    public StudentResponse updateProfile(UUID profileId, UUID requestingUserId,
                                         String requestingRole, UpdateStudentRequest request) {
        StudentProfile profile = studentProfileRepository.findById(profileId)
                .orElseThrow(() -> new StudentNotFoundException("Student profile not found: " + profileId));

        if (!requestingRole.equals("ADMIN") && !profile.getUserId().equals(requestingUserId)) {
            throw new AccessDeniedException();
        }

        if (request.getName() != null) profile.setName(request.getName());
        if (request.getPhone() != null) profile.setPhone(request.getPhone());
        if (request.getCgpa() != null) profile.setCgpa(request.getCgpa());
        if (request.getBranch() != null) profile.setBranch(request.getBranch());
        if (request.getGraduationYear() != null) profile.setGraduationYear(request.getGraduationYear());
        if (request.getBacklogs() != null) profile.setBacklogs(request.getBacklogs());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getLinkedinUrl() != null) profile.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getGithubUrl() != null) profile.setGithubUrl(request.getGithubUrl());

        if (request.getSkills() != null) {
            profile.setSkills(request.getSkills().toArray(new String[0]));
        }

        if (request.getEducations() != null) {
            profile.getEducations().clear();
            for (EducationRequest edu : request.getEducations()) {
                Education education = Education.builder()
                        .studentProfile(profile)
                        .degree(edu.getDegree())
                        .institution(edu.getInstitution())
                        .startYear(edu.getStartYear())
                        .endYear(edu.getEndYear())
                        .grade(edu.getGrade())
                        .build();
                profile.getEducations().add(education);
            }
        }

        return toResponse(studentProfileRepository.save(profile));
    }

    @Transactional
    public void updateResumeUrl(UUID profileId, String resumeUrl) {
        StudentProfile profile = studentProfileRepository.findById(profileId)
                .orElseThrow(() -> new StudentNotFoundException("Student profile not found: " + profileId));
        profile.setResumeUrl(resumeUrl);
        studentProfileRepository.save(profile);
    }

    public StudentResponse toResponse(StudentProfile profile) {
        List<EducationResponse> educations = profile.getEducations().stream()
                .map(edu -> EducationResponse.builder()
                        .id(edu.getId())
                        .degree(edu.getDegree())
                        .institution(edu.getInstitution())
                        .startYear(edu.getStartYear())
                        .endYear(edu.getEndYear())
                        .grade(edu.getGrade())
                        .build())
                .toList();

        return StudentResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .name(profile.getName())
                .email(profile.getEmail())
                .phone(profile.getPhone())
                .cgpa(profile.getCgpa())
                .branch(profile.getBranch())
                .graduationYear(profile.getGraduationYear())
                .backlogs(profile.getBacklogs())
                .bio(profile.getBio())
                .skills(profile.getSkills())
                .resumeUrl(profile.getResumeUrl() != null
                        ? profile.getResumeUrl()
                        : null)
                .linkedinUrl(profile.getLinkedinUrl())
                .githubUrl(profile.getGithubUrl())
                .educations(educations)
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
