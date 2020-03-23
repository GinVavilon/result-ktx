package com.github.ginvavilon.utils

import kotlin.reflect.KClass

sealed class Result<V, E> {

    @JvmName("onCatch")
    inline infix fun catch(call: (error: E) -> V): V {
        return when (this) {
            is Ok -> this.value
            is Error -> call(this.error)
        }
    }

    inline fun <NV> map(mapper: (V) -> NV): Result<NV, E> {
        return when (this) {
            is Ok -> Ok(mapper(this.value))
            is Error -> Error(this.error)
        }
    }

    inline fun <NV, NE> flatMap(mapper: (V) -> Result<NV, out NE>): Result<NV, NE> {

        return when (this) {
            is Ok -> {
                val res = mapper(this.value)
                when (res) {
                    is Ok -> Ok(res.value)
                    is Error -> Error(res.error)
                }
            }
            is Error -> Error(this.error as NE)
        }
    }

    inline fun <NV, NE : Throwable> tryMap(catch: KClass<out NE>, vararg catches: KClass<out NE> = arrayOf(), mapper: (V) -> NV): Result<NV, NE> {
        return flatMap {
            return try {
                Ok(mapper(it))
            } catch (e: Throwable) {
                if (catch.isInstance(e) || (catches.any { it.isInstance(e) })) {
                    Error(e as NE);
                } else {
                    throw e;
                }
            }
        }
    }

    inline fun <NV, NE : Throwable> safetyMap(mapper: (V) -> NV): Result<NV, NE> {
        return flatMap {
            return try {
                Ok(mapper(it))
            } catch (e: Throwable) {
                Error(e as NE);
            }
        }
    }


    fun revers(): Result<E, V> {
        return when (this) {
            is Ok -> Error(this.value)
            is Error -> Ok(this.error)
        }
    }

    data class Ok<V, E>(val value: V) : Result<V, E>()

    data class Error<V, E>(val error: E) : Result<V, E>()

    companion object {

        @JvmStatic
        fun <V, E> of(value: V): Ok<V, E> {
            return Ok(value)
        }

        @JvmStatic
        fun <V, E> error(error: E): Error<V, E> {
            return Error(error)
        }

        @JvmStatic
        fun <V, E : Throwable> execute(function: ResultFunction<V, E>): Result<V, E> {
            try {
                val value = function.invoke()
                return Result.of(value)
            } catch (e: Throwable) {
                return Result.error(e as E)
            }
        }

        inline fun <V, reified E : Throwable> tryCatch(vararg catch: KClass<out E> = arrayOf(E::class), body: () -> V): Result<V, E> {
            try {
                return Result.of(body());
            } catch (e: Throwable) {
                if (catch.any { it.isInstance(e) }) {
                    return Result.error(e as E)
                } else {
                    throw e;
                }
            }
        }

    }

}

/**
 * Convert to [Result.Ok]
 */
fun <V, E> V.toResult(): Result<V, E> = Result.of(this)

/**
 * Convert to [Result.Error]
 */
fun <V, E : Throwable> E.toResult(): Result<V, E> = Result.error(this)

inline fun <V, reified E : Throwable, NV> Result<V, E>.tryMap(mapper: (V) -> NV): Result<NV, E> {
    return this.flatMap {
        Result.tryCatch(E::class) { mapper(it) }
    }
}

inline fun <V, reified E : Throwable> tryCatch(vararg catch: KClass<out E> = arrayOf(E::class), body: () -> V): Result<V, E> = Result.tryCatch(catch = *catch, body = body)

inline fun <V, reified E : Throwable> try_(vararg catch: KClass<out E> = arrayOf(E::class), body: () -> V): Result<V, E> = Result.tryCatch(catch = *catch, body = body)