package com.openknot.project.repository

import com.openknot.project.entity.Project
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProjectRepository : CoroutineCrudRepository<Project, UUID> {

    @Query("""
        SELECT * FROM project
        WHERE deleted_at IS NULL
          AND (
              -- 비인증 사용자: 공개 프로젝트만
              (:userId IS NULL AND visibility = true)
              OR
              -- 인증된 사용자: 공개 또는 소유자 또는 멤버
              (:userId IS NOT NULL AND (
                  visibility = true
                  OR owner_id = :userId
                  OR (:visibleProjectIdsCount > 0 AND id IN (:visibleProjectIds))
              ))
          )
          AND (:statusCount = 0 OR status IN (:statuses))
          AND (:recruitingStatusCount = 0 OR recruiting_status IN (:recruitingStatuses))
          AND (:ownerId IS NULL OR owner_id = :ownerId)
        ORDER BY
            CASE WHEN :sortField = 'created_at' AND :sortOrder = 'ASC' THEN created_at END ASC,
            CASE WHEN :sortField = 'created_at' AND :sortOrder = 'DESC' THEN created_at END DESC,
            CASE WHEN :sortField = 'modified_at' AND :sortOrder = 'ASC' THEN modified_at END ASC,
            CASE WHEN :sortField = 'modified_at' AND :sortOrder = 'DESC' THEN modified_at END DESC,
            CASE WHEN :sortField = 'name' AND :sortOrder = 'ASC' THEN name END ASC,
            CASE WHEN :sortField = 'name' AND :sortOrder = 'DESC' THEN name END DESC,
            CASE WHEN :sortField = 'start_date' AND :sortOrder = 'ASC' THEN start_date END ASC,
            CASE WHEN :sortField = 'start_date' AND :sortOrder = 'DESC' THEN start_date END DESC,
            CASE WHEN :sortField = 'end_date' AND :sortOrder = 'ASC' THEN end_date END ASC,
            CASE WHEN :sortField = 'end_date' AND :sortOrder = 'DESC' THEN end_date END DESC
        LIMIT :limit OFFSET :offset
    """)
    fun searchProjects(
        userId: UUID?,
        visibleProjectIds: List<UUID>?,
        visibleProjectIdsCount: Int,
        statuses: List<String>?,
        statusCount: Int,
        recruitingStatuses: List<String>?,
        recruitingStatusCount: Int,
        ownerId: UUID?,
        sortField: String,
        sortOrder: String,
        limit: Int,
        offset: Int
    ): Flow<Project>

    @Query("""
        SELECT COUNT(*) FROM project
        WHERE deleted_at IS NULL
          AND (
              -- 비인증 사용자: 공개 프로젝트만
              (:userId IS NULL AND visibility = true)
              OR
              -- 인증된 사용자: 공개 또는 소유자 또는 멤버
              (:userId IS NOT NULL AND (
                  visibility = true
                  OR owner_id = :userId
                  OR (:visibleProjectIdsCount > 0 AND id IN (:visibleProjectIds))
              ))
          )
          AND (:statusCount = 0 OR status IN (:statuses))
          AND (:recruitingStatusCount = 0 OR recruiting_status IN (:recruitingStatuses))
          AND (:ownerId IS NULL OR owner_id = :ownerId)
    """)
    suspend fun countProjects(
        userId: UUID?,
        visibleProjectIds: List<UUID>?,
        visibleProjectIdsCount: Int,
        statuses: List<String>?,
        statusCount: Int,
        recruitingStatuses: List<String>?,
        recruitingStatusCount: Int,
        ownerId: UUID?
    ): Long
}