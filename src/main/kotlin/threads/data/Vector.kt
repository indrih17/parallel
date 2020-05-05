package threads.data

/** Просто чтобы писать [Vector] вместо [List]. Они взаимозаменяемы (ну почти). */
typealias Vector<T> = List<T>

/**
 * Создание вектора размера [size].
 * @param init лямбда инициализации, которая должна выдавать значение с учётом индекса.
 */
inline fun <T> vector(size: Int, init: (Int) -> T): Vector<T> = List(size, init)

/** Создание вектора на основе неопределённого количества элементов. */
fun <T> vectorOf(vararg elems: T): Vector<T> = List(elems.size) { elems[it] }

/** Умножить вектор на число. Результатом является вектор. */
infix fun Vector<Int>.multiple(number: Int): Vector<Int> = map { it * number }

/** Умножение вектора на вектор. Результатом является вектор. */
infix fun Vector<Int>.multiple(other: Vector<Int>): Vector<Int> =
    mapIndexed { index, value -> value * other[index] }

/** Деление вектора на вектор. Результатом является вектор. */
infix fun Vector<Double>.devDouble(other: Vector<Double>): Vector<Double> =
    mapIndexed { index, value -> value / other[index] }

/** Деление вектора на вектор. Результатом является вектор. */
infix fun Vector<Int>.devInt(other: Vector<Int>): Vector<Int> =
    mapIndexed { index, value -> value / other[index] }