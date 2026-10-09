package pro.artkart.kmagic.actor

import pro.artkart.kmagic.common.Result

class Worker(
    id: String
) : AbstractActor<Int>(id) {

    override fun onReceive(message: Int, sender: Result<Actor<Int>>) =
        sender.forEach(onSuccess = {
            it.tell(slowFibonacci(message), self())
        })

    private fun slowFibonacci(n: Int): Int = when (n) {
        0, 1 -> 1
        else -> slowFibonacci(n - 1) + slowFibonacci(n - 2)
    }
}
