package threads.exam.seidel

import koma.extensions.get
import koma.matrix.Matrix
import threads.exam.jacobi.jacobi
import kotlin.math.pow
import kotlin.math.sqrt

/** @see jacobi */
fun seidel(matrix: Matrix<Double>, freeMembers: List<Double>, eps: Double): List<Double> {
    val size = matrix.numRows()
    val indices = 0 until size

    var previousValues = List(size) { 0.0 }
    var norm: Double

    do {
        val currentValues = freeMembers.mapIndexed { i, member ->
            val sum = indices
                .minus(i)
                .map { j -> matrix[i, j] * previousValues[j] }
                .sum()
            (member - sum) / matrix[i, i]
        }

        val sqrSum = previousValues
            .mapIndexed { index, prev -> (currentValues[index] - prev).pow(2) }
            .sum()
        norm = sqrt(sqrSum)
        previousValues = currentValues
    } while (norm > eps)
    return previousValues
}