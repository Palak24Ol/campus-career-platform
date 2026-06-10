package com.campus.interview.repository;

import com.campus.interview.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, UUID> {

    List<Interview> findByStudentId(UUID studentId);

    List<Interview> findByApplicationId(UUID applicationId);

    List<Interview> findByCompanyId(UUID companyId);
}
