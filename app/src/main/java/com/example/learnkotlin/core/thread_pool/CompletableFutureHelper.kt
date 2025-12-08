package com.example.learnkotlin.core.thread_pool

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.learnkotlin.core.logger.BaseLog
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Base class helper cho các trường hợp CompletableFuture
 */
class CompletableFutureHelper(private val context: Context) {

    private val mainExecutor: Executor = ContextCompat.getMainExecutor(context)
    private val backgroundExecutor: Executor =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    // ------------------------
    // 1️⃣ Chạy song song nhiều task
    // ------------------------
    fun <T> runParallel(vararg tasks: () -> T): List<CompletableFuture<T>> {
        return tasks.map { task ->
            CompletableFuture.supplyAsync(task, backgroundExecutor)
        }
    }

    // ------------------------
    // 2️⃣ Chạy tuần tự nhiều task
    // ------------------------
    fun <T> runSequential(vararg tasks: () -> T): CompletableFuture<List<T>> {
        var future: CompletableFuture<List<T>> = CompletableFuture.completedFuture(emptyList())
        tasks.forEach { task ->
            future = future.thenCompose { results ->
                CompletableFuture.supplyAsync({
                    val result = task()
                    results + result
                }, backgroundExecutor)
            }
        }
        return future
    }

    // ------------------------
    // 3️⃣ Chờ tất cả task xong
    // ------------------------
    fun allOf(vararg futures: CompletableFuture<*>): CompletableFuture<Void> {
        return CompletableFuture.allOf(*futures)
    }

    // ------------------------
    // 4️⃣ Chờ tất cả xong + gom kết quả
    // ------------------------
    fun <T> allOfCollect(vararg futures: CompletableFuture<T>): CompletableFuture<List<T>> {
        return CompletableFuture.allOf(*futures).thenApply {
            futures.map { it.get() }
        }
    }

    // ------------------------
    // 5️⃣ Xử lý lỗi riêng từng task
    // ------------------------
    fun <T> handleIndividualErrors(task: () -> T, fallback: T? = null): CompletableFuture<T> {
        return CompletableFuture.supplyAsync(task, backgroundExecutor)
            .exceptionally { e ->
                BaseLog.e("TAG", "Task failed: ${e.message}")
                fallback
            }
    }

    // ------------------------
    // 6️⃣ Xử lý lỗi toàn nhóm
    // ------------------------
    fun <T> allOfWithGroupError(vararg futures: CompletableFuture<T>): CompletableFuture<Void> {
        return CompletableFuture.allOf(*futures)
            .exceptionally { e ->
                BaseLog.e("TAG", "Group failed: ${e.cause?.message}")
                null
            }
    }

    // ------------------------
    // 7️⃣ Fail-fast: lỗi task nào -> báo ngay
    // ------------------------
    fun <T> failFast(vararg futures: CompletableFuture<T>): CompletableFuture<Void> {
        val result = CompletableFuture<Void>()
        futures.forEach { f ->
            f.whenComplete { _, ex ->
                if (ex != null) {
                    futures.forEach { it.cancel(true) } // hủy task còn lại
                    result.completeExceptionally(ex)
                }
                if (futures.all { it.isDone && !it.isCompletedExceptionally }) {
                    result.complete(null)
                }
            }
        }
        return result
    }

    // ------------------------
    // 8️⃣ Lấy kết quả task nào hoàn tất trước
    // ------------------------
    fun <T> anyOf(vararg futures: CompletableFuture<T>): CompletableFuture<Any> {
        return CompletableFuture.anyOf(*futures)
    }

    // ------------------------
    // 9️⃣ Callback về Main Thread
    // ------------------------
    fun <T> thenOnMain(future: CompletableFuture<T>, action: (T) -> Unit) {
        future.thenAcceptAsync(action, mainExecutor)
    }

    // ------------------------
    // 10 Tiện ích: gom danh sách task + callback Main Thread
    // ------------------------
    fun <T> allOfCollectThenMain(
        vararg futures: CompletableFuture<T>,
        action: (List<T>) -> Unit
    ) {
        allOfCollect(*futures).thenAcceptAsync(action, mainExecutor)
    }

    // ------------------------
    // 11 Xử lý từng task + gom kết quả về Main
    // ------------------------
    fun <T> handleEachThenMain(
        vararg tasks: () -> T,
        fallback: T? = null,
        action: (List<T>) -> Unit
    ) {
        val futures = tasks.map { handleIndividualErrors(it, fallback) }.toTypedArray()
        allOfCollectThenMain(*futures, action = action)
    }

    // ------------------------
    // 🔹 Fail-fast + callback Main
    // ------------------------
    fun <T> failFastThenMain(
        vararg futures: CompletableFuture<T>,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        failFast(*futures).thenRunAsync({
            onSuccess()
        }, mainExecutor).exceptionally { e ->
            onError(e.cause ?: e)
            null
        }
    }

    // ------------------------
    // 10️⃣ Timeout helper
    // ------------------------
    fun <T> withTimeout(
        future: CompletableFuture<T>,
        timeout: Long,
        unit: TimeUnit,
        fallback: T? = null
    ): CompletableFuture<T> {
        val timeoutFuture = CompletableFuture.supplyAsync({
            Thread.sleep(unit.toMillis(timeout))
            fallback
        }, backgroundExecutor)
        return CompletableFuture.anyOf(future, timeoutFuture).thenApply { it as T }
    }

    // ------------------------
    // 14️⃣ Retry helper
    // ------------------------
    fun <T> retry(
        task: () -> T,
        retries: Int = 3,
        delayMillis: Long = 100
    ): CompletableFuture<T> {
        return CompletableFuture.supplyAsync(task, backgroundExecutor).handleAsync { result, ex ->
            if (ex == null) return@handleAsync result
            if (retries <= 0) throw ex
            Thread.sleep(delayMillis)
            retry(task, retries - 1, delayMillis).get()
        }
    }

    // ------------------------
    // 15️⃣ Progress / status callback
    // ------------------------
    fun <T> withProgress(
        task: () -> T,
        onProgress: (status: String) -> Unit
    ): CompletableFuture<T> {
        return CompletableFuture.supplyAsync({
            onProgress("Started")
            val result = task()
            onProgress("Completed")
            result
        }, backgroundExecutor)
    }

    // ------------------------
    // 16️⃣ Combine 2 task
    // ------------------------
    fun <A, B, R> combineTasks(
        futureA: CompletableFuture<A>,
        futureB: CompletableFuture<B>,
        combiner: (A, B) -> R
    ): CompletableFuture<R> {
        return futureA.thenCombine(futureB, combiner)
    }

    // ------------------------
    // 17️⃣ Run after both / either
    // ------------------------
    fun <A, B> runAfterBoth(
        futureA: CompletableFuture<A>,
        futureB: CompletableFuture<B>,
        action: () -> Unit
    ) {
        futureA.runAfterBoth(futureB, action)
    }

    fun <A, B> runAfterEither(
        futureA: CompletableFuture<A>,
        futureB: CompletableFuture<B>,
        action: () -> Unit
    ) {
        futureA.runAfterEither(futureB, action)
    }

    // ------------------------
    // 18️⃣ Dynamic task list: thêm task trong khi group đang chạy
    // ------------------------
    fun <T> dynamicTasks(tasks: MutableList<() -> CompletableFuture<T>>): CompletableFuture<List<T>> {
        val futures = tasks.map { it() }
        return CompletableFuture.allOf(*futures.toTypedArray())
            .thenApply { futures.map { it.get() } }
    }

}