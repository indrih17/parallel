package labs

import data.ThreadPool
import data.Vector
import testVector
import splitVector
import kotlin.math.sqrt

fun main() {
    val threadPool = ThreadPool(10)
    val vector1 = testVector(1000)
    val vector2 = testVector(1000)
    val result = length(vector1, threadPool) * length(vector2, threadPool)
    val normalResult = length(vector1) * length(vector2)
    println(result == normalResult)
    threadPool.interrupt()
}

private fun length(vector: Vector<Int>, threadPool: ThreadPool): Double =
    vector
        .splitVector(step = vector.size / threadPool.size) { list ->
            threadPool.scheduleTask { sumOfSquares(list) }
        }
        .fold(0.0) { acc, task -> acc + task.join() }
        .let(::sqrt)


private fun length(vector: Vector<Int>): Double =
    sqrt(sumOfSquares(vector).toDouble())

private fun sumOfSquares(vector: Vector<Int>): Int = vector.sumBy { it * it }
