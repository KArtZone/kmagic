package pro.artkart.patterns.arrow.concurrent.resilience

import arrow.resilience.CircuitBreaker
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object CircuitBreaker {

    val log = KotlinLogging.logger { }

    fun remoteServer(failureChance: Double) = sequence {
        while (true) {
            if (Random.nextDouble(1.0) < failureChance) {
                yield { throw RuntimeException() }
            } else {
                yield { "OK" }
            }
        }
    }

    suspend fun test() {
        CircuitBreaker(
            openingStrategy = CircuitBreaker.OpeningStrategy.Count(1),
            resetTimeout = 10.seconds,
            exponentialBackoffFactor = 2.0,
            maxResetTimeout = 60.seconds
        ).doOnOpen { log.info { "Opened" } }
            .doOnHalfOpen { log.info { "Half-Opened" } }
            .doOnClosed { log.info { "Closed" } }
            .let { circuitBreaker ->
                remoteServer(.3).forEach { request ->
                    try {
                        delay(400.milliseconds)
                        circuitBreaker.protectOrThrow { request() }
                            .also {
                                log.info { it }
                            }
                    } catch (e: RuntimeException) {
                        log.error { "Server returned exception: $e" }
                    } catch (e: CircuitBreaker.ExecutionRejected) {
                        log.error { "Circuit breaker exception: $e" }
                    }
                }
            }
    }
}
