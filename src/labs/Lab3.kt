package labs

import data.ThreadPool
import data.Matrix
import data.Vector
import data.vector
import testVector
import testMatrix
import data.multiple

fun main() {
    val threadPool = ThreadPool(10)
    val matrix = testMatrix(100)
    val vector = testVector(100)
    val result = multiple(matrix, vector, threadPool)
    val normalResult = matrix multiple vector
    println(result == normalResult)
    threadPool.interrupt()
}

private fun multiple(matrix: Matrix<Int>, vector: Vector<Int>, threadPool: ThreadPool): Vector<Int> =
    vector(vector.size) { index ->
        threadPool.scheduleTask {
            matrix.getColumnVector(index).multiple(vector).sum()
        }
    }.map { task -> task.join() }
