package pro.artkart.kmagic.tree

import pro.artkart.kmagic.exception.Resolution
import pro.artkart.kmagic.list.ImmutableList
import pro.artkart.kmagic.utils.sequence

class Map<out K : Comparable<@UnsafeVariance K>, V>(
    val delegate: Tree<MapEntry<@UnsafeVariance K, V>> = Tree()
) {

    fun isEmpty(): Boolean = delegate.isEmpty

    fun size(): Int = delegate.size

    fun <R> foldLeft(identity: R, f: (R) -> (MapEntry<@UnsafeVariance K, V>) -> R, m: (R) -> (R) -> R): R =
        delegate.foldLeft(identity, { acc ->
            { item ->
                f(acc)(item)
            }
        }, m)

    fun <R> foldInOrder(identity: R, f: (R) -> (MapEntry<@UnsafeVariance K, V>) -> (R) -> R): R =
        delegate.foldInOrder(identity, f)

    fun values(): ImmutableList<V> = sequence(
        foldInOrder(ImmutableList<Resolution<V>>()) { left ->
            { value ->
                { right ->
                    left.concat(right.cons(value.value))
                }
            }
        }).getOrElse { ImmutableList() }

    override fun toString(): String = delegate.toString()

    operator fun plus(entry: Pair<@UnsafeVariance K, V>): Map<K, V> = Map(delegate + MapEntry(entry))

    operator fun plus(entry: MapEntry<@UnsafeVariance K, V>): Map<K, V> = Map(delegate + entry)

    operator fun minus(key: @UnsafeVariance K): Map<K, V> = Map(delegate - MapEntry(key))

    operator fun contains(key: @UnsafeVariance K): Boolean = delegate.contains(MapEntry(key))

    operator fun get(key: @UnsafeVariance K): Resolution<MapEntry<@UnsafeVariance K, V>> =
        delegate[MapEntry(key)]

    companion object {
        operator fun <K : Comparable<K>, V> invoke(): Map<K, V> = Map()
    }
}

class MapEntry<K : Comparable<@UnsafeVariance K>, V> private constructor(
    val key: K,
    val value: Resolution<V>
) : Comparable<MapEntry<K, V>> {

    override fun compareTo(other: MapEntry<K, V>): Int = key.compareTo(other.key)

    override fun equals(other: Any?): Boolean =
        this === other || when (other) {
            is MapEntry<*, *> -> key == other.key
            else -> false
        }

    override fun hashCode(): Int = key.hashCode()

    override fun toString(): String = "MapEntry($key, $value)"

    companion object {

        fun <K : Comparable<K>, V> of(key: K, value: V): MapEntry<K, V> = MapEntry(key, Resolution(value))

        operator fun <K : Comparable<K>, V> invoke(pair: Pair<K, V>): MapEntry<K, V> =
            MapEntry(pair.first, Resolution(pair.second))

        operator fun <K : Comparable<K>, V> invoke(key: K): MapEntry<K, V> = MapEntry(key, Resolution())
    }
}
