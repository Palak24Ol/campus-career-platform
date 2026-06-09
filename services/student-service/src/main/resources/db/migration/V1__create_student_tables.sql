CREATE TABLE student_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    cgpa DECIMAL(4, 2),
    branch VARCHAR(50),
    graduation_year INTEGER,
    backlogs INTEGER NOT NULL DEFAULT 0,
    bio TEXT,
    skills TEXT[],
    resume_url VARCHAR(500),
    linkedin_url VARCHAR(255),
    github_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE education (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES student_profiles(id) ON DELETE CASCADE,
    degree VARCHAR(100),
    institution VARCHAR(255),
    start_year INTEGER,
    end_year INTEGER,
    grade DECIMAL(4, 2)
);

CREATE INDEX idx_student_profiles_user_id ON student_profiles(user_id);
CREATE INDEX idx_education_student_id ON education(student_id);
