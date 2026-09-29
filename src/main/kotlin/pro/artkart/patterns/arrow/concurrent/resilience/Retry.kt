package pro.artkart.patterns.arrow.concurrent.resilience

import arrow.resilience.Schedule
import arrow.resilience.retry
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlin.time.Duration.Companion.seconds

object Retry {

    val log = KotlinLogging.logger { }

    fun serverResponses(): Flow<String> {
        var requests = 0
        var lastErrorTime = System.currentTimeMillis()
        return flow {
            if (requests++ < 3) {
                log.error { "Error occurred at ${System.currentTimeMillis() - lastErrorTime}" }
                lastErrorTime = System.currentTimeMillis()
                throw RuntimeException("Something went wrong")
            } else {
                log.info { "Server is up" }
                emit("OK")
            }
        }
    }

    suspend fun test() {

        serverResponses().retry(
            Schedule.recurs<Throwable>(10) // also repeat*, doUntil, doWhile, etc.
                .and(Schedule.exponential(1.seconds, 2.5))
        ).toList()
    }
}
