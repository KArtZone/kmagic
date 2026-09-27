package pro.artkart.kmagic.io

import io.github.oshai.kotlinlogging.KotlinLogging
import pro.artkart.kmagic.exception.Resolution
import pro.artkart.kmagic.lazy.Stream
import pro.artkart.kmagic.list.ImmutableList

class ScriptReader : Input {

    private val commands: ImmutableList<String>

    constructor(commands: ImmutableList<String>) : super() {
        this.commands = commands
    }

    constructor(vararg commands: String) {
        this.commands = ImmutableList(*commands)
    }

    override fun readString(): Resolution<Pair<String, Input>> = when {
        commands.isEmpty() -> Resolution.failure("Not enough entries in script")
        else -> Resolution(
            Pair(commands.headSafe().getOrElse(""), ScriptReader(commands.drop(1)))
        )
    }

    override fun readInt(): Resolution<Pair<Int, Input>> = try {
        val number = commands.headSafe().getOrElse("").toInt()
        when {
            commands.isEmpty() -> Resolution.failure("Not enough entries in script")

            number > 0 -> Resolution(
                Pair(
                    number,
                    ScriptReader(commands.drop(1))
                )
            )

            else -> Resolution()
        }
    } catch (e: Exception) {
        Resolution.failure(RuntimeException(e))
    }

    override fun close() {}
}

fun main() {

    val log = KotlinLogging.logger { }

    Stream.unfold(
        ScriptReader(
            "1", "Mickey", "Mouse",
            "2", "Minnie", "Mouse",
            "3", "Donald", "Duck"
        ), ::person
    ).toList()
        .forEach { (id, firstName, lastName) ->
            log.info { "$id $firstName $lastName" }
        }

}
