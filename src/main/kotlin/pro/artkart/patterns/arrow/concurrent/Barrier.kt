package pro.artkart.patterns.arrow.concurrent

import arrow.fx.coroutines.parZip
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds


suspend fun fetchImageAsync() = coroutineScope {
    async {
        delay(200.milliseconds)
        "image"
    }
}

suspend fun fetchRelationAsync() = coroutineScope {
    async {
        delay(300.milliseconds)
        "relation"
    }
}

suspend fun fetchAudioAsync() = coroutineScope {
    async {
        delay(400.milliseconds)
        "audio"
    }
}

fun main() = runBlocking {

    val log = KotlinLogging.logger { }

    parZip( // also parMap, parMapUnordered, parMarOrAccumulate etc.
        { fetchImageAsync().await() },
        { fetchRelationAsync().await() },
        { fetchAudioAsync().await() }
    ) { image, relation, audio ->
        log.info { "image: $image, relation: $relation, audio: $audio" }
    }
}
