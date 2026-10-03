-- Add app_id, photo, email columns to sys_user table for multi-tenancy and profile support
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS app_id BIGINT;
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS photo VARCHAR(512);
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS email VARCHAR(128);

-- Index for faster app-based user lookups
CREATE INDEX IF NOT EXISTS idx_sys_user_app_id ON sys_user(app_id);
