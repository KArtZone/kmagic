package pro.artkart.kmagic.testing


fun maxMultiple(multiple: Int, list: List<Int>): Int =
    list.fold(0, isMaxMultiple(multiple))


fun isMaxMultiple(multiple: Int) =
    { max: Int, value: Int ->
        when {
            value / multiple * multiple == value && value > max -> value
            else -> max
        }
    }

data class Person(val name: String, val age: Int)
