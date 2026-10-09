package pro.artkart.kmagic.actor

import pro.artkart.kmagic.common.List
import pro.artkart.kmagic.common.Result
import pro.artkart.kmagic.common.sequence

class Manager(
    id: String,
    list: List<Int>,
    private val client: Actor<Result<List<Int>>>,
    private val workers: Int
) : AbstractActor<Int>(id) {

    private val initial: List<Pair<Int, Int>>
    private val workList: List<Int>
    private val resultList: List<Int>
    private val managerFunction: (Manager) -> (Behavior) -> (Int) -> Unit


    init {
        val splitList = list.splitAt(workers)
        initial = splitList.first.zipWithPosition()
        workList = splitList.second
        resultList = List.Companion()
        managerFunction = { manager ->
            { behavior ->
                { item ->
                    val result = behavior.resultList.cons(item)
                    if (result.length == list.length) {
                        client.tell(Result.Companion(result))
                    } else {
                        manager.context.become(
                            Behavior(
                                behavior.workList
                                    .tailSafe()
                                    .getOrElse(List.Companion()),
                                result
                            )
                        )
                    }
                }
            }
        }
    }

    fun start() {
        onReceive(0, self())
        sequence(initial.map { initWorker(it) })
            .forEach(
                onSuccess = { initWorkers(it) },
                onFailure = { tellClientEmptyResult(it.message ?: "Unknown error") }
            )
    }

    private fun initWorker(t: Pair<Int, Int>): Result<() -> Unit> =
        Result.Companion({
            Worker("Worker #${t.second}").tell(t.first, self())
        })

    private fun initWorkers(lst: List<() -> Unit>) {
        lst.forEach { it() }
    }

    private fun tellClientEmptyResult(string: String) {
        client.tell(Result.Companion.failure("$string caused by empty input list"))
    }

    override fun onReceive(message: Int, sender: Result<Actor<Int>>) {
        context.become(Behavior(workList, resultList))
    }

    internal inner class Behavior internal constructor(
        internal val workList: List<Int>,
        internal val resultList: List<Int>
    ) : MessageProcessor<Int> {

        override fun process(message: Int, sender: Result<Actor<Int>>) {
            managerFunction(this@Manager)(this@Behavior)(message)
            sender.forEach(onSuccess = { a: Actor<Int> ->
                workList.headSafe().forEach({ a.tell(it, self()) }) { a.shutdown() }
            })
        }
    }
}
