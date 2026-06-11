package com.campus.job.repository;

import com.campus.job.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {

    Page<Job> findByStatus(Job.JobStatus status, Pageable pageable);

    List<Job> findByCompanyId(UUID companyId);

    List<Job> findByCreatedBy(UUID createdBy);
}
