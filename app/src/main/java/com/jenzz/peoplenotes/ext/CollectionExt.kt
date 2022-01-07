package com.jenzz.peoplenotes.ext

inline fun <T, K> Collection<T>.checkNoDuplicates(
    crossinline selector: (T) -> K,
    lazyMessage: (Set<K>) -> Any,
): Collection<T> {
    val duplicates = groupingBy(selector).eachCount().filterValues { count -> count > 1 }.keys
    check(duplicates.isEmpty()) { lazyMessage(duplicates) }
    return this
}
