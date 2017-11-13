package com.soywiz.dynarek

data class State(
	@JvmField var a: Int = 0,
	@JvmField var b: Int = 0
) {
	val logList = arrayListOf<Int>()

	@JsName("mulAB")
	fun mulAB() {
		a *= b
	}

	@JsName("mulABArg")
	fun mulABArg(v: Int) {
		a *= b * v
	}

	@JsName("mulABArg2")
	fun mulABArg2(p0: Int, p1: Int) {
		a *= b * (p0 + p1)
	}

	@JsName("log")
	fun log(value: Int) {
		logList += value
	}
}
