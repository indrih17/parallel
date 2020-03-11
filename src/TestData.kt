import data.Vector
import data.matrix

/**
 * Пример:
 * testMatrix(2)
 *
 * Результат:
 * [0, 1]
 * [2, 3]
 */
fun testMatrix(size: Int) = matrix(size) { i, j -> i + j }

/**
 * Пример:
 * testVector(2)
 *
 * Результат:
 * [0, 1]
 */
fun testVector(size: Int): Vector<Int> = List(size) { it }
