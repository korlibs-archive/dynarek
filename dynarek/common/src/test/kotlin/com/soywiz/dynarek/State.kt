package com.soywiz.dynarek

data class State(@JvmField var a: Int = 0, @JvmField var b: Int = 0) {
	@JsName("mulAB")
	fun mulAB() {
		a *= b
	}

	@JsName("mulABArg")
	fun mulABArg(v: Int) {
		a *= b * v
	}
}
