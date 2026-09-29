package pro.artkart.patterns.arrow.concurrent

import io.github.oshai.kotlinlogging.KotlinLogging

val log = KotlinLogging.logger { }


fun main() {

    Barrier.test()
    CountDownLatch.test()
    CyclicBarrier.test()
    Race.test()
    Resource.test()
}
