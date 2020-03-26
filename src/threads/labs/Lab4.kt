package threads.labs

import threads.data.ThreadPool
import threads.data.matrixOf
import threads.data.multiple
import threads.data.Matrix
import threads.data.matrix
import threads.data.map

fun main() {
    val threadPool = ThreadPool(10)
    val first = matrixOf(
        listOf(1, 2),
        listOf(3, 4)
    )
    val second = matrixOf(
        listOf(5, 6),
        listOf(7, 8)
    )
    val result = multiple(first, second, threadPool)
    val normalResult = first multiple second
    println(result == normalResult)
    threadPool.interrupt()
}

private fun multiple(first: Matrix<Int>, second: Matrix<Int>, threadPool: ThreadPool): Matrix<Int> =
    matrix(first.size) { i, j ->
        threadPool.scheduleTask {
            val row = first.getRowVector(i)
            val column = second.getColumnVector(j)
            row.multiple(column).sum()
        }
    }.map { task -> task.join() }