package threads.labs

import threads.data.ThreadPool
import threads.data.Matrix
import threads.data.Vector
import threads.data.vector
import threads.testVector
import threads.testMatrix
import threads.data.multiple
import kotlin.system.measureTimeMillis

fun main() {
    val threadPool = ThreadPool(10)
    val matrix = testMatrix(10000)
    val vector = testVector(10000)
    val multiTime = measureTimeMillis {
        multiple(matrix, vector, threadPool)
    }
    val singleTime = measureTimeMillis {
        matrix multiple vector
    }
    println("Single: $singleTime, multi: $multiTime")
    threadPool.interrupt()
}

private fun multiple(matrix: Matrix<Int>, vector: Vector<Int>, threadPool: ThreadPool): Vector<Int> =
    vector(vector.size) { index ->
        threadPool.scheduleTask {
            matrix.getColumnVector(index).multiple(vector).sum()
        }
    }.map { task -> task.join() }
