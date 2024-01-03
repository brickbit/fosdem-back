package com.snap.fosdem

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class FosdemApplication

fun main(args: Array<String>) {
	runApplication<FosdemApplication>(*args)
}
