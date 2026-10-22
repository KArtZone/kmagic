package pro.artkart.kmagic.testing

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.positiveInt
import io.kotest.property.forAll

class MultipleTest : StringSpec() {
    init {
        val parameters = listOf(
            Triple(2, listOf(4, 11, 8, 2, 3, 1, 14, 9, 5, 17, 6, 7), 14),
            Triple(2, listOf(), 0),
            Triple(0, listOf(1), 0),
        )

        "maxMultiple test" {
            forAll(
                Arb.positiveInt(),
                Arb.positiveInt(),
                Arb.positiveInt(),
            ) { multiple: Int, max: Int, value: Int ->
                isMaxMultiple(multiple)(max, value).let { result ->
                    result >= value
                            && result % multiple == 0 || result == max
                            && ((result % multiple == 0 && result >= value) || result % multiple != 0)
                }
            }
        }
    }
}


