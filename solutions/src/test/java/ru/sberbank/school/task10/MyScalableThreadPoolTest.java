package ru.sberbank.school.task10;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import static org.junit.jupiter.api.Assertions.*;

class MyScalableThreadPoolTest {
    private MyScalableThreadPool pool;

    private void fillPool() {
        pool = new MyScalableThreadPool(4, 10);
    }

    private void fillTasks() {
        Runnable runnable = () -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread() + " running");
        };

        Callable<Integer> callable = () -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread() + " running");
            return null;
        };

        for (int i = 0; i < 10; i++) {
            pool.execute(runnable);
        }

        for (int i = 0; i < 10; i++) {
            pool.execute(callable);
        }
    }

    @Test
    @DisplayName("start() method")
    void start() {
        fillPool();
        pool.start();
        assertEquals(4, pool.getActiveThreads());
    }

    @Test
    @DisplayName("adding new ThreadWorkers if need")
    void addNewThreads() {
        fillPool();
        fillTasks();
        pool.start();
        fillTasks();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(10, pool.getActiveThreads());
    }

    @Test
    @DisplayName("deleting ThreadWorkers if need")
    void deleteThreads() {
        pool = new MyScalableThreadPool(1, 2);
        pool.start();
        fillTasks();

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(1, pool.getActiveThreads());
    }

    @Test
    @DisplayName("stopNow() method")
    void stopNow() {
        fillPool();
        fillTasks();
        pool.start();
        pool.stopNow();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(0, pool.getActiveThreads());
    }

    @Test
    @DisplayName("execute(Runnable) method")
    @SuppressWarnings("unchecked")
    void executeRunnable() {
        fillPool();
        fillTasks();
        pool.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread() + " running");
            }
        });

        try {
            Field field = pool.getClass().getDeclaredField("tasks");
            Queue<FutureTask> tasks = null;
            boolean changedAccess = false;
            if (!field.isAccessible()) {
                changedAccess = true;
                field.setAccessible(true);
            }
            try {
                tasks = (Queue<FutureTask>) field.get(pool);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (changedAccess) {
                field.setAccessible(false);
            }
            int taskSize = tasks.size();

            assertEquals(21, taskSize);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("execute(Callable) method")
    void executeCallable() {
        fillPool();
        fillTasks();
        pool.execute(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                System.out.println(Thread.currentThread() + " running");
                return null;
            }
        });

        try {
            Field field = pool.getClass().getDeclaredField("tasks");
            Queue<FutureTask> tasks = null;
            boolean changedAccess = false;
            if (!field.isAccessible()) {
                changedAccess = true;
                field.setAccessible(true);
            }
            try {
                tasks = (Queue<FutureTask>) field.get(pool);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (changedAccess) {
                field.setAccessible(false);
            }
            int taskSize = tasks.size();

            assertEquals(21, taskSize);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}