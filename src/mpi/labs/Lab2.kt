package mpi.labs

import mpi.CommunicationInfo
import mpi.awaitAllMessages
import mpi.data.commWorld
import mpi.data.centerRank
import mpi.data.Message
import mpi.data.splitWithIterationNumber
import mpi.data.merge
import mpi.data.multiple
import mpi.getLength
import kotlin.random.Random

fun main(args: Array<String>) = commWorld(args) { communicator ->
    val rank = communicator.rank
    val vectorSize = 12
    val commInfo = CommunicationInfo(communicator, vectorSize)
    when (rank) {
        centerRank -> {
            val vector1 = Message(vectorSize) { Random.nextInt(1, 10) }
            val vector2 = Message(vectorSize) { Random.nextInt(1, 10) }
            println("Vector 1: ${vector1.contentToString()}")
            println("Vector 2: ${vector2.contentToString()}")

            vector1.splitWithIterationNumber(step = commInfo.subMessageSize) { iteration, msg ->
                communicator.asyncSend(msg, destination = iteration)
            }
            vector2.splitWithIterationNumber(step = commInfo.subMessageSize) { iteration, msg ->
                communicator.asyncSend(msg, destination = iteration)
            }

            val result = commInfo
                .receivingRanks
                .map { communicator.asyncReceive(size = commInfo.subMessageSize, source = it) }
                .awaitAllMessages()
                .merge()
            println("Result:   ${result.contentToString()}")
        }
        in commInfo.receivingRanks -> {
            val count = communicator.probe(source = centerRank).getLength()
            val vector1 = communicator.receive(size = count, source = centerRank)
            val vector2 = communicator.receive(size = count, source = centerRank)
            communicator.send(
                message = vector1 multiple vector2,
                destination = centerRank
            )
        }
        else -> println("Ранк не используется: $rank")
    }
}
