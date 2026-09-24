package pro.artkart.notelearner

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import java.lang.System.currentTimeMillis
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

val log = KotlinLogging.logger { }

val timeout = 3.seconds
const val countDown = 5
val strings = (1..6).toList()
val notes = ('A'..'G').toList()

val noteFlow = flow {
    while (true) {
        val randomString = Random(currentTimeMillis()).nextInt(6)
        val randomNote = Random(currentTimeMillis()).nextInt(7)
        emit("${strings[randomString]} - ${notes[randomNote]}")
        repeat(countDown) {
            log.info { countDown - it }
            delay(timeout.div(countDown))
        }
    }

}

fun main() {

    runBlocking {
        noteFlow.collect {
            log.info { it }
        }
    }
}
