package pro.artkart.kmagic.io

import pro.artkart.kmagic.exception.Resolution
import java.io.BufferedReader

abstract class AbstractReader(
    private val reader: BufferedReader
) : Input {
    override fun readString(): Resolution<Pair<String, Input>> = try {
        reader.readLine().let { line ->
            when {
                line.isEmpty() -> Resolution.Companion()
                else -> Resolution.Companion(Pair(line, this))
            }
        }
    } catch (e: Exception) {
        Resolution.Companion.failure(RuntimeException(e))
    }

    override fun readInt(): Resolution<Pair<Int, Input>> = try {
        reader.readLine().let { line ->
            when {
                line.isEmpty() -> Resolution.Companion()
                else -> Resolution.Companion(Pair(line.toInt(), this))
            }
        }
    } catch (e: Exception) {
        Resolution.Companion.failure(RuntimeException(e))
    }

    override fun close() = reader.close()
}
