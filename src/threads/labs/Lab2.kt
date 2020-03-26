package threads.labs

import threads.data.ThreadPool
import threads.data.Vector
import threads.data.multiple
import threads.splitVectorIndexed
import threads.testVector

fun main() {
    val threadPool = ThreadPool(10)
    val vector1 = testVector(1000)
    val vector2 = testVector(1000)
    val result = multiple(vector1, vector2, threadPool)
    val normalVector = vector1 multiple vector2
    println(result == normalVector)
    threadPool.interrupt()
}

private fun multiple(vector1: Vector<Int>, vector2: Vector<Int>, threadPool: ThreadPool): Vector<Int> =
    vector1
        .splitVectorIndexed(step = vector1.size / threadPool.size) { from, _, list ->
            threadPool.scheduleTask {
                list.mapIndexed { index, value -> value * vector2[from + index] }
            }
        }
        .fold(emptyList()) { acc, task -> acc + task.join() }
