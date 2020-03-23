package com.github.ginvavilon.utils

import org.junit.Assert.*

import org.junit.Test

class KotlinResultTest {

	@Test
	fun test() {

		val res = KotlinExample.parseValue("12").catch {
			-1
		}

		assertEquals(12, res);
	}

}
