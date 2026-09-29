package pro.artkart.patterns.arrow.concurrent.resilience

import arrow.resilience.saga
import arrow.resilience.transact
import io.github.oshai.kotlinlogging.KotlinLogging

object Saga {

    val log = KotlinLogging.logger { }

    fun packDonuts() = log.info { "Packed donut in box" }
    fun unpackDonuts() = log.info { "Unpacked donut from box" }
    fun putLabel() = log.info { "Putted label on box" }
    fun removeLabel() = log.info { "Removed label from box" }
    fun deliver() = log.info { "Box has been delivered" }

    suspend fun test() {
        saga {
            saga({ packDonuts() }, { unpackDonuts() })
            saga({ putLabel() }, { removeLabel() })
            saga({ deliver() }, { log.error { "That was just a wasted time" } })
        }.transact()
    }
}
