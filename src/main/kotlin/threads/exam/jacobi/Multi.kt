package threads.exam.jacobi

import koma.extensions.get
import koma.matrix.Matrix
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.math.abs

/** @see jacobi */
suspend fun jacobiParallel(
    matrix: Matrix<Double>,
    freeMembers: List<Double>,
    eps: Double
): List<Double> = coroutineScope {
    val size = matrix.numRows()
    val indices = 0 until size

    // Введем вектор значений неизвестных на предыдущей итерации,
    // размер которого равен числу строк в матрице, т.е. size,
    // причем согласно методу изначально заполняем его нулями.
    var previousValues = List(size) { 0.0 }

    // Погрешность относительно предыдущей итерации
    var norm: Double

    // Будем выполнять итерационный процесс до тех пор,
    // пока не будет достигнута необходимая точность
    do {
        // Посчитаем значения неизвестных на текущей итерации
        // в соответствии с теоретическими формулами
        val currentValues = freeMembers
            .mapIndexed { i, member ->
                async {
                    val sum = indices
                        .minus(i)
                        .map { j -> matrix[i, j] * previousValues[j] }
                        .sum()
                    (member - sum) / matrix[i, i]
                }
            }
            .awaitAll()

        // Посчитаем текущую погрешность относительно предыдущей итерации
        norm = previousValues
            .mapIndexed { index, prev -> abs(currentValues[index] - prev) }
            .max()
            ?.also { check(it.isFinite()) }
            ?: error("Previous values is empty")

        // Переходим к следующей итерации, так что текущие значения неизвестных
        // становятся значениями на предыдущей итерации
        previousValues = currentValues
    } while (norm > eps)
    return@coroutineScope previousValues
}