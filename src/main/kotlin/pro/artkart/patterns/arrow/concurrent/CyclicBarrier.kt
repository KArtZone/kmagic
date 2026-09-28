package pro.artkart.patterns.arrow.concurrent

import arrow.fx.coroutines.CyclicBarrier
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds


suspend fun fetchArticleAsync(url: String) = coroutineScope {
    async {
        delay(400.milliseconds)
        url.substringAfter("letter/")
    }
}

fun main() = runBlocking {

    val log = KotlinLogging.logger { }

    val barrier = CyclicBarrier(3) { log.info { "Done" } }

    ('a'..'x').forEach {
        launch {
            fetchArticleAsync("https://wiki.com/letter/$it")
            log.info { "Fetched letter $it" }
            barrier.await()
        }
    }
}
