package com.openknot.project.dto

import com.openknot.project.entity.Project
import com.openknot.project.entity.ProjectStatus
import com.openknot.project.entity.RecruitingStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class ProjectSearchResponse(
    val items: List<ProjectDto>,
    val pagination: PaginationMeta
)

data class ProjectDto(
    val id: UUID,
    val ownerId: UUID,
    val name: String,
    val description: String?,
    val visibility: Boolean,
    val status: ProjectStatus,
    val recruitingStatus: RecruitingStatus,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime?
) {
    companion object {
        fun from(project: Project): ProjectDto {
            return ProjectDto(
                id = project.getId(),
                ownerId = project.ownerId,
                name = project.name,
                description = project.description,
                visibility = project.visibility,
                status = project.status,
                recruitingStatus = project.recruitingStatus,
                startDate = project.startDate,
                endDate = project.endDate,
                createdAt = project.createdAt!!,
                modifiedAt = project.modifiedAt
            )
        }
    }
}

data class PaginationMeta(
    val total: Long,
    val limit: Int,
    val offset: Int
)
