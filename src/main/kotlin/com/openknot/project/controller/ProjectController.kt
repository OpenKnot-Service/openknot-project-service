package com.openknot.project.controller

import com.openknot.project.dto.ProjectSearchResponse
import com.openknot.project.service.ProjectService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID


@RestController
@RequestMapping("/projects")
class ProjectController(
    private val projectService: ProjectService,
) {

    @GetMapping
    suspend fun searchProjects(
        @RequestHeader("X-User-Id", required = false) userId: UUID?,
        @RequestParam(required = false) status: List<String>?,
        @RequestParam(required = false) recruitingStatus: List<String>?,
        @RequestParam(required = false) ownerId: UUID?,
        @RequestParam(defaultValue = "createdAt") sort: String,
        @RequestParam(defaultValue = "desc") order: String,
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int
    ): ResponseEntity<ProjectSearchResponse> {
        return ResponseEntity.ok(
            projectService.searchProjects(
                userId = userId,
                status = status,
                recruitingStatus = recruitingStatus,
                ownerId = ownerId,
                sort = sort,
                order = order,
                limit = limit,
                offset = offset
            )
        )
    }
}