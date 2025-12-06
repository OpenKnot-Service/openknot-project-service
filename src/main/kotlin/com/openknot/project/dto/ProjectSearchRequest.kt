package com.openknot.project.dto

import com.openknot.project.entity.ProjectStatus
import com.openknot.project.entity.RecruitingStatus
import java.util.UUID

data class ProjectSearchRequest(
    val status: List<ProjectStatus>? = null,
    val recruitingStatus: List<RecruitingStatus>? = null,
    val ownerId: UUID? = null,
    val sort: SortField = SortField.CREATED_AT,
    val order: SortOrder = SortOrder.DESC,
    val limit: Int = 20,
    val offset: Int = 0
) {
    enum class SortField(val columnName: String) {
        CREATED_AT("created_at"),
        MODIFIED_AT("modified_at"),
        NAME("name"),
        START_DATE("start_date"),
        END_DATE("end_date")
    }

    enum class SortOrder {
        ASC, DESC
    }

    init {
        require(limit in 1..100) { "limit은 1에서 100 사이여야 합니다" }
        require(offset >= 0) { "offset은 0 이상이어야 합니다" }
    }
}
