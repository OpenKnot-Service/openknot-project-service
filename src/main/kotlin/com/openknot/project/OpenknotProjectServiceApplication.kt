package com.openknot.project

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@EnableR2dbcRepositories
@EnableR2dbcAuditing
@SpringBootApplication
class OpenknotProjectServiceApplication

fun main(args: Array<String>) {
    runApplication<OpenknotProjectServiceApplication>(*args)
}
