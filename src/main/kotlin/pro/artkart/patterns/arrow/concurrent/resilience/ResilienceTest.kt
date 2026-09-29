package pro.artkart.patterns.arrow.concurrent.resilience


suspend fun main() {

    Retry.test()
    CircuitBreaker.test()
    Saga.test()
}
