package pro.artkart.kmagic.actor

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.sync.Semaphore
import pro.artkart.kmagic.common.List
import pro.artkart.kmagic.common.Result
import pro.artkart.kmagic.common.range
import java.lang.System.currentTimeMillis
import kotlin.random.Random


val log = KotlinLogging.logger { }
private val semaphore = Semaphore(1)
private const val listLength = 20_000
private const val workers = 8
private val rnd = Random(8)
private val testList = range(0, listLength).map { rnd.nextInt(35) }

suspend fun main() {

    semaphore.acquire()
    val startTime = currentTimeMillis()
    val client = object : AbstractActor<Result<List<Int>>>("Client") {

        override fun onReceive(message: Result<List<Int>>, sender: Result<Actor<Result<List<Int>>>>) {
            message.forEach(
                onSuccess = { processSuccess(it) },
                onFailure = { processFailure(it.message ?: "Unknown error") }
            )
            log.info { "Total time: ${(currentTimeMillis() - startTime)}" }
            semaphore.release()
        }
    }
    val manager = Manager("Manager", testList, client, workers)
    manager.start()
    semaphore.acquire()
}

private fun processSuccess(lst: List<Int>) {
    log.info { "Input: ${testList.splitAt(40).first}" }
    log.info { "Result: ${lst.splitAt(40).first}" }
}

private fun processFailure(message: String) {
    log.error { message }
}
