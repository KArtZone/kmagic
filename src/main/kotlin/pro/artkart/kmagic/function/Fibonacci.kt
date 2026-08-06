package pro.artkart.kmagic.function

import java.math.BigDecimal

object Fibonacci {

        private val cache = mutableMapOf(0 to BigDecimal.ONE, 1 to BigDecimal.ONE)

        fun memoizedFibonacci(n: Int): BigDecimal {
            tailrec fun fib(index: Int, current: BigDecimal, previous: BigDecimal): BigDecimal {
                return if (index > n) {
                    cache[index - 1]!!
                } else {
                    val cur = current + previous
                    cache[index] = cur
                    fib(index + 1, cur, current)
                }
            }

            return if (cache.contains(n))
                cache[n]!!
            else {
                val index = findNearestTo(n)
                fib(index + 1, cache[index]!!, cache[index - 1]!!)
            }
        }

        private fun findNearestTo(n: Int): Int {
            var index = n - 1
            while (true) {
                if (cache.contains(index))
                    return index
                else
                    --index
            }
        }
    }
