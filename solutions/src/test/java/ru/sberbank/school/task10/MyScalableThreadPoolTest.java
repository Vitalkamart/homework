package ru.sberbank.school.task10;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;

class MyScalableThreadPoolTest {
    private MyScalableThreadPool pool;

    private void fillPool() {
        pool = new MyScalableThreadPool(4, 10);
    }

    private void fillTasks() {
        Runnable runnable = () -> {
            System.out.println(Thread.currentThread() + " running");
        };

        Callable<Integer> callable = () -> {
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
    void start() {
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
    void stopNow() {
        fillPool();
        fillTasks();
        pool.start();
        pool.stopNow();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(0, pool.getActiveThreads());
    }

    @Test
    void execute() {

    }

    @Test
    void execute1() {
    }
}