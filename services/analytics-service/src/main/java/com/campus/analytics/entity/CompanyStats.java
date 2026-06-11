package com.campus.analytics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "company_stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyStats {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "company_id", unique = true, nullable = false)
    private UUID companyId;

    @Column(name = "company_name", length = 255)
    private String companyName;

    @Column(name = "total_applications")
    @Builder.Default
    private Long totalApplications = 0L;

    @Column(name = "total_offers")
    @Builder.Default
    private Long totalOffers = 0L;

    @Column(name = "average_ctc", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal averageCtc = BigDecimal.ZERO;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
