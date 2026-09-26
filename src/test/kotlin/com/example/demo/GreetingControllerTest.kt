package com.example.demo

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class GreetingControllerTest(@Autowired private val mockMvc: MockMvc) {

	@Test
	fun `returns greeting for given name`() {
		mockMvc.get("/greeting") { param("name", "Alex") }
			.andExpect {
				status { isOk() }
				jsonPath("$.message") { value("Hello, Alex!") }
			}
	}

	@Test
	fun `returns default greeting without name`() {
		mockMvc.get("/greeting")
			.andExpect {
				status { isOk() }
				jsonPath("$.message") { value("Hello, World!") }
			}
	}
}
