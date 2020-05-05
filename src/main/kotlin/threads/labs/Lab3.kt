package threads.labs

import koma.matrix.Matrix
import threads.data.ThreadPool
import threads.data.Vector
import threads.data.multiple
import threads.data.vector
import threads.getColumnVector
import threads.testVector
import threads.testMatrix
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

/** Умножение матрицы на вектор. Результатом является вектор. */
private infix fun Matrix<Int>.multiple(vector: Vector<Int>): Vector<Int> =
    vector(vector.size) { index ->
        getColumnVector(index).multiple(vector).sum()
    }

private fun multiple(matrix: Matrix<Int>, vector: Vector<Int>, threadPool: ThreadPool): Vector<Int> =
    vector(vector.size) { index ->
        threadPool.scheduleTask {
            matrix.getColumnVector(index).multiple(vector).sum()
        }
    }.map { task -> task.join() }
