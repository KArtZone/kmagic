package pro.artkart.kmagic.testing

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.arbitrary.arbitrary

class StringGenerator(
    private val maxList: Int,
    private val maxString: Int,
) : Arb<List<Pair<String, Map<Char, Int>>>>() {

    private val delegate: Arb<List<Pair<String, Map<Char, Int>>>> = arbitrary { rs ->
        (0 until rs.random.nextInt(maxList)).map {
            (0 until rs.random.nextInt(maxString))
                .fold(Pair("", mapOf<Char, Int>())) { pair, _ ->
                    (rs.random.nextInt(122 - 96) + 96).toChar().let { char ->
                        Pair("${pair.first}$char", updateMap(pair.second, char))
                    }
                }
        }
    }

    override fun edgecase(rs: RandomSource): Sample<List<Pair<String, Map<Char, Int>>>>? = delegate.edgecase(rs)

    override fun sample(rs: RandomSource): Sample<List<Pair<String, Map<Char, Int>>>> = delegate.sample(rs)
}
