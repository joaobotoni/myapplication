package com.example.myapplication.ui.helpers;

import android.os.Handler;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.inject.Inject;

public final class TaskHelper {

    private TaskHelper(){
        throw new AssertionError("TaskHelper is a utility class and must not be instantiated.");
    }
    private final ExecutorService executor;
    private final Handler handler;

    @Inject
    public TaskHelper(@NonNull ExecutorService executor, @NonNull Handler handler) {
        this.executor = executor;
        this.handler = handler;
    }

    public interface Cancellable {
        void cancel();
    }

    public static final class Cancellables {
        private final List<Cancellable> ativas = new ArrayList<>();

        @NonNull
        public Cancellable adicionar(@NonNull Cancellable cancellable) {
            ativas.add(cancellable);
            return cancellable;
        }

        public void cancelarTudo() {
            for (Cancellable c : ativas) c.cancel();
            ativas.clear();
        }
    }

    @NonNull
    public <T> Cancellable execute(
            @NonNull Callable<T> task,
            @NonNull Consumer<T> onSuccess,
            @NonNull Consumer<Exception> onError
    ) {
        AtomicBoolean cancelled = new AtomicBoolean(false);
        Future<?> future = executor.submit(() -> {
            try {
                T result = task.call();
                if (cancelled.get()) return;
                handler.post(() -> {
                    if (!cancelled.get()) onSuccess.accept(result);
                });
            } catch (Exception e) {
                if (cancelled.get()) return;
                handler.post(() -> {
                    if (!cancelled.get()) onError.accept(e);
                });
            }
        });
        return () -> {
            cancelled.set(true);
            future.cancel(true);
        };
    }
}
