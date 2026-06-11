package com.campus.analytics.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
    name = "monthly_trends",
    uniqueConstraints = @UniqueConstraint(columnNames = {"year", "month"})
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyTrend {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Builder.Default
    private Long applications = 0L;

    @Builder.Default
    private Long offers = 0L;

    @Column(name = "average_ctc", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal averageCtc = BigDecimal.ZERO;
}
