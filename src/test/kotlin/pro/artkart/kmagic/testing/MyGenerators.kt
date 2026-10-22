package pro.artkart.kmagic.testing

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.forAll
import java.util.Random

val personGen: Arb<Person> = Arb.bind(
    Arb.string(),
    Arb.int(1..50),
) { name, age ->
    Person(name, age)
}


fun charMap(str: String): Map<Char, Int> =
    str.fold(mapOf(), ::updateMap)

fun getCharUsed(words: List<String>) =
    words.groupBy(::charMap)

val stringGenerator: Arb<List<Pair<String, Map<Char, Int>>>> =
    Arb.list(Arb.list(Arb.int(97, 122)))
        .map { intListList ->
            intListList.asSequence().map { intList ->
                intList.map { n -> n.toChar() }
            }.map { charList ->
                Pair(String(charList.toCharArray()), makeMap(charList))
            }.toList()
        }

fun stringGeneratorV2(maxList: Int, maxString: Int):
        Arb<List<Pair<String, Map<Char, Int>>>> =
    ListGenerator(ListGenerator(Arb.int(32, 127), maxString), maxList)
        .map { intListList ->
            intListList.asSequence().map { intList ->
                intList.map { n -> n.toChar() }
            }.map { charList ->
                Pair(String(charList.toCharArray()), makeMap(charList))
            }.toList()
        }

fun mapGenerator(min: Char = 'a', max: Char = 'z'): Arb<Map<Char, Int>> =
    Arb.list(
        Arb.int(min.code, max.code)
            .map(Int::toChar)
    )
        .map(::makeMap)

fun charGenerator(p: (Char) -> Boolean): Arb<Char> =
    Arb.int(0, 255).map(Int::toChar).filter(p)

fun makeMap(charList: List<Char>): Map<Char, Int> =
    charList.fold(mapOf(), ::updateMap)

fun updateMap(map: Map<Char, Int>, char: Char) = when {
    map.containsKey(char) -> map + Pair(char, map[char]!! + 1)
    else -> map + Pair(char, 1)
}

class MyGeneratorsTest : StringSpec() {

    private val random = Random()
    private val min = 'a'
    private val max = 'z'

    init {

        "getCharUsed test" {
            forAll(stringGenerator) { list ->
                getCharUsed(list.map { it.first }).keys.toSet() ==
                        list.asSequence().map { it.second }.toSet()
            }
        }

        "getCharUsedV2 test" {
            forAll(stringGeneratorV2(100, 100)) { list ->
                getCharUsed(list.map { it.first }).keys.toSet() ==
                        list.asSequence().map { it.second }.toSet()
            }
        }

        "getCarUsed testV2" {
            forAll(mapGenerator()) { map ->
                (random.nextInt(max.code - min.code) + min.code)
                    .toChar().let {
                        if (map.containsKey(it)) {
                            updateMap(map, it)[it] == map[it]!! + 1
                        } else {
                            updateMap(map, it)[it] == 1
                        } && updateMap(map, it) - it == map - it
                    }
            }
        }

        "getCarUsed testV3" {
            forAll(mapGenerator(), charGenerator(Char::isLetterOrDigit)) { map, char ->
                if (map.containsKey(char)) {
                    updateMap(map, char)[char] == map[char]!! + 1
                } else {
                    updateMap(map, char)[char] == 1
                } && updateMap(map, char) - char == map - char
            }
        }

        "getCarUsed testV4" {
            forAll(mapGenerator(), charGenerator(Char::isLetterOrDigit)) { map, char ->
                updateMap(map, char)[char] == map.getOrDefault(char, 0) + 1
                        && updateMap(map, char) - char == map - char
            }
        }
    }
}
