package com.bastman.ktry


/*
    see: https://github.com/scala/scala/blob/v2.9.3/src/library/scala/util/Try.scala#L1
 */

sealed class Try<out T>(val isFailure: Boolean, val isSuccess: Boolean)
data class Success<out T>(val value: T) : Try<T>(isFailure = false, isSuccess = true)
data class Failure<out T>(val exception: Throwable) : Try<T>(isFailure = true, isSuccess = false)


fun <T> Try(block: () -> T): Try<T> = try {
    val value = block()

    Success(value)
} catch (exception: Throwable) {

    Failure(exception)
}


fun <T> Try<T>.getOrThrow(): T = when (this) {
    is Success -> value
    is Failure -> throw exception
}


fun <T> Try<T>.getOrNull(): T? = when (this) {
    is Success -> value
    is Failure -> null
}


fun <T> Try<T>.getOrDefault(default: T): T = when (this) {
    is Success -> value
    is Failure -> default
}


inline fun <T> Try<T>.getOrElse(block: (Throwable) -> T): T = when (this) {
    is Success -> value
    is Failure -> block(exception)
}


fun <T> Try<T>.orElse(default: Try<T>): Try<T> = when (this) {
    is Success -> this
    else -> default
}

inline fun <T> Try<T>.failure(block: (Throwable) -> Unit): Try<T> = when (this) {
    is Failure -> {
        block(exception)

        this
    }
    else -> this
}


inline fun <T> Try<T>.success(block: (T) -> Unit): Try<T> = when (this) {
    is Success -> {
        block(value)

        this
    }
    else -> this
}


inline fun <T> Try<T>.then(block: (Try<T>) -> Unit): Try<T> {
    block(this)

    return this
}

inline fun <T> Try<T>.map(block: (T) -> T): Try<T> = when (this) {
    is Success -> copy(block(value))
    else -> this
}

inline fun <T> Try<T>.flatMap(block: (T) -> Try<T>): Try<T> = when (this) {
    is Success -> block(value)
    else -> this
}

inline fun <T> Try<T>.mapException(block: (Throwable) -> Throwable): Try<T> = when (this) {
    is Failure -> copy(block(exception))
    else -> this
}

inline fun <T> Try<T>.flatMapException(block: (Throwable) -> Try<T>): Try<T> = when (this) {
    is Failure -> block(exception)
    else -> this
}

inline fun <T> Try<T>.recoverWith(block: (Throwable) -> Try<T>): Try<T> = flatMapException(block)

inline fun <T> Try<T>.recover(block: (Throwable) -> T): Try<T> = when (this) {
    is Failure -> Success(block(exception))
    else -> this
}

inline fun <T, R> Try<T>.to(block: (Try<T>) -> Try<R>): Try<R> {
    return block(this)
}

fun <T> Try<T>.copyTo(): Try<T> {
    return when (this) {
        is Success -> copy()
        is Failure -> copy()
    }
}