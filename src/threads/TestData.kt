package threads

import threads.data.Vector
import threads.data.matrix
import kotlin.random.Random

fun testMatrix(size: Int) = matrix(size) { _, _ -> Random.nextInt() }

fun testVector(size: Int): Vector<Int> = List(size) { Random.nextInt() }
