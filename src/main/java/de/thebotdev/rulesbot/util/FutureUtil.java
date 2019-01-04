package de.thebotdev.rulesbot.util;

import java.util.concurrent.CompletableFuture;

public class FutureUtil {
    public static <T> CompletableFuture<T> reject(Throwable t) {
        CompletableFuture<T> fut = new CompletableFuture<>();
        fut.completeExceptionally(t);
        return fut;
    }

    public static <T> CompletableFuture<T> resolve(T result) {
        CompletableFuture<T> fut = new CompletableFuture<>();
        fut.complete(result);
        return fut;
    }
}
