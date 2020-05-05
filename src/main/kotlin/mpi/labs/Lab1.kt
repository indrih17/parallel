package mpi.labs

import mpi.*
import mpi.data.centerRank
import mpi.data.commWorld
import mpi.data.Message
import mpi.data.merge
import kotlin.random.Random

fun main(args: Array<String>) = commWorld(args) { communicator ->
    val currentRank = communicator.rank
    val messageSize = 12
    val number = 2
    val commInfo = CommInfo(communicator, messageSize, centralRankCollectsData = true)
    when (currentRank) {
        centerRank -> {
            val vector = Message(messageSize) { Random.nextInt(1, 10) }
            println("Vector: ${vector.contentToString()}")
            println("Number: $number")

            commInfo
                .split(vector)
                .forEach { (rank, msg) -> communicator.asyncSend(msg, rank) }

            val result = commInfo
                .onEachRank { rank, range -> communicator.asyncReceive(size = range.size, source = rank) }
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
        else -> println("Ранк не используется: $currentRank")
    }
}
