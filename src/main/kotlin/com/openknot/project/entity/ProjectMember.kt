package com.openknot.project.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

@Table("project_member")
class ProjectMember(
    @Id
    private val id: UUID,

    val projectId: UUID,
    val userId: UUID,
    val positionId: UUID,

    @CreatedDate
    val createdAt: LocalDateTime? = null,
    var deletedAt: LocalDateTime? = null,
) : Persistable<UUID> {

    override fun getId(): UUID = this.id
    override fun isNew(): Boolean = createdAt == null

    override fun hashCode(): Int = id.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ProjectMember
        return id == other.id
    }
}
