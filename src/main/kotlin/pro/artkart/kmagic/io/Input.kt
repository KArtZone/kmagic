package pro.artkart.kmagic.io

import pro.artkart.kmagic.exception.Resolution

interface Input : AutoCloseable {

    fun readString(): Resolution<Pair<String, Input>>

    fun readInt(): Resolution<Pair<Int, Input>>

    fun readString(message: String): Resolution<Pair<String, Input>> = readString()

    fun readInt(message: String): Resolution<Pair<Int, Input>> = readInt()
}
