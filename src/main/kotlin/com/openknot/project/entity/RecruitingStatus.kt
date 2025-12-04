package com.openknot.project.entity

enum class RecruitingStatus(
    val label: String
) {
    OPEN("모집중"),
    CLOSED("모집마감"),
    PAUSED("일시중지");

    companion object {
        fun fromLabel(label: String): RecruitingStatus? =
            RecruitingStatus.entries.firstOrNull { it.name == label || it.label == label }
    }
}