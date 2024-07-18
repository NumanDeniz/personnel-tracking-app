package com.example.reployetracking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("com.example.reployetracking")
class ReployetrackingApplication

fun main(args: Array<String>) {
	runApplication<ReployetrackingApplication>(*args)
}
