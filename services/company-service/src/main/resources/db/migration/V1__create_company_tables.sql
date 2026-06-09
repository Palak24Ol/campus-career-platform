CREATE TABLE companies (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           name VARCHAR(255) NOT NULL,
                           website VARCHAR(255),
                           description TEXT,
                           industry VARCHAR(100),
                           logo_url VARCHAR(500),
                           status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                           rejection_reason TEXT,
                           created_by UUID NOT NULL,
                           created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                           updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE recruiter_profiles (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    user_id UUID NOT NULL UNIQUE,
                                    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
                                    name VARCHAR(100),
                                    email VARCHAR(255),
                                    phone VARCHAR(15),
                                    designation VARCHAR(100),
                                    verified BOOLEAN NOT NULL DEFAULT FALSE,
                                    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_companies_status ON companies(status);
CREATE INDEX idx_recruiter_profiles_user_id ON recruiter_profiles(user_id);
CREATE INDEX idx_recruiter_profiles_company_id ON recruiter_profiles(company_id);
CREATE INDEX idx_recruiter_profiles_verified ON recruiter_profiles(verified);