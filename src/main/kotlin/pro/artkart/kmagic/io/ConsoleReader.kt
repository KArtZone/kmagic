package pro.artkart.kmagic.io

import io.github.oshai.kotlinlogging.KotlinLogging
import pro.artkart.kmagic.exception.Resolution
import pro.artkart.kmagic.lazy.Stream
import java.io.BufferedReader
import java.io.InputStreamReader

class ConsoleReader(
    private val reader: BufferedReader
) : AbstractReader(reader) {

    override fun readString(message: String): Resolution<Pair<String, Input>> {
        log.info { message }
        return readString()
    }

    override fun readInt(message: String): Resolution<Pair<Int, Input>> {
        log.info { message }
        return readInt()
    }

    companion object {

        val log = KotlinLogging.logger { }

        operator fun invoke(): ConsoleReader = ConsoleReader(
            BufferedReader(InputStreamReader(System.`in`))
        )
    }
}

fun main() {

    val log = KotlinLogging.logger { }

    ConsoleReader().use {
        Stream.unfold(it, ::person)
            .toList()
            .forEach { (id, firstName, lastName) ->
                log.info { "$id $firstName $lastName" }
            }
    }
}


fun person(input: Input): Resolution<Pair<Person, Input>> =
    input.readInt("Enter id:").flatMap { id ->
        id.second.readString("Enter first name:").flatMap { firstName ->
            firstName.second.readString("Enter last name:").map { lastName ->
                Pair(Person(id.first, firstName.first, lastName.first), lastName.second)
            }
        }
    }
