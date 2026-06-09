package com.campus.company.repository;

import com.campus.company.entity.RecruiterProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile, UUID> {

    Optional<RecruiterProfile> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    List<RecruiterProfile> findByCompanyId(UUID companyId);

    List<RecruiterProfile> findByVerified(boolean verified);
}