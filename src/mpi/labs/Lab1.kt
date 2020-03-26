package mpi.labs

import mpi.CommunicationInfo
import mpi.awaitAllMessages
import mpi.getLength
import mpi.data.centerRank
import mpi.data.commWorld
import mpi.data.Message
import mpi.data.splitWithIterationNumber
import mpi.data.merge
import kotlin.random.Random

fun main(args: Array<String>) = commWorld(args) { communicator ->
    val rank = communicator.rank
    val messageSize = 12
    val number = 2
    val commInfo = CommunicationInfo(communicator, messageSize)
    when (rank) {
        centerRank -> {
            val vector = Message(messageSize) { Random.nextInt(1, 10) }
            println("Vector: ${vector.contentToString()}")
            println("Number: $number")

            vector.splitWithIterationNumber(step = commInfo.subMessageSize) { iteration, msg ->
                communicator.asyncSend(msg, destination = iteration)
            }
            val result = commInfo
                .receivingRanks
                .map { communicator.asyncReceive(size = commInfo.subMessageSize, source = it) }
                .awaitAllMessages()
                .merge()
            println("Result: ${result.contentToString()}")
        }
        in commInfo.receivingRanks -> {
            val count = communicator.probe(source = centerRank).getLength()
            val vector = communicator.receive(size = count, source = centerRank)
            communicator.asyncSend(
                message = vector.map { it * number }.toIntArray(),
                destination = centerRank
            )
        }
        else -> println("Ранк не используется: $rank")
    }
}
