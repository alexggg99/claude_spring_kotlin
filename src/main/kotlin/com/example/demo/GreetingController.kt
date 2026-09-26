package com.example.demo

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

data class Greeting(val message: String)

@RestController
class GreetingController(private val greetingService: GreetingService) {

	@GetMapping("/greeting")
	fun greeting(@RequestParam(required = false) name: String?): Greeting =
		Greeting(greetingService.greet(name))
}
