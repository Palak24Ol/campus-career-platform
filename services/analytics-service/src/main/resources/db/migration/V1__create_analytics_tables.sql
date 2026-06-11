CREATE TABLE placement_stats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    total_applications BIGINT NOT NULL DEFAULT 0,
    total_offers BIGINT NOT NULL DEFAULT 0,
    total_active_jobs BIGINT NOT NULL DEFAULT 0,
    average_package DECIMAL(10, 2) NOT NULL DEFAULT 0.0,
    highest_package DECIMAL(10, 2) NOT NULL DEFAULT 0.0,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE monthly_trends (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    year INTEGER NOT NULL,
    month INTEGER NOT NULL,
    applications BIGINT NOT NULL DEFAULT 0,
    offers BIGINT NOT NULL DEFAULT 0,
    average_ctc DECIMAL(10, 2) NOT NULL DEFAULT 0.0,
    CONSTRAINT uq_year_month UNIQUE (year, month)
);

CREATE TABLE company_stats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL UNIQUE,
    company_name VARCHAR(255),
    total_applications BIGINT NOT NULL DEFAULT 0,
    total_offers BIGINT NOT NULL DEFAULT 0,
    average_ctc DECIMAL(10, 2) NOT NULL DEFAULT 0.0,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_company_stats_company_id ON company_stats(company_id);
CREATE INDEX idx_monthly_trends_year_month ON monthly_trends(year, month);
