package threads.labs

import threads.data.ThreadPool
import threads.data.Vector
import threads.data.multiple
import threads.testVector
import kotlin.system.measureTimeMillis

fun main() {
    val threadPool = ThreadPool(1000)
    val vector = testVector(10000000)
    val number = 10323124
    val multiTime = measureTimeMillis {
        multiple(vector, number = number, threadPool = threadPool)
    }
    val singleTime = measureTimeMillis {
        vector multiple number
    }
    println("Single: $singleTime, multi: $multiTime")
    threadPool.interrupt()
}

private fun multiple(vector: Vector<Int>, number: Int, threadPool: ThreadPool): Vector<Int> =
    vector
        .chunked(vector.size / threadPool.size) { list ->
            val copy = list.toList()
            threadPool.scheduleTask { copy.map { it * number } }
        }
        .fold(emptyList()) { acc, task -> acc + task.join() }

