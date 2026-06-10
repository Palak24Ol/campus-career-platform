package com.campus.job.service;

import com.campus.job.dto.response.EligibilityResponse;
import com.campus.job.dto.response.StudentProfileDto;
import com.campus.job.entity.Job;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class EligibilityEngine {

    public EligibilityResponse check(Job job, StudentProfileDto student) {
        if (student == null) {
            return EligibilityResponse.builder()
                    .eligible(false)
                    .reason("Student profile not found")
                    .build();
        }

        if (job.getMinCgpa() != null && student.getCgpa() != null) {
            if (student.getCgpa().compareTo(job.getMinCgpa()) < 0) {
                return EligibilityResponse.builder()
                        .eligible(false)
                        .reason("CGPA " + student.getCgpa() + " is below minimum required "
                                + job.getMinCgpa())
                        .build();
            }
        }

        if (!job.isBacklogsAllowed() && student.getBacklogs() != null
                && student.getBacklogs() > 0) {
            return EligibilityResponse.builder()
                    .eligible(false)
                    .reason("Backlogs not allowed for this job")
                    .build();
        }

        if (job.getEligibleBranches() != null && job.getEligibleBranches().length > 0
                && student.getBranch() != null) {
            List<String> eligible = Arrays.asList(job.getEligibleBranches());
            if (!eligible.contains(student.getBranch())) {
                return EligibilityResponse.builder()
                        .eligible(false)
                        .reason("Branch " + student.getBranch() + " is not eligible. "
                                + "Eligible branches: " + String.join(", ", eligible))
                        .build();
            }
        }

        if (job.getGraduationYear() != null && student.getGraduationYear() != null) {
            if (!student.getGraduationYear().equals(job.getGraduationYear())) {
                return EligibilityResponse.builder()
                        .eligible(false)
                        .reason("Graduation year " + student.getGraduationYear()
                                + " does not match required " + job.getGraduationYear())
                        .build();
            }
        }

        return EligibilityResponse.builder()
                .eligible(true)
                .reason("Student meets all eligibility criteria")
                .build();
    }
}
