package com.github.jbibro.etl.domain

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

        val selects = selects(query)
        val conditions = conditions(query)
        val groupBy = groupBy(query)

        return create
            .select(selects)
            .select(groupBy)
            .from("campaigns")
            .where(conditions)
            .groupBy(groupBy)
            .fetch()
            .formatJSON(DEFAULT_FOR_RECORDS.recordFormat(JSONFormat.RecordFormat.OBJECT))
    }

    private fun selects(query: Query) = with(query) {
        listOf(
            min.map { min(field(it)).`as`("min $it") },
            max.map { max(field(it)).`as`("max $it") },
            avg.map { avg(field(it, Double::class.java)).cast(Double::class.java).`as`("avg $it") },
            sum.map { sum(field(it, Double::class.java)).cast(Double::class.java).`as`("sum $it") }
        )
            .flatten()
    }

    private fun conditions(query: Query) = query.filters.map {
        when (it.operator()) {
            "eq" -> field(it.field()).eq(it.value())
            "le" -> field(it.field()).le(it.value())
            "ge" -> field(it.field()).ge(it.value())
            else -> trueCondition()
        }
    }

    private fun groupBy(query: Query) = query.groupBy.map { field(it) }
}

data class Query(
    val min: List<String> = emptyList(),
    val max: List<String> = emptyList(),
    val sum: List<String> = emptyList(),
    val avg: List<String> = emptyList(),

    val groupBy: List<String>,

    val filters: List<Filter>
)

@JvmInline
value class Filter(private val s: String) {

    init {
        require(s.split(".").size == 3)
    }

    private val parts: List<String>
        get() = s.split(".")


    fun field() = parts[0]
    fun operator() = parts[1]
    fun value() = parts[2]
}