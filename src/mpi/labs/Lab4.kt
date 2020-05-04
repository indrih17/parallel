package mpi.labs

import mpi.*
import mpi.data.commWorld
import mpi.data.centerRank
import mpi.data.Message
import mpi.data.stringify
import mpi.data.multiple
import kotlin.random.Random

private const val matrixSize = 2

fun main(args: Array<String>) = commWorld(args) { communicator ->
    val currentRank = communicator.rank
    val commInfo = CommInfo(communicator, matrixSize, centralRankCollectsData = true)
    when (currentRank) {
        centerRank -> {
            val matrix1 = List(matrixSize) { Message(matrixSize) { Random.nextInt(1, 5) } }
            val matrix2 = List(matrixSize) { Message(matrixSize) { Random.nextInt(1, 5) } }
            println(matrix1.stringify())
            println(matrix2.stringify())

            listOf(matrix1, matrix2).forEach { matrix ->
                commInfo
                    .split(matrix)
                    .map { (rank, list) -> list.map { communicator.asyncSend(it, rank) } }
                    .flatten()
                    .awaitAll()
            }

            val result = commInfo.onEachRank { source, range ->
                range
                    .map { communicator.asyncReceive(size = matrixSize, source = source) }
                    .awaitAllMessages()
            }

            println("Result:\n${result.joinToString(separator = "\n") { list ->
                list.joinToString { it.contentToString() }
            } }")
        }
        in commInfo.receivingRanks -> {
            val vectorList1 = commInfo
                .rangeForRank(currentRank)
                .map { communicator.asyncReceive(size = matrixSize, source = centerRank) }
                .awaitAllMessages()
            val vectorList2 = commInfo
                .rangeForRank(currentRank)
                .map { communicator.asyncReceive(size = matrixSize, source = centerRank) }
                .awaitAllMessages()
            vectorList1
                .asSequence()
                .mapIndexed { index, msg -> msg multiple vectorList2[index] }
                .map { communicator.asyncSend(it, destination = centerRank) }
                .toList()
                .awaitAll()
        }
        else -> println("Ранк не используется: $currentRank")
    }
}
