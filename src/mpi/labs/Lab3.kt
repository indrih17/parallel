package mpi.labs

import mpi.CommunicationInfo
import mpi.awaitAll
import mpi.awaitAllMessages
import mpi.data.Message
import mpi.data.stringify
import mpi.data.getColumn
import mpi.data.merge
import mpi.data.multiple
import mpi.data.commWorld
import mpi.data.centerRank
import threads.splitWithIterationNumber
import kotlin.random.Random

private const val matrixSize = 2

fun main(args: Array<String>) = commWorld(args) { communicator ->
    val rank = communicator.rank
    val commInfo = CommunicationInfo(communicator, matrixSize)

    when (rank) {
        centerRank -> {
            val matrix = List(matrixSize) { Message(matrixSize) { Random.nextInt(1, 5) } }
            val vector = Message(matrixSize) { Random.nextInt(1, 5) }
            println(matrix.stringify())
            println("Vector:   ${vector.contentToString()}")

            matrix
                .indices
                .map(matrix::getColumn)
                .splitWithIterationNumber(step = commInfo.subMessageSize) { iteration, list ->
                    list
                        .map { communicator.asyncSend(it, destination = iteration) }
                        .plus(communicator.asyncSend(vector, destination = iteration))
                }
                .flatten()
                .awaitAll()

            val result = commInfo
                .receivingRanks
                .map { communicator.asyncReceive(size = commInfo.subMessageSize, source = it) }
                .awaitAllMessages()
                .merge()
            println("Result:   ${result.contentToString()}")
        }
        in commInfo.receivingRanks -> {
            val vectorList = commInfo
                .subMessageRange
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
        else -> println("Ранк не используется: $rank")
    }
}
