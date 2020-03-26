package threads

import threads.data.Vector
import threads.data.matrix

/**
 * Пример:
 * threads.testMatrix(2)
 *
 * Результат:
 * [0, 1]
 * [2, 3]
 */
fun testMatrix(size: Int) = matrix(size) { i, j -> i + j }

/**
 * Пример:
 * threads.testVector(2)
 *
 * Результат:
 * [0, 1]
 */
fun testVector(size: Int): Vector<Int> = List(size) { it }
