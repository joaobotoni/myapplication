package com.example.myapplication.ui.helpers;

import android.os.Handler;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import javax.inject.Inject;

public final class TaskHelper {

    private final ExecutorService executor;
    private final Handler handler;

    @Inject
    public TaskHelper(@NonNull ExecutorService executor, @NonNull Handler handler) {
        this.executor = executor;
        this.handler = handler;
    }

    public <T> void execute(
            @NonNull Callable<T> task,
            @NonNull Consumer<T> onSuccess,
            @NonNull Consumer<Exception> onError
    ) {
        executor.execute(() -> {
            try {
                T result = task.call();
                handler.post(() -> onSuccess.accept(result));
            } catch (Exception e) {
                handler.post(() -> onError.accept(e));
            }
        });
    }
    public void cancelAll() {
        executor.shutdownNow();
    }
}
