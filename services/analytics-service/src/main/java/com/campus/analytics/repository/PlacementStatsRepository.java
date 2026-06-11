package com.campus.analytics.repository;

import com.campus.analytics.entity.PlacementStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlacementStatsRepository extends JpaRepository<PlacementStats, UUID> {

    Optional<PlacementStats> findFirstByOrderByUpdatedAtDesc();
}
