package pro.artkart.patterns.arrow.concurrent

import arrow.fx.coroutines.ResourceScope
import arrow.fx.coroutines.resource.context.install
import arrow.fx.coroutines.resourceScope
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking

object Resource {

    val log = KotlinLogging.logger { }

    context(resourceScope: ResourceScope)
    suspend fun openWardrobe(): Wardrobe =
        install({ Wardrobe().also { log.info { "Resource opened" } } }) { wardrobe, _ ->
            log.info { "Resource closed" }
            wardrobe.close()
        }

    context(resourceScope: ResourceScope)
    suspend fun getItem(item: String): String = openWardrobe().getItem(item)

    class Wardrobe : AutoCloseable {

        fun getItem(item: String) = "$item"

        override fun close() {
            log.info { "Wardrobe closed" }
        }
    }

    fun test() = runBlocking {

        log.info { "Before resource opened" }
        resourceScope {
            openWardrobe().getItem("Test").let {
                log.info { it }
            }
        }
        log.info { "After resource closed" }
    }
}
