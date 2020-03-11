package labs

import data.ThreadPool
import data.Vector
import data.multiple
import splitVector
import testVector

fun main() {
    val threadPool = ThreadPool(10)
    val vector = testVector(1000)
    val k = 101
    val result = multiple(vector, number = k, threadPool = threadPool)
    val normalVector = vector multiple k
    println(result == normalVector)
    threadPool.interrupt()
}

private fun multiple(vector: Vector<Int>, number: Int, threadPool: ThreadPool): Vector<Int> =
    vector
        .splitVector(step = vector.size / threadPool.size) { list ->
            threadPool.scheduleTask { list.map { it * number } }
        }
        .fold(emptyList()) { acc, task -> acc + task.join() }

