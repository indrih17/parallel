package threads.exam.seidel

import koma.extensions.get
import koma.matrix.Matrix
import kotlinx.coroutines.coroutineScope
import threads.exam.jacobi.jacobi
import kotlin.math.pow
import kotlin.math.sqrt

/** @see jacobi */
suspend fun seidelParallel(
    matrix: Matrix<Double>,
    freeMembers: List<Double>,
    eps: Double
): List<Double> = coroutineScope {
    val size = matrix.numRows()
    val indices = 0 until size

    var previousValues = List(size) { 0.0 }
    var converge = false

    while (!converge) {
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
        converge = sqrt(sqrSum) <= eps
        previousValues = currentValues
    }
    return@coroutineScope previousValues
}