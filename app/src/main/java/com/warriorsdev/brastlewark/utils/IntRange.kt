package com.warriorsdev.brastlewark.utils

/**
 * Checks if a given value is within an [IntRange]
 * @param value Value to check if is within a range.
 */
fun IntRange.has(value: Number) = value.toInt() in this

/**
 * Retrieves a range from a given List.
 * @param sorterBlock Function that must return the value for how the list must be sorted.
 * @param rangeBlock Function that must return an [IntRange],
 * it passes the first and the last elements of the sorted list as a [Pair].
 */
fun <T> List<T>.getRangeFrom(
    sorterBlock: (element: T) -> Int,
    rangeBlock: (sortResult: Pair<T, T>) -> IntRange
): IntRange {
    val sort = this.sortedBy { sorterBlock(it) }
    return rangeBlock(sort.first() to sort.last())
}