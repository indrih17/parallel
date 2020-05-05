@file:Suppress("NOTHING_TO_INLINE")

package mpi.data

import mpi.Intracomm
import mpi.MPI
import mpi.Request
import mpi.Status

/** Запуск [MPI] и гарантированная отчистка ресурсов после завершения. */
inline fun commWorld(args: Array<String>, block: (communicator: Communicator) -> Unit) {
    MPI.Init(args)
    block(Communicator(MPI.COMM_WORLD))
    MPI.Finalize()
}

typealias Rank = Int

const val mainTag = 0
const val centerRank: Rank = 0

/**
 * Обёртка над коммуникатором для использования в Kotlin style.
 * Коммуникатор - множество процессов, образующих логическую область для выполнения
 * коллективных операций (обменов информацией и др.).
 *
 * Блокирующие вызовы отправки и получения приостанавливают исполнение программы до момента, когда данные
 * будут отправлены (скопированы из буфера отправки), но они не обязаны быть получены получающей задачей.
 * Содержимое буфера отправки теперь может быть безопасно модифицировано без воздействия на отправленное сообщение.
 * Завершение блокирующего обмена подразумевает, что данные в буфере получения правильные.
 *
 * Неблокирующие вызовы возвращаются немедленно после инициации коммуникации. Программист не знает, скопированы ли
 * отправляемые данные из буфера отправки, или являются ли передаваемые данные прибывшими к получателю.
 * Таким образом, перед очередным использованием буфера сообщения программист должен проверить его статус.
 */
inline class Communicator(private val intracomm: Intracomm) {
    val rank: Rank get() = intracomm.Rank()
    val numberOfRanks: Int get() = intracomm.Size()

    /**
     * Блокирующая отправка сообщения.
     * @param message сообщение в виде массива чисел.
     * @param destination ранк, куда доставляется сообщение.
     */
    fun send(
        message: Message,
        destination: Int,
        tag: Int = mainTag
    ) {
        intracomm.Send(message, 0, message.size, MPI.INT, destination, tag)
    }

    /**
     * Блокирующее получение сообщения.
     * @param source ранк, откуда доставляется сообщение.
     * @return сообщение в виде массива чисел.
     *
     * Может принимать сообщения, отправленные в любом режиме.
     */
    fun receive(source: Int, size: Int, tag: Int = mainTag): Message =
        Message(size).also { intracomm.Recv(it, 0, size, MPI.INT, source, tag) }

    /** Асинхронный вариант [send]. */
    fun asyncSend(
        message: Message,
        destination: Int,
        tag: Int = mainTag
    ): Request =
        intracomm.Isend(message, 0, message.size, MPI.INT, destination, tag)

    /** Асинхронный вариант [receive]. */
    fun asyncReceive(
        size: Int,
        source: Int,
        tag: Int = mainTag
    ): Pair<Message, Request> {
        val message = Message(size)
        val request = intracomm.Irecv(message, 0, size, MPI.INT, source, tag)
        return message to request
    }

    /** Позволяет проверить входные сообщения без их реального приема. */
    fun probe(source: Int, tag: Int = mainTag): Status =
        intracomm.Probe(source, tag)
}
