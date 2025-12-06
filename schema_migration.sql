-- Project Search Feature - Database Migration Script
-- This script creates the project_member table and adds necessary indexes

-- =============================================================================
-- 1. Create project_member table
-- =============================================================================
CREATE TABLE IF NOT EXISTS project_member (
    id BINARY(16) PRIMARY KEY COMMENT 'UUIDv7 primary key',
    project_id BINARY(16) NOT NULL COMMENT 'Foreign key to project.id',
    user_id BINARY(16) NOT NULL COMMENT 'Foreign key to user.id',
    position_id BINARY(16) NOT NULL COMMENT 'Position/role reference',
    created_at DATETIME(6) NOT NULL COMMENT 'Created timestamp',
    deleted_at DATETIME(6) DEFAULT NULL COMMENT 'Soft delete timestamp',

    CONSTRAINT uk_project_user UNIQUE (project_id, user_id),
    INDEX idx_user_id_deleted (user_id, deleted_at),
    INDEX idx_project_id_deleted (project_id, deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Project member relationships with soft delete support';

-- =============================================================================
-- 2. Add indexes to project table for search optimization
-- =============================================================================

-- Soft delete filtering
CREATE INDEX IF NOT EXISTS idx_project_deleted_at ON project(deleted_at);

-- Visibility filtering (composite index for public/private queries)
CREATE INDEX IF NOT EXISTS idx_project_visibility_deleted ON project(visibility, deleted_at);

-- Status filtering
CREATE INDEX IF NOT EXISTS idx_project_status_deleted ON project(status, deleted_at);

-- Owner filtering
CREATE INDEX IF NOT EXISTS idx_project_owner_deleted ON project(owner_id, deleted_at);

-- Recruiting status filtering
CREATE INDEX IF NOT EXISTS idx_project_recruiting_deleted ON project(recruiting_status, deleted_at);

-- Sorting indexes (DESC for default order)
CREATE INDEX IF NOT EXISTS idx_project_created_at_desc ON project(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_project_modified_at_desc ON project(modified_at DESC);
CREATE INDEX IF NOT EXISTS idx_project_name ON project(name);
CREATE INDEX IF NOT EXISTS idx_project_start_date ON project(start_date);
CREATE INDEX IF NOT EXISTS idx_project_end_date ON project(end_date);

-- =============================================================================
-- Migration complete
-- =============================================================================

-- Verification queries (optional):
-- SELECT COUNT(*) FROM project_member;
-- SHOW INDEX FROM project;
-- SHOW INDEX FROM project_member;
