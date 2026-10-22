package pro.artkart.kmagic.testing

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.arbitrary.ListShrinker
import io.kotest.property.arbitrary.arbitrary

class ListGenerator<T>(
    private val gen: Arb<T>,
    private val maxLength: Int,
) : Arb<List<T>>() {

    private val delegate: Arb<List<T>> = arbitrary(ListShrinker(0..maxLength)) { rs ->
        val size = rs.random.nextInt(maxLength)
        (0 until size).map { gen.sample(rs).value }
    }

    override fun edgecase(rs: RandomSource): Sample<List<T>>? = delegate.edgecase(rs)

    override fun sample(rs: RandomSource): Sample<List<T>> = delegate.sample(rs)
}
