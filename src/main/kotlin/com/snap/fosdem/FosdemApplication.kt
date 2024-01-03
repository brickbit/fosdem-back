package com.snap.fosdem

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FosdemApplication

fun main(args: Array<String>) {
	runApplication<FosdemApplication>(*args)
}
