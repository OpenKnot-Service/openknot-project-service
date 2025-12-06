package com.openknot.project.repository

import com.openknot.project.entity.ProjectMember
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProjectMemberRepository : CoroutineCrudRepository<ProjectMember, UUID> {

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM project_member
            WHERE project_id = :projectId
            AND user_id = :userId
            AND deleted_at IS NULL
        )
    """)
    suspend fun existsByProjectIdAndUserIdAndDeletedAtIsNull(
        projectId: UUID,
        userId: UUID
    ): Boolean

    @Query("""
        SELECT DISTINCT project_id FROM project_member
        WHERE user_id = :userId
        AND deleted_at IS NULL
    """)
    suspend fun findProjectIdsByUserId(userId: UUID): List<UUID>
}
