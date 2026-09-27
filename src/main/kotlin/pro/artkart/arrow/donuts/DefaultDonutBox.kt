package pro.artkart.arrow.donuts

import pro.artkart.arrow.donuts.model.Donut

class DefaultDonutBox(capacity: Int) : DonutBox(capacity) {

    fun add(donut: Donut): DefaultDonutBox = apply {
        when {
            donuts.size < capacity -> donuts += donut
            else -> throw NoSpaceInBoxException()
        }
    }

    fun delete(name: String): Donut? = donuts
        .find { it.name == name }
        ?.also { donuts.remove(it) }
}

class NoSpaceInBoxException : RuntimeException("No space in box")
