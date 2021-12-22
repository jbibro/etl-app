package com.github.jbibro.etl.api

import com.github.jbibro.etl.domain.EtlService
import com.github.jbibro.etl.domain.Filter
import com.github.jbibro.etl.domain.Query
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/analyze")
class EtlEndpoint(private val etlService: EtlService) {

    @GetMapping("/", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun analyze(request: Request): ResponseEntity<String> {

        val query = request.let {
            Query(
                min = it.min,
                max = it.max,
                avg = it.avg,
                sum = it.sum,
                groupBy = it.groupBy,
                filters = it.filters.map { s ->
                    val (field, operator, value) = s.split("")
                    return@map Filter(field ,operator, value)
                }
            )
        }

        return ResponseEntity.ok(etlService.execute(query))
    }
}

data class Request(
    // metrics
    val min: List<String> = emptyList(),
    val max: List<String> = emptyList(),
    val sum: List<String> = emptyList(),
    val avg: List<String> = emptyList(),

    // dimensions
    val groupBy: List<String> = emptyList(),

    // filters
    val filters: List<String> = emptyList()
)
