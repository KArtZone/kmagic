package pro.artkart.patterns.arrow.error

import io.github.oshai.kotlinlogging.KotlinLogging
import pro.artkart.patterns.arrow.error.model.Donut

class DefaultDonutBox(capacity: Int) : DonutBox(capacity) {

    operator fun plus(donut: Donut): DefaultDonutBox = apply {
        when {
            donuts.size < capacity -> donuts += donut
            else -> throw NoSpaceInBoxException()
        }
    }

    operator fun minus(name: String): Donut? = donuts
        .find { it.name == name }
        ?.also { donuts.remove(it) }
}

class NoSpaceInBoxException : RuntimeException("No space in box")

fun main() {

    val log = KotlinLogging.logger { }

    val box = DefaultDonutBox(1)
    box + Donut("One", 1, emptySet())

    log.info { box }

    box - "One"

    log.info { box }
}
