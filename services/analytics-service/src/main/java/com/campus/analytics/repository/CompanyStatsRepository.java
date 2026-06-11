package com.campus.analytics.repository;

import com.campus.analytics.entity.CompanyStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyStatsRepository extends JpaRepository<CompanyStats, UUID> {

    Optional<CompanyStats> findByCompanyId(UUID companyId);
}
