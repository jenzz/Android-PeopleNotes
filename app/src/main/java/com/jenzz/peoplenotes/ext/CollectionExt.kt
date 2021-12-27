package com.jenzz.peoplenotes.ext

inline fun <T, K> Collection<T>.checkUnique(
    selector: (T) -> K,
    lazyMessage: () -> Any,
): Collection<T> {
    check(size == distinctBy(selector).size, lazyMessage)
    return this
}
