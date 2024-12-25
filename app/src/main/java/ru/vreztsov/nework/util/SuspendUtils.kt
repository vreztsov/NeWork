package ru.vreztsov.nework.util

interface SuspendSupplier<T> {
    suspend fun invoke(): T
}

interface SuspendReceiver<T> {
    suspend fun invoke(t: T)
}

class DefaultSuspendReceiver<T>(val receiver: (T) -> Unit): SuspendReceiver<T> {
    override suspend fun invoke(t: T) {
        receiver(t)
    }
}

class DefaultSuspendSupplier<T>(val supplier: () -> T): SuspendSupplier<T> {
    override suspend fun invoke(): T = supplier()
}