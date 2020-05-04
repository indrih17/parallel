package threads.labs

import java.util.concurrent.ArrayBlockingQueue
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
    val group = ThreadGroup("thread_group")
    val threadsNumber = 20

    val successQueue = ArrayBlockingQueue<Int>(1)
    val failQueue = ArrayBlockingQueue<Int>(threadsNumber)

    val result = ArrayBlockingQueue<Int>(1)
    val array = IntArray(1000000000) { Random.nextInt() }
    val elem = array.last()

    val multiTime = measureTimeMillis {
        array.splitIndexed(step = array.size / threadsNumber) { from, _, msg ->
            Thread(group) {
                val index = msg.indexOf(elem)
                if (index != -1) successQueue.add(from + index) else failQueue.add(index)
            }.start()
        }

        Thread(group) {
            try {
                result.put(successQueue.take())
            } catch (e: InterruptedException) {}
        }.start()

        Thread(group) {
            try {
                for (i in 0 until threadsNumber) failQueue.take()
            } catch (e: InterruptedException) {}
            result.put(-1)
        }.start()

        println("Result: ${result.take()}")
    }

    val singleTime = measureTimeMillis { array.indexOf(elem) }
    println("Single: $singleTime, multi: $multiTime")
    group.interrupt()
}

private inline fun <R> IntArray.splitIndexed(
    step: Int,
    transform: (from: Int, to: Int, msg: IntArray) -> R
): List<R> {
    check(step > 0) { "Шаг должен быть больше 0" }
    val result = ArrayList<R>(size / step)
    var index = 0
    while (index < size) {
        val fromIndex = index
        val toIndexExclusive = (index + step).takeIf { it <= size } ?: size
        result.add(transform(fromIndex, toIndexExclusive, copyOfRange(fromIndex, toIndexExclusive)))
        index += step
    }
    return result
}

