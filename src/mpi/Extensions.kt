package mpi

import mpi.data.Message

/** Длина сообщения. */
fun Status.getLength(): Int = Get_count(MPI.INT)

fun List<Request>.awaitAll(): List<Status> = map { it.Wait() }

fun Pair<Message, Request>.await(): Message {
    second.Wait()
    return first
}

fun List<Pair<Message, Request>>.awaitAllMessages(): List<Message> =
    map(Pair<Message, Request>::await)

val IntRange.size: Int get() = last - first + 1
