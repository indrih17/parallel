package mpi.labs

import mpi.*
import mpi.data.commWorld
import mpi.data.centerRank
import mpi.data.Message
import mpi.data.merge
import mpi.data.multiple
import kotlin.random.Random

fun main(args: Array<String>) = commWorld(args) { communicator ->
    val currentRank = communicator.rank
    val vectorSize = 12
    val commInfo = CommInfo(communicator, vectorSize, centralRankCollectsData = true)
    when (currentRank) {
        centerRank -> {
            val vector1 = Message(vectorSize) { Random.nextInt(1, 10) }
            val vector2 = Message(vectorSize) { Random.nextInt(1, 10) }
            println("Vector 1: ${vector1.contentToString()}")
            println("Vector 2: ${vector2.contentToString()}")

            listOf(vector1, vector2).forEach { vector ->
                commInfo.split(vector).forEach { (rank, msg) -> communicator.asyncSend(msg, rank) }
            }

            val result = commInfo
                .onEachRank { rank, range -> communicator.asyncReceive(size = range.size, source = rank) }
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
        else -> println("Ранк не используется: $currentRank")
    }
}
