package threads.labs

import threads.data.ThreadPool
import threads.data.Vector
import threads.data.multiple
import threads.splitVector
import threads.testVector

fun main() {
    val threadPool = ThreadPool(10)
    val vector = testVector(1000)
    val number = 101
    val result = multiple(vector, number = number, threadPool = threadPool)
    val normalVector = vector multiple number
    println(result == normalVector)
    threadPool.interrupt()
}

private fun multiple(vector: Vector<Int>, number: Int, threadPool: ThreadPool): Vector<Int> =
    vector
        .splitVector(step = vector.size / threadPool.size) { list ->
            threadPool.scheduleTask { list.map { it * number } }
        }
        .fold(emptyList()) { acc, task -> acc + task.join() }
