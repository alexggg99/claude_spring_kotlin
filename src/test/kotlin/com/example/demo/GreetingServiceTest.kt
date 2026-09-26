package com.example.demo

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GreetingServiceTest {

	private val service = GreetingService()

	@Test
	fun `greets by name`() {
		assertEquals("Hello, Alex!", service.greet("Alex"))
	}

	@Test
	fun `falls back to World when name is null or blank`() {
		assertEquals("Hello, World!", service.greet(null))
		assertEquals("Hello, World!", service.greet("   "))
	}
}
