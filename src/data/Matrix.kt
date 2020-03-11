package data

/**
 * Создание квадратной матрицы размера [size].
 * @param init лямбда инициализации, которая должна выдавать значение с учётом индексов i и j.
 */
inline fun <T> matrix(size: Int, init: (i: Int, j: Int) -> T): Matrix<T> =
    Matrix(MutableList(size) { i -> MutableList(size) { j -> init(i, j) } })

/** Создание матрицы на основе неопределённого количества списка элементов. */
fun <T> matrixOf(vararg lists: List<T>): Matrix<T> =
    Matrix(lists.map { it.toMutableList() })

/**
 * Матрица элементов, внутри использует списки списков, которые внутри используют [ArrayList].
 */
inline class Matrix<T>(private val matr: List<MutableList<T>>) {
    /** Размер матрицы. */
    val size: Int get() = matr.size

    /**
     * Пример использования:
     * matrix[i, j] = newValue
     */
    operator fun set(i: Int, j: Int, elem: T) {
        matr[i][j] = elem
    }

    /**
     * Пример использования:
     * val elem = matrix[i, j]
     */
    operator fun get(i: Int, j: Int): T = matr[i][j]

    /**
     * В матрице
     * [1, 2]
     * [3, 4]
     * по индексу [index] = 2 результат: [3, 4].
     */
    fun getRowVector(index: Int): Vector<T> =
        vector(size) { matr[index][it] }

    /**
     * В матрице
     * [1, 2]
     * [3, 4]
     * по индексу [index] = 1 результат: [1, 3].
     */
    fun getColumnVector(index: Int): Vector<T> =
        vector(size) { matr[it][index] }

    /**
     * Пример результата:
     * [1, 2]
     * [3, 4]
     */
    override fun toString(): String =
        matr.joinToString(separator = "\n") { list -> list.toString() }
}

/** Умножение матрицы на вектор. Результатом является вектор. */
infix fun Matrix<Int>.multiple(vector: Vector<Int>): Vector<Int> =
    vector(vector.size) { index ->
        getColumnVector(index).multiple(vector).sum()
    }

/** Умножение матрицы на матрицу. */
infix fun Matrix<Int>.multiple(other: Matrix<Int>): Matrix<Int> =
    matrix(size) { i, j ->
        val row = this.getRowVector(i)
        val column = other.getColumnVector(j)
        row.multiple(column).sum()
    }

/** Поэлементная трансформация одной матрицы в другую. */
fun <T, R> Matrix<T>.map(transform: (T) -> R): Matrix<R> =
    matrix(size) { i, j -> transform(get(i, j)) }
