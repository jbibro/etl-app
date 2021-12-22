package com.github.jbibro.etl.api

import com.github.jbibro.etl.domain.Aggregation.AVG
import com.github.jbibro.etl.domain.Aggregation.MAX
import com.github.jbibro.etl.domain.Aggregation.MIN
import com.github.jbibro.etl.domain.Aggregation.SUM
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
                aggregations = mapOf(
                    MIN to it.min,
                    MAX to it.max,
                    SUM to it.sum,
                    AVG to it.avg,
                ),
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
