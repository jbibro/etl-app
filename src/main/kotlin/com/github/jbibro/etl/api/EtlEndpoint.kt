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

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun analyze(request: Request): ResponseEntity<String> {

        val query = request.let { r ->
            Query(
                min = r.min,
                max = r.max,
                avg = r.avg,
                sum = r.sum,
                groupBy = r.groupBy,
                filters = r.filters.map { Filter(it) }
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
