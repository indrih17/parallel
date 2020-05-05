package threads.exam

import koma.create
import koma.matrix.Matrix
import threads.exam.jacobi.jacobi
import threads.exam.jacobi.jacobiParallel
import threads.exam.seidel.seidel
import threads.exam.seidel.seidelParallel
import threads.jacobiConvergenceCondition
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

sealed class Methods(
    val single: (Matrix<Double>, List<Double>, Double) -> List<Double>,
    val multi: suspend (Matrix<Double>, List<Double>, Double) -> List<Double>
) {
    object Jacobi : Methods(single = ::jacobi, multi = ::jacobiParallel)
    object Seidel : Methods(single = ::seidel, multi = ::seidelParallel)

    companion object {
        val list: List<Methods> get() = listOf(Jacobi, Seidel)
    }
}

@OptIn(ExperimentalTime::class)
suspend fun main() {
    // Считываем размер вводимой матрицы
    val size = readlnWithPrint("Введите размер матрицы (максимум 4): ").toInt()
    println("Генерация матрицы...")

    // Будем хранить матрицу в векторе, состоящем из векторов вещественных чисел
    val (pair, duration) = measureTimedValue {
        createJacobiLinearSystem(size)
    }
    val (matrix, freeMembers) = pair
    println("Матрица:\n$matrix\n")
    println("Свободные члены: $freeMembers\n")
    println("Время подсчёта: $duration\n")

    // Считываем необходимую точность решения
    val eps = readlnWithPrint("Укажите точность вычислений: ").toDouble()

    // Выбираем метод вычислений
    val index = readlnWithPrint(
        """
        Выберите метод вычислений:
        1. Якоби
        2. Зейделя
        Ваш ответ (число): 
    """.trimIndent()
    ).toInt()
    val method = Methods.list[index - 1]

    // Выводим найденные значения неизвестных
    val singleResult = measureTimedValue { method.single(matrix, freeMembers, eps) }
    val multiResult = measureTimedValue { method.multi(matrix, freeMembers, eps) }
    println("----------------------------------------------------------------")
    if (singleResult.value == multiResult.value) {
        println("Single: ${singleResult.duration}, multi: ${multiResult.duration}")
        println("RESULT: ${singleResult.duration}")
    } else {
        error("Single result != multi result")
    }
}

private fun readlnWithPrint(text: String): String {
    print(text)
    return readLine() ?: error("Input == null")
}

private fun createJacobiLinearSystem(size: Int): Pair<Matrix<Double>, List<Double>> {
    while (true) {
        val (matrix, freeMem) = createLinearSystem(size)
        if (matrix.jacobiConvergenceCondition())
            return matrix to freeMem
    }
}

private fun createLinearSystem(size: Int): Pair<Matrix<Double>, List<Double>> {
    val xList = List(size) { Random.nextDouble() }
    val ratiosMatrix = xList
        .map { DoubleArray(size) { Random.nextDouble() } }
        .toTypedArray()
    val freeMembers = ratiosMatrix.map { ratios ->
        xList.mapIndexed { index, x -> ratios[index] * x }.sum()
    }
    return create(ratiosMatrix) to freeMembers
}

