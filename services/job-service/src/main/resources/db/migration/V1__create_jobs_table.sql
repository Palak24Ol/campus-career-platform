CREATE TABLE jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    company_id UUID NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    ctc BIGINT,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    min_cgpa DECIMAL(4, 2) DEFAULT 0.0,
    eligible_branches TEXT[],
    backlogs_allowed BOOLEAN NOT NULL DEFAULT TRUE,
    graduation_year INTEGER,
    deadline DATE,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_jobs_status ON jobs(status);
CREATE INDEX idx_jobs_company_id ON jobs(company_id);
CREATE INDEX idx_jobs_created_by ON jobs(created_by);
CREATE INDEX idx_jobs_type ON jobs(type);
