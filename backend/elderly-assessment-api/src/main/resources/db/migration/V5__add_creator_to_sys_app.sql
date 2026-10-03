-- Add creator_id column to sys_app table to track the user who created/owns the app
ALTER TABLE sys_app ADD COLUMN IF NOT EXISTS creator_id BIGINT;

-- Backfill creator_id for existing apps: the first user (earliest created_at) bound to the app is treated as the creator
UPDATE sys_app a
SET creator_id = (
    SELECT u.id
    FROM sys_user u
    WHERE u.app_id = a.id
    ORDER BY u.created_at ASC
    LIMIT 1
)
WHERE a.creator_id IS NULL;

CREATE INDEX IF NOT EXISTS idx_sys_app_creator ON sys_app(creator_id);
