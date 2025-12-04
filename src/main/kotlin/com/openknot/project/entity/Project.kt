package com.openknot.project.entity

import com.openknot.project.exception.BusinessException
import com.openknot.project.exception.ErrorCode
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Table
class Project(
    @Id
    private val id: UUID,

    // 소유자 User ID
    var ownerId: UUID,
    // 프로젝트 명
    var name: String,
    // 프로젝트 설명
    var description: String? = null,
    // 공개 여부 (true = 공개, false = 비공개)
    var visibility: Boolean = true,
    // 프로젝트 상태
    var status: ProjectStatus,
    // 모집 여부
    var recruitingStatus: RecruitingStatus,
    // 프로젝트 시작일
    var startDate: LocalDate? = null,
    // 프로젝트 마감일
    var endDate: LocalDate? = null,

     @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var modifiedAt: LocalDateTime? = null,
    var deletedAt: LocalDateTime? = null,

) : Persistable<UUID> {
    fun update(
        name: String? = null,
        description: String? = null,
        visibility: Boolean? = null,
        status: String? = null,
        recruitingStatus: String? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ) {
        name?.let { this.name = it }
        description?.let { this.description = it }
        visibility?.let { this.visibility = it }
        status?.let {
            this.status = ProjectStatus.fromLabel(it.uppercase())
                ?: throw BusinessException(ErrorCode.VALIDATION_FAIL)
        }
        recruitingStatus?.let {
            this.recruitingStatus = RecruitingStatus.fromLabel(it.uppercase())
                ?: throw BusinessException(ErrorCode.VALIDATION_FAIL)
        }
        startDate?.let { this.startDate = it }
        endDate?.let { this.endDate = it}
        this.modifiedAt = LocalDateTime.now()
    }

    override fun getId(): UUID = this.id
    override fun isNew(): Boolean = createdAt == null
    override fun hashCode(): Int = id.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Project
        return id == other.id
    }
}