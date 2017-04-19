package com.bastman.ktry


/*
    see: https://github.com/scala/scala/blob/v2.9.3/src/library/scala/util/Try.scala#L1
 */

sealed class Try<out T>(val isFailure: Boolean, val isSuccess: Boolean)
data class Success<out T>(val value: T) : Try<T>(isFailure = false, isSuccess = true)
data class Failure<out T>(val exception: Throwable) : Try<T>(isFailure = true, isSuccess = false)


fun <T> Try(f: () -> T): Try<T> = try {
    val value = f()

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


inline fun <T> Try<T>.getOrElse(f: (Throwable) -> T): T = when (this) {
    is Success -> value
    is Failure -> f(exception)
}


fun <T> Try<T>.orElse(default: Try<T>): Try<T> = when (this) {
    is Success -> this
    else -> default
}

inline fun <T> Try<T>.failure(f: (Throwable) -> Unit): Try<T> = when (this) {
    is Failure -> {
        f(exception)

        this
    }
    else -> this
}


inline fun <T> Try<T>.success(f: (T) -> Unit): Try<T> = when (this) {
    is Success -> {
        f(value)

        this
    }
    else -> this
}


inline fun <T> Try<T>.then(f: (Try<T>) -> Unit): Try<T> {
    f(this)

    return this
}

inline fun <T> Try<T>.map(f: (T) -> T): Try<T> = when (this) {
    is Success -> copy(f(value))
    else -> this
}

inline fun <T> Try<T>.flatMap(f: (T) -> Try<T>): Try<T> = when (this) {
    is Success -> f(value)
    else -> this
}

inline fun <T> Try<T>.mapException(f: (Throwable) -> Throwable): Try<T> = when (this) {
    is Failure -> copy(f(exception))
    else -> this
}

inline fun <T> Try<T>.flatMapException(f: (Throwable) -> Try<T>): Try<T> = when (this) {
    is Failure -> f(exception)
    else -> this
}

inline fun <T> Try<T>.recoverWith(f: (Throwable) -> Try<T>): Try<T> = flatMapException(f)

inline fun <T> Try<T>.recover(f: (Throwable) -> T): Try<T> = when (this) {
    is Failure -> Success(f(exception))
    else -> this
}

inline fun <T, R> Try<T>.to(f: (Try<T>) -> Try<R>): Try<R> {
    return f(this)
}

fun <T> Try<T>.copyTo(): Try<T> {
    return when (this) {
        is Success -> copy()
        is Failure -> copy()
    }
}


inline fun <T, R> Try<T>.transform(s: (T) -> Try<R>, f: (Throwable) -> Try<R>): Try<R> = when (this) {
    is Success -> s(value)
    is Failure -> f(exception)
}


inline fun <T, R> Try<T>.onEach(f: (T) -> R): Unit {
    when (this) {
        is Success -> f(value)
    }
}