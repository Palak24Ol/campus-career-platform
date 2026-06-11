package com.campus.analytics.repository;

import com.campus.analytics.entity.MonthlyTrend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MonthlyTrendRepository extends JpaRepository<MonthlyTrend, UUID> {

    Optional<MonthlyTrend> findByYearAndMonth(int year, int month);

    List<MonthlyTrend> findAllByOrderByYearAscMonthAsc();
}
