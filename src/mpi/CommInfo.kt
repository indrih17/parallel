package mpi

import mpi.data.Communicator
import mpi.data.Message
import mpi.data.Rank

import kotlin.math.min

/** Информация, упрощающая работу при коммуникации между процессами. */
class CommInfo(
    communicator: Communicator,
    private val messageSize: Int,
    centralRankCollectsData: Boolean
) {
    private val centerRankShift = if (communicator.numberOfRanks > 1 && centralRankCollectsData) 1 else 0

    /** Размер каждого каждого подсообщения, которое можно отослать другому ранку. */
    private val subMsgSizeRaw: Int =
        if (messageSize >= communicator.numberOfRanks)
            messageSize / (communicator.numberOfRanks - centerRankShift)
        else
            1

    /** Ранки, которые будут получать информацию. Не входят простаивающие ранки, которым нечем заняться. */
    val receivingRanks: IntRange = centerRankShift..min((messageSize / subMsgSizeRaw), communicator.numberOfRanks - 1)

    /** Умная версия [subMsgSizeRaw], которая отрезает лишнее. */
    private fun subMessageSize(rank: Rank): Int {
        val ranks = receivingRanks.toList().size
        return if (rank == receivingRanks.last)
            subMsgSizeRaw - (subMsgSizeRaw * ranks - messageSize)
        else
            subMsgSizeRaw
    }

    /** Промежуток индексов, который необходимо обработать текущему ранку. */
    fun rangeForRank(rank: Rank): IntRange {
        val from = (rank - centerRankShift) * subMsgSizeRaw
        val to = from + subMessageSize(rank)
        return from until to
    }

    init {
        check(messageSize > 0)
    }
}

inline fun <T> CommInfo.onEachRank(block: (rank: Rank, range: IntRange) -> T): List<T> =
    receivingRanks.map { rank -> block(rank, rangeForRank(rank)) }

fun CommInfo.split(message: Message): Map<Rank, Message> =
    onEachRank { rank, range ->
        rank to message.copyOfRange(range.first, range.last + 1)
    }.toMap()

fun CommInfo.split(messages: Iterable<Message>): Map<Rank, List<Message>> =
    onEachRank { rank, range ->
        rank to messages.copyOfRange(range.first, range.last + 1)
    }.toMap()

private fun <T> Iterable<T>.copyOfRange(from: Int, to: Int): List<T> =
    drop(from).take(to - from)
