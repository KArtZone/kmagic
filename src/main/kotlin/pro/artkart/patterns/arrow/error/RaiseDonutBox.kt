package pro.artkart.patterns.arrow.error

import arrow.core.raise.Raise
import arrow.core.raise.context.ensure
import arrow.core.raise.context.raise
import arrow.core.raise.fold
import io.github.oshai.kotlinlogging.KotlinLogging
import pro.artkart.patterns.arrow.error.model.Donut

class RaiseDonutBox(capacity: Int) : DonutBox(capacity) {

    context(raise: Raise<NoSpaceInBox>)
    operator fun plus(donut: Donut): RaiseDonutBox {
        ensure(donuts.size < capacity) { NoSpaceInBox }
        return apply { donuts += donut }
    }

    context(raise: Raise<NoSuchDonut>)
    operator fun minus(name: String): Donut =
        donuts.find { it.name == name }
            ?.also { donuts.remove(it) }
            ?: raise(NoSuchDonut(name))
}

fun main() {

    val log = KotlinLogging.logger { }

    val box = RaiseDonutBox(1)

    fold(
        {
            box + Donut("One", 1, emptySet())
        },
        { log.error { "No space in box" } },
        {
            fold(
                {
                    box - "One"
                },
                { log.error { "No such donut" } },
                {
                    log.info { "I'v got donut: $it" }
                }
            )
        }
    )
}
