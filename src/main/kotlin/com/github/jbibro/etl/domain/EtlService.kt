package com.github.jbibro.etl.domain

import com.github.jbibro.etl.domain.Aggregation.AVG
import com.github.jbibro.etl.domain.Aggregation.MAX
import com.github.jbibro.etl.domain.Aggregation.MIN
import com.github.jbibro.etl.domain.Aggregation.SUM
import org.jooq.DSLContext
import org.jooq.JSONFormat
import org.jooq.JSONFormat.DEFAULT_FOR_RECORDS
import org.jooq.impl.DSL.avg
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.max
import org.jooq.impl.DSL.min
import org.jooq.impl.DSL.sum
import org.jooq.impl.DSL.trueCondition
import org.springframework.stereotype.Service

@Service
class EtlService(private val create: DSLContext) {

    fun execute(query: Query): String {

        val conditions = query.filters.map {
            when (it.operator) {
                "eq" -> field(it.field).eq(it.value)
                "le" -> field(it.field).le(it.value)
                "gt" -> field(it.field).gt(it.value)
                else -> trueCondition()
            }
        }

        return with(query) {
            create
                .select(aggregations[MIN]?.map { min(field(it)).`as`("min $it") })
                .select(aggregations[MAX]?.map { max(field(it)).`as`("max $it") })
                .select(aggregations[SUM]?.map {
                    sum(field(it, Double::class.java)).cast(Double::class.java).`as`("sum $it")
                })
                .select(aggregations[AVG]?.map {
                    avg(field(it, Double::class.java)).cast(Double::class.java).`as`("avg $it")
                })
                .select(groupBy.map { field(it) })
                .from("campaigns")
                .where(conditions)
                .groupBy(groupBy.map { field(it) })
                .fetch()
                .formatJSON(DEFAULT_FOR_RECORDS.recordFormat(JSONFormat.RecordFormat.OBJECT))
        }
    }
}

enum class Aggregation {
    MIN, MAX, SUM, AVG
}

data class Query(
    val aggregations: Map<Aggregation, List<String>>,
    val groupBy: List<String>,
    val filters: List<Filter>
)


data class Filter(
    val field: String,
    val operator: String,
    val value: String
)