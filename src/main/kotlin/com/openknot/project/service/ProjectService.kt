package com.openknot.project.service

import com.openknot.project.dto.PaginationMeta
import com.openknot.project.dto.ProjectDto
import com.openknot.project.dto.ProjectSearchRequest
import com.openknot.project.dto.ProjectSearchResponse
import com.openknot.project.entity.ProjectStatus
import com.openknot.project.entity.RecruitingStatus
import com.openknot.project.exception.BusinessException
import com.openknot.project.exception.ErrorCode
import com.openknot.project.repository.ProjectMemberRepository
import com.openknot.project.repository.ProjectRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val projectMemberRepository: ProjectMemberRepository,
) {

    suspend fun searchProjects(
        userId: UUID?,
        status: List<String>?,
        recruitingStatus: List<String>?,
        ownerId: UUID?,
        sort: String,
        order: String,
        limit: Int,
        offset: Int
    ): ProjectSearchResponse {
        val request = buildSearchRequest(
            status = status,
            recruitingStatus = recruitingStatus,
            ownerId = ownerId,
            sort = sort,
            order = order,
            limit = limit,
            offset = offset
        )

        return searchProjects(request, userId)
    }

    private fun buildSearchRequest(
        status: List<String>?,
        recruitingStatus: List<String>?,
        ownerId: UUID?,
        sort: String,
        order: String,
        limit: Int,
        offset: Int
    ): ProjectSearchRequest {
        val parsedStatus = parseProjectStatus(status)
        val parsedRecruitingStatus = parseRecruitingStatus(recruitingStatus)
        val sortField = parseSortField(sort)
        val sortOrder = parseSortOrder(order)

        return validateAndCreateRequest(
            status = parsedStatus,
            recruitingStatus = parsedRecruitingStatus,
            ownerId = ownerId,
            sortField = sortField,
            sortOrder = sortOrder,
            limit = limit,
            offset = offset
        )
    }

    private fun parseProjectStatus(status: List<String>?): List<ProjectStatus>? {
        return status?.map {
            ProjectStatus.fromLabel(it.uppercase())
                ?: throw BusinessException(ErrorCode.VALIDATION_FAIL)
        }
    }

    private fun parseRecruitingStatus(recruitingStatus: List<String>?): List<RecruitingStatus>? {
        return recruitingStatus?.map {
            RecruitingStatus.fromLabel(it.uppercase())
                ?: throw BusinessException(ErrorCode.VALIDATION_FAIL)
        }
    }

    private fun parseSortField(sort: String): ProjectSearchRequest.SortField {
        return try {
            ProjectSearchRequest.SortField.valueOf(sort.uppercase().replace("-", "_"))
        } catch (e: IllegalArgumentException) {
            throw BusinessException(ErrorCode.VALIDATION_FAIL)
        }
    }

    private fun parseSortOrder(order: String): ProjectSearchRequest.SortOrder {
        return try {
            ProjectSearchRequest.SortOrder.valueOf(order.uppercase())
        } catch (e: IllegalArgumentException) {
            throw BusinessException(ErrorCode.VALIDATION_FAIL)
        }
    }

    private fun validateAndCreateRequest(
        status: List<ProjectStatus>?,
        recruitingStatus: List<RecruitingStatus>?,
        ownerId: UUID?,
        sortField: ProjectSearchRequest.SortField,
        sortOrder: ProjectSearchRequest.SortOrder,
        limit: Int,
        offset: Int
    ): ProjectSearchRequest {
        return try {
            ProjectSearchRequest(
                status = status,
                recruitingStatus = recruitingStatus,
                ownerId = ownerId,
                sort = sortField,
                order = sortOrder,
                limit = limit,
                offset = offset
            )
        } catch (e: IllegalArgumentException) {
            throw BusinessException(ErrorCode.VALIDATION_FAIL)
        }
    }

    private suspend fun searchProjects(
        request: ProjectSearchRequest,
        userId: UUID?
    ): ProjectSearchResponse {

        // 1단계: 인증된 사용자의 접근 가능한 프로젝트 ID 결정
        val visibleProjectIds = if (userId != null) {
            // 사용자가 멤버인 프로젝트 조회
            projectMemberRepository.findProjectIdsByUserId(userId)
        } else {
            emptyList()
        }

        // 2단계: 검색 쿼리 실행
        val projects = projectRepository.searchProjects(
            userId = userId,
            visibleProjectIds = visibleProjectIds,
            visibleProjectIdsCount = visibleProjectIds.size,
            statuses = request.status?.map { it.name },
            statusCount = request.status?.size ?: 0,
            recruitingStatuses = request.recruitingStatus?.map { it.name },
            recruitingStatusCount = request.recruitingStatus?.size ?: 0,
            ownerId = request.ownerId,
            sortField = request.sort.columnName,
            sortOrder = request.order.name,
            limit = request.limit,
            offset = request.offset
        )

        // 3단계: 전체 개수 조회
        val total = projectRepository.countProjects(
            userId = userId,
            visibleProjectIds = visibleProjectIds,
            visibleProjectIdsCount = visibleProjectIds.size,
            statuses = request.status?.map { it.name },
            statusCount = request.status?.size ?: 0,
            recruitingStatuses = request.recruitingStatus?.map { it.name },
            recruitingStatusCount = request.recruitingStatus?.size ?: 0,
            ownerId = request.ownerId
        )

        // 4단계: DTO로 변환
        val projectDtos = projects.map { ProjectDto.from(it) }.toList()

        // 5단계: 응답 생성
        return ProjectSearchResponse(
            items = projectDtos,
            pagination = PaginationMeta(
                total = total,
                limit = request.limit,
                offset = request.offset
            )
        )
    }
}