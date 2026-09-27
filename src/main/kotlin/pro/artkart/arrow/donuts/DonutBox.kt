package pro.artkart.arrow.donuts

import pro.artkart.arrow.donuts.model.Donut

open class DonutBox(
    protected val capacity: Int = 0
) {

    protected val donuts = mutableListOf<Donut>()

    override fun toString(): String = "DonutBox($donuts)"
}
