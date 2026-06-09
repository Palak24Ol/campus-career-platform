-
CREATE DATABASE auth_db
    WITH
    OWNER = campus
    ENCODING = 'UTF8'
    TEMPLATE = template0;

COMMENT ON DATABASE auth_db IS 'Auth Service: user accounts, roles, credentials';


-- STUDENT SERVICE DATABASE
-- Tables: student_profiles, education

CREATE DATABASE student_db
    WITH
    OWNER = campus
    ENCODING = 'UTF8'
    TEMPLATE = template0;

COMMENT ON DATABASE student_db IS 'Student Service: profiles, education, resume metadata';

-- COMPANY SERVICE DATABASE
-- Tables: companies, recruiter_profiles

CREATE DATABASE company_db
    WITH
    OWNER = campus
    ENCODING = 'UTF8'
    TEMPLATE = template0;

COMMENT ON DATABASE company_db IS 'Company Service: companies, recruiters, admin approval workflow';

-- JOB SERVICE DATABASE
-- Tables: jobs

CREATE DATABASE job_db
    WITH
    OWNER = campus
    ENCODING = 'UTF8'
    TEMPLATE = template0;

COMMENT ON DATABASE job_db IS 'Job Service: job listings with eligibility criteria';

-- APPLICATION SERVICE DATABASE
-- Tables: applications

CREATE DATABASE application_db
    WITH
    OWNER = campus
    ENCODING = 'UTF8'
    TEMPLATE = template0;

COMMENT ON DATABASE application_db IS 'Application Service: student applications and status lifecycle';

-- INTERVIEW SERVICE DATABASE
-- Tables: interviews

CREATE DATABASE interview_db
    WITH
    OWNER = campus
    ENCODING = 'UTF8'
    TEMPLATE = template0;

COMMENT ON DATABASE interview_db IS 'Interview Service: scheduled interviews and lifecycle';

-- NOTIFICATION SERVICE DATABASE
-- Tables: notifications, notification_preferences

CREATE DATABASE notification_db
    WITH
    OWNER = campus
    ENCODING = 'UTF8'
    TEMPLATE = template0;

COMMENT ON DATABASE notification_db IS 'Notification Service: stored notifications and user preferences';

-- ANALYTICS SERVICE DATABASE
-- Tables: placement_stats, monthly_trends, company_stats

CREATE DATABASE analytics_db
    WITH
    OWNER = campus
    ENCODING = 'UTF8'
    TEMPLATE = template0;

COMMENT ON DATABASE analytics_db IS 'Analytics Service: placement stats, trends, company metrics';

-- GRANT EXPLICIT PRIVILEGES

GRANT ALL PRIVILEGES ON DATABASE auth_db        TO campus;
GRANT ALL PRIVILEGES ON DATABASE student_db     TO campus;
GRANT ALL PRIVILEGES ON DATABASE company_db     TO campus;
GRANT ALL PRIVILEGES ON DATABASE job_db         TO campus;
GRANT ALL PRIVILEGES ON DATABASE application_db TO campus;
GRANT ALL PRIVILEGES ON DATABASE interview_db   TO campus;
GRANT ALL PRIVILEGES ON DATABASE notification_db TO campus;
GRANT ALL PRIVILEGES ON DATABASE analytics_db   TO campus;

-- ENABLE EXTENSIONS IN EACH DATABASE
-- uuid-ossp : Provides uuid_generate_v4() — used by some Hibernate strategies
-- pgcrypto  : Provides gen_random_uuid() — used for UUID PKs in PostgreSQL 14-


\c auth_db
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

\c student_db
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

\c company_db
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

\c job_db
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

\c application_db
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

\c interview_db
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

\c notification_db
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

\c analytics_db
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- VERIFICATION QUERY

\c campus_main
SELECT datname AS database_name,
       pg_encoding_to_char(encoding) AS encoding,
       datcollate AS collation
FROM pg_database
WHERE datname LIKE '%_db'
ORDER BY datname;
