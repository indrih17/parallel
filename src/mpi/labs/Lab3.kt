package mpi.labs

import mpi.*
import mpi.data.*
import kotlin.random.Random

private const val matrixSize = 2

fun main(args: Array<String>) = commWorld(args) { communicator ->
    val currentRank = communicator.rank
    val commInfo = CommInfo(communicator, matrixSize, centralRankCollectsData = true)
    when (currentRank) {
        centerRank -> {
            val matrix = List(matrixSize) { Message(matrixSize) { Random.nextInt(1, 5) } }
            val vector = Message(matrixSize) { Random.nextInt(1, 5) }
            println(matrix.stringify())
            println("Vector:   ${vector.contentToString()}")

            commInfo
                .split(matrix)
                .map { (rank, list) ->
                    list
                        .map { communicator.asyncSend(it, rank) }
                        .plus(communicator.asyncSend(vector, rank))
                }
                .flatten()
                .awaitAll()

            val result = commInfo
                .onEachRank { rank, range -> communicator.asyncReceive(size = range.size, source = rank)  }
                .awaitAllMessages()
                .merge()
            println("Result:   ${result.contentToString()}")
        }
        in commInfo.receivingRanks -> {
            val vectorList = commInfo
                .rangeForRank(currentRank)
                .map { communicator.asyncReceive(size = matrixSize, source = centerRank) }
                .awaitAllMessages()
            val vector = communicator.receive(size = matrixSize, source = centerRank)
            communicator.send(
                message = vectorList
                    .map { (it multiple vector).sum() }
                    .toIntArray(),
                destination = centerRank
            )
        }
        else -> println("Ранк не используется: $currentRank")
    }
}
