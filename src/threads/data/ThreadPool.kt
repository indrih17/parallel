package threads.data

import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

/**
 * Содержит внутри себя очередь задач и список потоков.
 * Создаются потоки, каждый из них блокируется и ждёт задачу из очереди.
 * Таска поступает в очередь, свободный поток (или кто первый успеет, если таковых несколько)
 * забирает задачу и выполняет ёё, а затем снова блокируется в ожидании задачи.
 */
class ThreadPool(val size: Int) {
    private val taskQueue = LinkedBlockingQueue<Task<*>>()
    private val threadList = MutableList(size) {
        thread(start = true) {
            try {
                while (!Thread.currentThread().isInterrupted) {
                    taskQueue.take().invoke()
                }
            } catch (e: InterruptedException) {
            }
        }
    }

    /** Создать задачу и положить её в очередь. */
    fun <T> scheduleTask(block: () -> T): Task<T> =
        Task(block).also(::schedule)

    /** Отправить задачу в очередь. */
    fun <T> schedule(task: Task<T>) =
        taskQueue.put(task)

    /** Прервать выполнение всех потоков и отчистить ресурсы. */
    fun interrupt() {
        threadList.forEach(Thread::interrupt)
        threadList.clear()
        taskQueue.clear()
    }
}
