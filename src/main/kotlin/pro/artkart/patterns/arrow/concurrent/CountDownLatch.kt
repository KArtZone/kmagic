package pro.artkart.patterns.arrow.concurrent

import arrow.fx.coroutines.CountDownLatch
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random.Default.nextInt
import kotlin.time.Duration.Companion.milliseconds

object CountDownLatch {
    val log = KotlinLogging.logger { }

    suspend fun worker(id: Long, latch: CountDownLatch) {
        log.info { "Worker #$id started" }
        delay((nextInt(1000) + 200).milliseconds)
        log.info { "Worker #$id finished" }
        latch.countDown()
    }

    fun test() = runBlocking {

        val n = 5L
        val countDownLatch = CountDownLatch(n)

        (1..n).forEach {
            launch {
                worker(it, countDownLatch)
            }
        }

        countDownLatch.await()
        log.info { "All workers finished" }
    }
}
