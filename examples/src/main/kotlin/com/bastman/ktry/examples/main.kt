package com.bastman.ktry.examples

import com.bastman.ktry.*
import java.util.*

fun main(args: Array<String>) {
    val n: Try<Double?> = Try {
        throw RuntimeException("foo")
    }.failure {
        println("FAILURE ${it.message}")
    }.then {
        println("FINALLY")
    }
    println("n: $n")

    val d1: Double? = Try {
        200.0
    }.getOrThrow()
    println("d1: $d1")

    val d2: Double? = Try {
        200.0
    }.getOrElse { null }
    println("d2: $d2")


    for (i in 0..100) {
        val t: Try<Double?> = Try {
            trySth()
        }.failure {
            println("FAILURE ${it.message}")
        }.success {
            println("SUCCESS: ${it}")
        }.map {
            it * 10
        }.mapException {
            it
        }.recover {
            0.0
        }.recoverWith {
            Success(-100.0)
        }.then { println("FINALLY") }
                .to { it }

        println("--- $i: ${t.getOrNull()}")

    }
}

fun trySth(): Double {
    val rand = Random().nextInt(100)
    if (rand < 50) {
        return rand.toDouble()
    } else {
        throw RuntimeException("FAILED! $rand")
    }
}