-- Add appId column to assessment_record table for multi-tenancy support
ALTER TABLE assessment_record ADD COLUMN IF NOT EXISTS app_id BIGINT;

-- Create index for better query performance
CREATE INDEX IF NOT EXISTS idx_assessment_app_id ON assessment_record(app_id);
