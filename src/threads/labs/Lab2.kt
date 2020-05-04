package threads.labs

import threads.data.ThreadPool
import threads.data.Vector
import threads.data.multiple
import threads.splitVectorIndexed
import threads.testVector
import kotlin.system.measureTimeMillis

fun main() {
    val threadPool = ThreadPool(10)
    val vector1 = testVector(10000000)
    val vector2 = testVector(10000000)
    val multiTime = measureTimeMillis {
        multiple(vector1, vector2, threadPool)
    }
    val singleTime = measureTimeMillis {
        vector1 multiple vector2
    }
    println("Single: $singleTime, multi: $multiTime")
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
