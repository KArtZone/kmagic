package pro.artkart.kmagic.io

import io.github.oshai.kotlinlogging.KotlinLogging
import pro.artkart.kmagic.exception.Resolution
import pro.artkart.kmagic.lazy.Stream
import java.io.BufferedReader
import java.io.File
import java.lang.ClassLoader.getSystemResource

class FileReader(
    private val reader: BufferedReader
) : AbstractReader(reader) {

    companion object {
        operator fun invoke(path: String): Resolution<FileReader> = try {
            Resolution(
                FileReader(
                    File(getSystemResource(path).file).bufferedReader()
                )
            )
        } catch (e: Exception) {
            Resolution.failure(RuntimeException(e))
        }
    }
}

fun main() {

    val log = KotlinLogging.logger { }

    FileReader("persons.txt").forEach({ reader ->
        reader.use {
            Stream.unfold(it, ::person)
                .toList()
                .forEach { (id, firstName, lastName) ->
                    log.info { "$id $firstName $lastName" }
                }
        }
    }, {
        log.error { it.message }
    })
}
