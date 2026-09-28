package pro.artkart.patterns.arrow.error

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import io.github.oshai.kotlinlogging.KotlinLogging
import pro.artkart.patterns.arrow.error.model.Donut
import kotlin.random.Random

class EitherDonutBox(capacity: Int) : DonutBox(capacity) {

    operator fun plus(donut: Donut): Either<NoSpaceInBox, EitherDonutBox> =
        either {
            ensure(donuts.size < capacity) { NoSpaceInBox }
            this@EitherDonutBox.apply { donuts += donut }
        }

    operator fun minus(name: String): Either<NoSuchDonut, Donut> = either {
        donuts.find { it.name == name }
            ?.also { donuts.remove(it) }
            ?: raise(NoSuchDonut(name))
    }
}

object NoSpaceInBox

data class NoSuchDonut(val name: String)

fun main() {

    val log = KotlinLogging.logger { }

    either {
        Random.nextInt(10).let {
            ensure(it > 5) { RuntimeException("Fuck you") }
            42
        }
    }.onLeft { log.error { it.message } }
        .onRight { log.info { it } }
}
