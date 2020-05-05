package threads.labs

import koma.create
import koma.matrix.Matrix
import koma.matrix.MatrixTypes
import threads.data.ThreadPool
import threads.data.multiple
import threads.getColumnVector
import threads.getRowVector
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
        first * second
    }
    println("Single: $singleTime, multi: $multiTime")
    threadPool.interrupt()
}

private fun multiple(first: Matrix<Int>, second: Matrix<Int>, threadPool: ThreadPool): Matrix<Int> {
    val arrayOfDoubleArrays = Array(first.numRows()) { i ->
        Array(first.numCols()) { j ->
            threadPool.scheduleTask {
                val row = first.getRowVector(i)
                val column = second.getColumnVector(j)
                row.multiple(column).sum()
            }
        }
            .map { task -> task.join() }
            .map { it.toDouble() }
            .toDoubleArray()
    }
    return create(arrayOfDoubleArrays, MatrixTypes.IntType)
}