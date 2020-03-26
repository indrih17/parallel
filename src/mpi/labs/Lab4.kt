package mpi.labs

import mpi.CommunicationInfo
import mpi.awaitAll
import mpi.awaitAllMessages
import mpi.data.commWorld
import mpi.data.centerRank
import mpi.data.Message
import mpi.data.stringify
import mpi.data.getColumn
import mpi.data.merge
import mpi.data.multiple
import threads.splitWithIterationNumber
import kotlin.random.Random

private const val matrixSize = 2

fun main(args: Array<String>) = commWorld(args) { communicator ->
    val rank = communicator.rank
    val commInfo = CommunicationInfo(communicator, matrixSize)

    when (rank) {
        centerRank -> {
            val matrix1 = List(matrixSize) { Message(matrixSize) { Random.nextInt(1, 5) } }
            val matrix2 = List(matrixSize) { Message(matrixSize) { Random.nextInt(1, 5) } }
            println(matrix1.stringify())
            println(matrix2.stringify())

            matrix1
                .indices
                .map(matrix1::getColumn)
                .splitWithIterationNumber(step = commInfo.subMessageSize) { iteration, list ->
                    list.map { communicator.asyncSend(it, destination = iteration) }
                }
                .flatten()
                .awaitAll()
            matrix2
                .splitWithIterationNumber(step = commInfo.subMessageSize) { iteration, list ->
                    list.map { communicator.asyncSend(it, destination = iteration) }
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
            val vectorList1 = commInfo
                .subMessageRange
                .map { communicator.asyncReceive(size = matrixSize, source = centerRank) }
                .awaitAllMessages()
            val vectorList2 = commInfo
                .subMessageRange
                .map { communicator.asyncReceive(size = matrixSize, source = centerRank) }
                .awaitAllMessages()
            communicator.send(
                message = vectorList1
                    .mapIndexed { index, msg -> (msg multiple vectorList2[index]).sum() }
                    .toIntArray(),
                destination = centerRank
            )
        }
        else -> println("Ранк не используется: $rank")
    }
}
