CREATE TABLE IF NOT EXISTS sys_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(100),
    phone VARCHAR(32),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS assessment_record (
    id BIGSERIAL PRIMARY KEY,
    no VARCHAR(64) NOT NULL UNIQUE,
    assessment_date DATE NOT NULL,
    reason VARCHAR(128),
    elderly_name VARCHAR(100) NOT NULL,
    gender VARCHAR(32),
    birth_date DATE,
    height NUMERIC(8,2),
    weight NUMERIC(8,2),
    ethnicity VARCHAR(64),
    religion VARCHAR(64),
    id_no VARCHAR(64),
    education VARCHAR(128),
    living JSONB NOT NULL DEFAULT '[]',
    marriage VARCHAR(64),
    risks JSONB NOT NULL DEFAULT '{}',
    provider JSONB NOT NULL DEFAULT '{}',
    disease JSONB NOT NULL DEFAULT '[]',
    disease_other TEXT,
    medications JSONB NOT NULL DEFAULT '[]',
    health JSONB NOT NULL DEFAULT '{}',
    answers JSONB NOT NULL DEFAULT '{}',
    total_score NUMERIC(8,2) NOT NULL DEFAULT 0,
    initial_level INTEGER,
    final_level INTEGER,
    created_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_assessment_user FOREIGN KEY (created_by) REFERENCES sys_user(id)
);

CREATE INDEX IF NOT EXISTS idx_assessment_name ON assessment_record(elderly_name);
CREATE INDEX IF NOT EXISTS idx_assessment_date ON assessment_record(assessment_date);
CREATE INDEX IF NOT EXISTS idx_assessment_creator ON assessment_record(created_by);

CREATE TABLE IF NOT EXISTS assessment_operation_log (
    id BIGSERIAL PRIMARY KEY,
    record_id BIGINT,
    user_id BIGINT,
    operation VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
