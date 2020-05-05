package threads

import koma.matrix.Matrix
import koma.matrix.MatrixTypes
import koma.rand
import threads.data.Vector
import kotlin.random.Random

fun testMatrix(size: Int): Matrix<Int> = rand(size, size, MatrixTypes.IntType)

fun testVector(size: Int): Vector<Int> = List(size) { Random.nextInt() }
