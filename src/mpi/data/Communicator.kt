@file:Suppress("NOTHING_TO_INLINE")

package mpi.data

import mpi.Intracomm
import mpi.MPI
import mpi.Request
import mpi.Status

/** Запуск [MPI] и гарантированная отчистка ресурсов после завершения. */
inline fun commWorld(args: Array<String>, block: (communicator: Communicator) -> Unit) {
    MPI.Init(args)
    try {
        block(Communicator(MPI.COMM_WORLD))
    } finally {
        MPI.Finalize()
    }
}

const val mainTag = 0
const val centerRank = 0

/** Обёртка над коммуникатором для использования в Kotlin style. */
inline class Communicator(val intracomm: Intracomm) {
    val rank: Int get() = intracomm.Rank()
    val size: Int get() = intracomm.Size()
    val lastRank: Int get() = size - 1

    /**
     * Отправка сообщения.
     * @param message сообщение в виде массива чисел.
     * @param destination ранк, куда доставляется сообщение.
     */
    inline fun send(
        message: Message,
        destination: Int,
        tag: Int = mainTag,
        size: Int = message.size,
        offset: Int = 0
    ): Unit =
        intracomm.Send(message, offset, size, MPI.INT, destination, tag)

    /**
     * Получение сообщения.
     * @param size размер ожидаемого сообщения.
     * @param source ранк, откуда доставляется сообщение.
     * @return сообщение в виде массива чисел.
     */
    inline fun receive(
        size: Int,
        source: Int,
        tag: Int = mainTag
    ): Message =
        Message(size).also { intracomm.Recv(it, 0, size, MPI.INT, source, tag) }

    /** Асинхронный вариант [send]. */
    inline fun asyncSend(
        message: Message,
        destination: Int,
        tag: Int = mainTag,
        size: Int = message.size,
        offset: Int = 0
    ): Request =
        intracomm.Isend(message, offset, size, MPI.INT, destination, tag)

    /** Асинхронный вариант [receive]. */
    inline fun asyncReceive(
        size: Int,
        source: Int,
        tag: Int = mainTag
    ): Pair<Message, Request> {
        val message = Message(size)
        val request = intracomm.Irecv(message, 0, size, MPI.INT, source, tag)
        return message to request
    }

    /** Позволяет проверить входные сообщения без их реального приема. */
    inline fun probe(source: Int, tag: Int = mainTag): Status =
        intracomm.Probe(source, tag)
}
