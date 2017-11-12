package com.soywiz.dynarek

import kotlin.reflect.KClass
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3
import kotlin.reflect.KMutableProperty1

class StmBuilder<TRet : Any, T0 : Any, T1 : Any>(val ret: KClass<TRet>, val t0: KClass<T0>, val t1: KClass<T1>) {
	inner class ElseBuilder(val ifElse: DIfElse) {
		infix fun ELSE(block: StmBuilder<TRet, T0, T1>.() -> Unit) {
			val b = createBuilder()
			block(b)
			ifElse.sfalse = b.build()
		}
	}

	val stms = ArrayList<DStm>()

	fun createBuilder() = StmBuilder<TRet, T0, T1>(ret, t0, t1)

	val <T> T.lit: DLiteral<T> get() = DLiteral(this)

	val p0 get() = DArg<T0>(t0, 0)
	val p1 get() = DArg<T1>(t1, 1)

	fun <T : Any> arg(clazz: KClass<T>, index: Int) = DArg<T>(clazz, index)

	operator fun DExpr<Int>.plus(that: DExpr<Int>) = DBinopInt(this, "+", that)
	operator fun DExpr<Int>.minus(that: DExpr<Int>) = DBinopInt(this, "-", that)
	operator fun DExpr<Int>.times(that: DExpr<Int>) = DBinopInt(this, "*", that)
	operator fun DExpr<Int>.div(that: DExpr<Int>) = DBinopInt(this, "/", that)
	operator fun DExpr<Int>.rem(that: DExpr<Int>) = DBinopInt(this, "%", that)

	infix fun DExpr<Int>.eq(that: DExpr<Int>) = DBinopIntBool(this, "==", that)
	infix fun DExpr<Int>.ne(that: DExpr<Int>) = DBinopIntBool(this, "!=", that)
	infix fun DExpr<Int>.ge(that: DExpr<Int>) = DBinopIntBool(this, ">=", that)
	infix fun DExpr<Int>.le(that: DExpr<Int>) = DBinopIntBool(this, "<=", that)
	infix fun DExpr<Int>.gt(that: DExpr<Int>) = DBinopIntBool(this, ">", that)
	infix fun DExpr<Int>.lt(that: DExpr<Int>) = DBinopIntBool(this, "<", that)

	inline operator fun <reified T : Any, TR> DExpr<T>.get(prop: KMutableProperty1<T, TR>): DFieldAccess<T, TR> = DFieldAccess(T::class, this, prop)

	//inline operator fun <reified T : Any, TR> DExpr<T>.get(func: KFunction1<T, TR>): DInstanceMethod1<T, TR> = DInstanceMethod1(T::class, this, func)

	fun RET(expr: DExpr<TRet>) = stms.add(DReturnExpr(expr))
	fun RET() = stms.add(DReturnVoid(true))
	fun <T> SET(ref: DRef<T>, value: DExpr<T>) = stms.add(DAssign(ref, value))

	fun <T> STM(expr: DExpr<T>) = stms.add(DStmExpr(expr))

	inline operator fun <reified TThis : Any, TR : Any> KFunction1<TThis, TR>.invoke(p0: DExpr<TThis>): DExpr<TR> = DExprInvoke1<TThis, TR>(TThis::class, this, p0)
	inline operator fun <reified TThis : Any, T1 : Any, TR : Any> KFunction2<TThis, T1, TR>.invoke(p0: DExpr<TThis>, p1: DExpr<T1>): DExpr<TR> = DExprInvoke2<TThis, T1, TR>(TThis::class, this, p0, p1)
	inline operator fun <reified TThis : Any, T1 : Any, T2 : Any, TR : Any> KFunction3<TThis, T1, T2, TR>.invoke(p0: DExpr<TThis>, p1: DExpr<T1>, p2: DExpr<T2>): DExpr<TR> = DExprInvoke3<TThis, T1, T2, TR>(TThis::class, this, p0, p1, p2)

	fun (StmBuilder<TRet, T0, T1>.() -> Unit).build(): DStm {
		val builder = createBuilder()
		this(builder)
		return builder.build()
	}

	fun IF(cond: Boolean, block: StmBuilder<TRet, T0, T1>.() -> Unit): ElseBuilder = IF(cond.lit, block)

	fun IF(cond: DExpr<Boolean>, block: StmBuilder<TRet, T0, T1>.() -> Unit): ElseBuilder {
		val trueBuilder = createBuilder()
		block(trueBuilder)
		val ifElse = DIfElse(cond, trueBuilder.build())
		stms.add(ifElse)
		return ElseBuilder(ifElse)
	}

	fun WHILE(cond: DExpr<Boolean>, block: StmBuilder<TRet, T0, T1>.() -> Unit): Unit {
		stms.add(DWhile(cond, block.build()))
	}

	fun build(): DStm = DStms(stms.toList())
}

//typealias StmBuilderBlock<TRet, T0, T1> = StmBuilder<TRet, T0, T1>.() -> Unit
