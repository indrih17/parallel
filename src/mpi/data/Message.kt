package mpi.data

typealias Message = IntArray

fun messageOf(vararg elements: Int) = intArrayOf(*elements)

infix fun Message.multiple(other: Message): Message {
    assert(size == other.size)
    return mapIndexed { index, number -> number * other[index] }.toIntArray()
}

fun List<Message>.merge(): Message =
    fold(messageOf()) { acc, message -> acc + message }

fun List<Message>.stringify(): String =
    mapIndexed { index, msg -> "Matrix $index: ${msg.contentToString()}" }
        .joinToString(separator = "\n")
