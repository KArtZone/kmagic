package pro.artkart.kmagic.actor

import pro.artkart.kmagic.common.Heap
import pro.artkart.kmagic.common.List
import pro.artkart.kmagic.common.Result
import pro.artkart.kmagic.common.sequence

class Manager(
    id: String,
    list: List<Int>,
    private val client: Actor<Int>,
    private val workers: Int
) : AbstractActor<Pair<Int, Int>>(id) {

    private val initial: List<Pair<Int, Int>>
    private val workList: List<Pair<Int, Int>>
    private val resultHeap: Heap<Pair<Int, Int>>
    private val managerFunction: (Manager) -> (Behavior) -> (Pair<Int, Int>) -> Unit
    private val limit: Int = list.length - 1


    init {
        val splitLists = list.zipWithPosition()
            .splitAt(workers)
        initial = splitLists.first
        workList = splitLists.second
        this@Manager.resultHeap = Heap { p1, p2 -> p1.second.compareTo(p2.second) }
        managerFunction = { manager ->
            { behavior ->
                { item ->
                    val result = streamResult(
                        behavior.resultHeap + item,
                        behavior.expected,
                        List()
                    )
                    result.third.forEach { client.tell(it) }
                    if (result.second == limit) {
                        client.tell(-1)
                    } else {
                        manager.context.become(
                            Behavior(
                                behavior.workList
                                    .tailSafe()
                                    .getOrElse(List()),
                                result.first,
                                result.second
                            )
                        )
                    }
                }
            }
        }
    }

    fun start() {
        onReceive(0 to 0, self())
        sequence(initial.map { initWorker(it) })
            .forEach(
                onSuccess = { initWorkers(it) },
                onFailure = { client.tell(-1) }
            )
    }

    private fun streamResult(
        result: Heap<Pair<Int, Int>>,
        expected: Int,
        list: List<Int>
    ): Triple<Heap<Pair<Int, Int>>, Int, List<Int>> {
        val triple = Triple(result, expected, list)
        val temp = result.head.flatMap { head ->
            result.tail().map { tail ->
                if (head.second == expected) {
                    streamResult(tail, expected + 1, list.cons(head.first))
                } else {
                    triple
                }
            }
        }
        return temp.getOrElse(triple)
    }

    private fun initWorker(t: Pair<Int, Int>): Result<() -> Unit> =
        Result({
            Worker("Worker #${t.second}").tell(t, self())
        })

    private fun initWorkers(lst: List<() -> Unit>) {
        lst.forEach { it() }
    }

    override fun onReceive(message: Pair<Int, Int>, sender: Result<Actor<Pair<Int, Int>>>) {
        context.become(Behavior(workList, resultHeap, 0))
    }

    inner class Behavior internal constructor(
        internal val workList: List<Pair<Int, Int>>,
        internal val resultHeap: Heap<Pair<Int, Int>>,
        internal val expected: Int
    ) : MessageProcessor<Pair<Int, Int>> {

        override fun process(message: Pair<Int, Int>, sender: Result<Actor<Pair<Int, Int>>>) {
            managerFunction(this@Manager)(this@Behavior)(message)
            sender.forEach(onSuccess = { a: Actor<Pair<Int, Int>> ->
                workList.headSafe().forEach({ a.tell(it, self()) }) { a.shutdown() }
            })
        }
    }
}
