package com.openknot.project.entity

enum class ProjectStatus(
    val label: String
) {
    PLANNING("계획중"),
    IN_PROGRESS("진행중"),
    COMPLETED("완료"),
    ARCHIVED("보관됨");

    companion object {
        fun fromLabel(label: String): ProjectStatus? =
            ProjectStatus.entries.firstOrNull { it.name == label || it.label == label }
    }
}