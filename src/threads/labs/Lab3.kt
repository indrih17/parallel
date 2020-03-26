package threads.labs

import threads.data.ThreadPool
import threads.data.Matrix
import threads.data.Vector
import threads.data.vector
import threads.testVector
import threads.testMatrix
import threads.data.multiple

fun main() {
    val threadPool = ThreadPool(10)
    val matrix = testMatrix(2)
    val vector = testVector(2)
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
