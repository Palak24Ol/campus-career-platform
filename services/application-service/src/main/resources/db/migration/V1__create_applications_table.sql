CREATE TABLE applications (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              student_id UUID NOT NULL,
                              job_id UUID NOT NULL,
                              student_name VARCHAR(100),
                              job_title VARCHAR(255),
                              company_name VARCHAR(255),
                              company_id UUID,
                              status VARCHAR(20) NOT NULL DEFAULT 'APPLIED',
                              recruiter_note TEXT,
                              applied_at TIMESTAMP NOT NULL DEFAULT NOW(),
                              updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
                              CONSTRAINT uq_student_job UNIQUE (student_id, job_id)
);

CREATE INDEX idx_applications_student_id ON applications(student_id);
CREATE INDEX idx_applications_job_id ON applications(job_id);
CREATE INDEX idx_applications_status ON applications(status);