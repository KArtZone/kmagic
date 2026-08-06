package pro.artkart.kmagic.function

import java.util.concurrent.ConcurrentHashMap

class Memoizer<T, R> private constructor() {

    private val cache = ConcurrentHashMap<T, R>()

    fun doMemoize(f: (T) -> R): (T) -> R = { key ->
        cache.computeIfAbsent(key, f)
    }

    companion object {
        fun <T, R> memoize(f: (T) -> R): (T) -> R = Memoizer<T, R>().doMemoize(f)
    }
}

fun main() {
    fun longComputation(x: Int): Int {
        Thread.sleep(1000)
        return x
    }

    val startTime1 = System.currentTimeMillis()
    val result1 = longComputation(43)
    val time1 = System.currentTimeMillis() - startTime1
    val memoizedLongComputation =
        Memoizer.memoize(::longComputation)
    val startTime2 = System.currentTimeMillis()
    val result2 = memoizedLongComputation(43)
    val time2 = System.currentTimeMillis() - startTime2
    val startTime3 = System.currentTimeMillis()
    val result3 = memoizedLongComputation(43)
    val time3 = System.currentTimeMillis() - startTime3
    println(
        "Call to nonmemoized function: result = " +
                "$result1, time = $time1"
    )
    println(
        "First call to memoized function: result = " +
                "$result2, time = $time2"
    )
    println(
        "Second call to nonmemoized function: result = " +
                "$result3, time = $time3"
    )

}