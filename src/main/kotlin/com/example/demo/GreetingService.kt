package com.example.demo

import org.springframework.stereotype.Service

@Service
class GreetingService {

	fun greet(name: String?): String {
		val target = name?.trim().takeUnless { it.isNullOrEmpty() } ?: "World"
		return "Hello, $target!"
	}
}
