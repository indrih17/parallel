package threads

import koma.extensions.map
import koma.extensions.mapIndexed
import koma.matrix.Matrix
import threads.data.Vector
import kotlin.math.abs

/** @see chunked */
inline fun <T, R> Collection<T>.chunkedIndexed(
    size: Int,
    crossinline transform: (from: Int, to: Int, list: List<T>) -> R
): List<R> {
    val collSize = this.size
    var index = 0
    return chunked(size) { list ->
        val fromIndex = index
        index += size
        val toIndex = (fromIndex + size).takeIf { it <= collSize } ?: collSize
        transform(fromIndex, toIndex, list)
    }
}

fun <T> Matrix<T>.getRowVector(index: Int): Vector<T> =
    getRow(index).toList()

fun <T> Matrix<T>.getColumnVector(index: Int): Vector<T> =
    getCol(index).toList()

fun Matrix<Double>.jacobiConvergenceCondition(): Boolean {
    val diag = diagonalMatrix()
    val bMatrix = diag.inv().neg() * (this - diag)
    return bMatrix.infNorm() < 1
}

fun Matrix<Double>.neg(): Matrix<Double> =
    map { elem -> -elem }

fun Matrix<Double>.infNorm(): Double =
    to2DArray()
        .map { arr -> arr.map(::abs).sum() }
        .max()
        ?: error("Norm not found.")

fun Matrix<Double>.diagonalMatrix(): Matrix<Double> =
    diagonalMatrix(defaultValue = 0.0)

fun <T> Matrix<T>.diagonalMatrix(defaultValue: T): Matrix<T> =
    mapIndexed { row, col, elem -> if (row == col) elem else defaultValue }
