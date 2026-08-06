package pro.artkart.kmagic.function

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class FibonacciTest : StringSpec({

    "Fibonacci.memoizedFibonacci(7) should return 21" {
        Fibonacci.memoizedFibonacci(7) shouldBe BigDecimal(21)
    }
})
