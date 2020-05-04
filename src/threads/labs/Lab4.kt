package threads.labs

import threads.data.ThreadPool
import threads.data.multiple
import threads.data.Matrix
import threads.data.matrix
import threads.data.map
import threads.testMatrix
import kotlin.system.measureTimeMillis

fun main() {
    val threadPool = ThreadPool(10)
    val first = testMatrix(500)
    val second = testMatrix(500)
    val multiTime = measureTimeMillis {
        multiple(first, second, threadPool)
    }
    val singleTime = measureTimeMillis {
        first multiple second
    }
    println("Single: $singleTime, multi: $multiTime")
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