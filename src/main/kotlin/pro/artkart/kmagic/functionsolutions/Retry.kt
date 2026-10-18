package pro.artkart.kmagic.functionsolutions

import pro.artkart.kmagic.common.Result
import java.util.concurrent.TimeUnit


fun <T, R> retry(f: (T) -> R, attempts: Int, delay: Long = 10): (T) -> Result<R> {
    fun retry(arg: T, result: Result<R>, e: Result<R>, remainAttempts: Int): Result<R> =
        result.orElse {
            when (remainAttempts) {
                0 -> e
                else -> {
                    TimeUnit.MILLISECONDS.sleep(delay)
                    retry(arg, Result.of { f(arg) }, e, remainAttempts - 1)
                }
            }
        }
    return { retry(it, Result.of { f(it) }, Result(), attempts - 1) }
}
