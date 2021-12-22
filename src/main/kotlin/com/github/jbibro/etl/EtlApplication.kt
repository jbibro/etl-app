package com.github.jbibro.etl

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EtlApplication

fun main(args: Array<String>) {
	runApplication<EtlApplication>(*args)
}
