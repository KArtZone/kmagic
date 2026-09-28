package pro.artkart.patterns.arrow.concurrent

import arrow.fx.coroutines.raceN
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds


suspend fun racer(name: String): String = name.also {
    val time = Random.nextInt(1000) + 200
    log.info { "$name: my time is $time" }
    delay(time.milliseconds)
}

fun main() = runBlocking {

    val log = KotlinLogging.logger { }

    val winner = raceN(
        { racer("George Russell") },
        { racer("Lewis Hamilton") },
        { racer("Lando Norris") }
    )

    log.info { winner }
}
