CREATE TABLE interviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id UUID NOT NULL,
    student_id UUID NOT NULL,
    job_id UUID NOT NULL,
    company_id UUID NOT NULL,
    company_name VARCHAR(255),
    scheduled_at TIMESTAMP NOT NULL,
    mode VARCHAR(20) NOT NULL,
    meet_link VARCHAR(500),
    venue VARCHAR(500),
    round INTEGER NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    description TEXT,
    reschedule_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_interviews_application_id ON interviews(application_id);
CREATE INDEX idx_interviews_student_id ON interviews(student_id);
CREATE INDEX idx_interviews_company_id ON interviews(company_id);
CREATE INDEX idx_interviews_status ON interviews(status);
