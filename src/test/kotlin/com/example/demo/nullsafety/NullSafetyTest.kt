package com.example.demo.nullsafety

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

/**
 * Учебные тесты по теме Null safety.
 * Примеры взяты из документации: https://kotlinlang.org/docs/null-safety.html
 * Каждый вложенный класс соответствует разделу документации.
 */
@DisplayName("Kotlin Null safety")
class NullSafetyTest {

	data class Person(val name: String, var department: Department? = null)
	data class Department(var head: Person? = null)

	@Nested
	@DisplayName("1. Nullable и non-nullable типы")
	inner class NullableTypes {

		@Test
		fun `non-nullable type can be used directly`() {
			val a: String = "abc"
			// a = null  // не скомпилируется: Null can not be a value of a non-null type String
			assertEquals(3, a.length)
		}

		@Test
		fun `nullable type can hold null`() {
			var b: String? = "abc"
			b = null
			// b.length  // не скомпилируется: Only safe (?.) or non-null asserted (!!.) calls are allowed
			assertNull(b)
		}
	}

	@Nested
	@DisplayName("2. Проверка на null через if")
	inner class IfCheck {

		@Test
		fun `if expression returns fallback for null`() {
			val b: String? = null
			val l = if (b != null) b.length else -1
			assertEquals(-1, l)
		}

		@Test
		fun `smart cast after null check`() {
			val b: String? = "Kotlin"
			// после проверки b != null компилятор сам приводит b к String (smart cast)
			val result = if (b != null && b.length > 0) "String of length ${b.length}" else "Empty string"
			assertEquals("String of length 6", result)
		}
	}

	@Nested
	@DisplayName("3. Safe call ?.")
	inner class SafeCall {

		@Test
		fun `safe call returns value or null`() {
			val a: String? = "Kotlin"
			val b: String? = null
			assertEquals(6, a?.length)
			assertNull(b?.length)
		}

		@Test
		fun `safe call chain returns null if any link is null`() {
			val boss = Person("Alice")
			val bob: Person? = Person("Bob", Department(head = boss))
			val noDepartment: Person? = Person("Carol")
			val nobody: Person? = null

			assertEquals("Alice", bob?.department?.head?.name)
			assertNull(noDepartment?.department?.head?.name)
			assertNull(nobody?.department?.head?.name)
		}

		@Test
		fun `safe call assignment skips right side when receiver is null`() {
			var managerRequests = 0
			fun getManager(): Person {
				managerRequests++
				return Person("Manager")
			}

			val person: Person? = Person("Bob")  // department == null
			person?.department?.head = getManager()
			assertEquals(0, managerRequests, "правая часть не вычисляется, если в цепочке есть null")

			person?.department = Department()
			person?.department?.head = getManager()
			assertEquals(1, managerRequests)
			assertEquals("Manager", person?.department?.head?.name)
		}
	}

	@Nested
	@DisplayName("4. Elvis-оператор ?:")
	inner class Elvis {

		@Test
		fun `elvis provides default value`() {
			val b: String? = null
			assertEquals(0, b?.length ?: 0)
		}

		private fun departmentHeadName(person: Person?): String? {
			val department = person?.department ?: return null
			val head = department.head ?: throw IllegalArgumentException("head expected")
			return head.name
		}

		@Test
		fun `elvis with return`() {
			assertNull(departmentHeadName(Person("Bob")))
		}

		@Test
		fun `elvis with throw`() {
			val ex = assertFailsWith<IllegalArgumentException> {
				departmentHeadName(Person("Bob", Department()))
			}
			assertEquals("head expected", ex.message)
		}

		@Test
		fun `elvis passes non-null value through`() {
			assertEquals("Alice", departmentHeadName(Person("Bob", Department(Person("Alice")))))
		}
	}

	@Nested
	@DisplayName("5. Not-null assertion !!")
	inner class NotNullAssertion {

		@Test
		fun `not-null assertion returns value when not null`() {
			val b: String? = "Kotlin"
			assertEquals(6, b!!.length)
		}

		@Test
		fun `not-null assertion throws NPE on null`() {
			val b: String? = null
			assertFailsWith<NullPointerException> { b!!.length }
		}
	}

	@Nested
	@DisplayName("6. Nullable receiver")
	inner class NullableReceiver {

		@Test
		fun `toString is defined on nullable receiver`() {
			val person: Person? = null
			// Any?.toString() объявлен с nullable receiver, поэтому вызов без ?. безопасен
			assertEquals("null", person.toString())
		}

		@Test
		fun `safe call toString returns real null`() {
			val person1: Person? = null
			val person2: Person? = Person("Alice")
			assertNull(person1?.toString())
			assertEquals("Person(name=Alice, department=null)", person2?.toString())
		}

		private fun String?.orPlaceholder(): String = this ?: "<empty>"

		@Test
		fun `own extension with nullable receiver`() {
			val missing: String? = null
			assertEquals("<empty>", missing.orPlaceholder())
			assertEquals("Kotlin", "Kotlin".orPlaceholder())
		}
	}

	@Nested
	@DisplayName("7. Функция let")
	inner class Let {

		@Test
		fun `let runs only for non-null items`() {
			val listWithNulls: List<String?> = listOf("Kotlin", null)
			val printed = mutableListOf<String>()
			for (item in listWithNulls) {
				item?.let { printed.add(it) }
			}
			assertEquals(listOf("Kotlin"), printed)
		}

		@Test
		fun `let with elvis`() {
			val name: String? = null
			val greeting = name?.let { "Hello, $it" } ?: "Hello, stranger"
			assertEquals("Hello, stranger", greeting)
		}
	}

	@Nested
	@DisplayName("8. Safe cast as?")
	inner class SafeCast {

		@Test
		fun `safe cast returns null instead of exception`() {
			val a: Any = "Hello, Kotlin!"
			val aInt: Int? = a as? Int
			val aString: String? = a as? String
			assertNull(aInt)
			assertEquals("Hello, Kotlin!", aString)
		}

		@Test
		fun `unsafe cast throws ClassCastException`() {
			val a: Any = "Hello, Kotlin!"
			assertFailsWith<ClassCastException> { a as Int }
		}
	}

	@Nested
	@DisplayName("9. Коллекции nullable-типов")
	inner class NullableCollections {

		@Test
		fun `filterNotNull removes nulls and changes element type`() {
			val nullableList: List<Int?> = listOf(1, 2, null, 4)
			val intList: List<Int> = nullableList.filterNotNull()
			assertEquals(listOf(1, 2, 4), intList)
		}

		@Test
		fun `nullable list vs list of nullable elements`() {
			val nullableList: List<Int>? = null    // сам список может быть null
			val listOfNullable: List<Int?> = listOf(null) // элементы могут быть null
			assertEquals(0, nullableList?.size ?: 0)
			assertEquals(1, listOfNullable.size)
		}
	}
}
