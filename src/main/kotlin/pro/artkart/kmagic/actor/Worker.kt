package pro.artkart.kmagic.actor

import pro.artkart.kmagic.common.Result

class Worker(
    id: String
) : AbstractActor<Pair<Int, Int>>(id) {

    override fun onReceive(message: Pair<Int, Int>, sender: Result<Actor<Pair<Int, Int>>>) =
        sender.forEach(onSuccess = {
            it.tell(Pair(fastFibonacci(message.first), message.second), self())
        })

    private fun slowFibonacci(n: Int): Int = when (n) {
        0, 1 -> 1
        else -> slowFibonacci(n - 1) + slowFibonacci(n - 2)
    }

    private fun fastFibonacci(n: Int): Int {
        tailrec fun fastFibonacci(acc: Pair<Int, Int>, index: Int): Int = when (index) {
            0 -> acc.first
            else -> fastFibonacci(Pair(acc.second, acc.first + acc.second), index - 1)
        }
        return fastFibonacci(1 to 1, n)
    }
}
