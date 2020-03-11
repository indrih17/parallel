package data

import java.util.concurrent.ArrayBlockingQueue

/**
 * Задача, которую мы можем запустить и дождаться её выполнения.
 */
class Task<T>(private val block: () -> T) {
    /**
     * Примитив синхронизации. Через него будут общаться выплняющий и ожидающий потоки.
     */
    private val lock = ArrayBlockingQueue<T>(1)

    /**
     * Ожидающий поток вызывает этот метод и блокируется, пока не дождётся результата.
     */
    fun join(): T = lock.take()

    /**
     * Выполняющий поток вызывает этот метод и запускает задачу, результат поступает в очередь.
     */
    operator fun invoke() = lock.put(block())
}