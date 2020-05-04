package threads

import threads.data.Vector

/**
 * Разбить вектор на подсписки размером [step] и трансформацией каждого списка в [R].
 * Если последний список меньше размером, то выдаётся таким.
 *
 * Пример:
 * vectorOf(1, 2, 3, 4, 5)
 *     .splitVector(step = 3) { it }
 *     .forEach { println(it) }
 *
 * Вывод результата:
 * [1, 2, 3]
 * [4, 5]
 *
 * @throws IllegalStateException если [step] <= 0 или [step] >= size.
 */
inline fun <T, R> Vector<T>.splitVector(step: Int, transform: (List<T>) -> R): Vector<R> =
    splitVectorIndexed(step) { _, _, vector -> transform(vector) }

/** То же самое, что и [splitVector], только [transform] ещё содержит индексы. */
inline fun <T, R> Vector<T>.splitVectorIndexed(
    step: Int,
    transform: (from: Int, to: Int, list: List<T>) -> R
): Vector<R> {
    assert(step > 0) { println("Шаг должен быть больше 0") }
    val result = ArrayList<R>(size / step)
    var index = 0
    while (index < size) {
        val fromIndex = index
        val toIndex = (index + step).takeIf { it <= size } ?: size
        result.add(transform(fromIndex, toIndex, subList(fromIndex, toIndex)))
        index += step
    }
    return result
}
