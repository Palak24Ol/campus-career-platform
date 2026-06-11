package com.campus.analytics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "placement_stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementStats {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "total_applications")
    @Builder.Default
    private Long totalApplications = 0L;

    @Column(name = "total_offers")
    @Builder.Default
    private Long totalOffers = 0L;

    @Column(name = "total_active_jobs")
    @Builder.Default
    private Long totalActiveJobs = 0L;

    @Column(name = "average_package", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal averagePackage = BigDecimal.ZERO;

    @Column(name = "highest_package", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal highestPackage = BigDecimal.ZERO;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
