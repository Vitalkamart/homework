package ru.sberbank.school.task10;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.stream.IntStream;

/**
 * Created by Mart
 * 04.06.2019
 **/
public class MyScalableThreadPool implements ThreadPool {
    private volatile int activeThreads;
    private final Object activeThreadsLock = new Object();
    private final int sizeMin;
    private final int sizeMax;
    private final Queue<Integer> freeIndexForNewWorker;
    private final Map<Integer, ThreadWorker> poolMap;
    private final Queue<FutureTask> tasks;

    public MyScalableThreadPool(int sizeMin, int sizeMax) {
        if (sizeMin <= 0 || sizeMax <= 0 || sizeMax < sizeMin) {
            throw new IllegalArgumentException("Incorrect input size");
        }

        this.sizeMin = sizeMin;
        this.sizeMax = sizeMax;
        this.activeThreads = 0;
        this.freeIndexForNewWorker = new LinkedList<>();
        this.poolMap = new HashMap<>();
        this.tasks = new LinkedList<>();
        fillPoolMap(sizeMin);
    }

    public MyScalableThreadPool(int fixedSize) {
        if (fixedSize <= 0) {
            throw new IllegalArgumentException("Incorrect input size");
        }
        this.sizeMin = fixedSize;
        this.sizeMax = fixedSize;
        this.activeThreads = 0;
        this.freeIndexForNewWorker = new LinkedList<>();
        this.poolMap = new HashMap<>();
        this.tasks = new LinkedList<>();
        fillPoolMap(fixedSize);
    }

    private void fillPoolMap(int size) {
        IntStream.range(0, size)
                .forEach(i -> poolMap.put(i, new ThreadWorker("ThreadWorker " + i, i)));

        IntStream.range(size, sizeMax)
                .forEach(freeIndexForNewWorker::add);
    }

    @Override
    public void start() {
        poolMap.forEach((key, worker) -> {
            worker.start();

            synchronized (activeThreadsLock) {
                activeThreads++;
            }
        });
    }


    @Override
    public void stopNow() {
        synchronized (poolMap) {
            poolMap.forEach((key, worker) -> {
                worker.interrupt();
            });
            poolMap.clear();
            activeThreads = 0;
            freeIndexForNewWorker.clear();
            tasks.clear();
        }
    }




    @Override
    public void execute (Runnable runnable){
        synchronized (tasks) {
            tasks.add(new FutureTask(runnable, true));
            tasks.notifyAll();
        }
    }

    @Override
    public <T > Future < T > execute(Callable < T > callable) {
        synchronized (tasks) {
            FutureTask<T> futureTask = new FutureTask(callable);
            tasks.add(futureTask);
            tasks.notifyAll();
            return futureTask;
        }
    }

    /*
    *return true if needed to interrupt, otherwise return false
    *  */
    private boolean poolResize () {
        int taskSize = tasks.size() + 1; // + 1 because we deed poll before resize method

        synchronized (poolMap) {
            poolMap.forEach((key, worker) -> {

                if (worker == null || worker.isInterrupted()) {
                    worker = null;
                    if (!freeIndexForNewWorker.contains(key)) {
                        freeIndexForNewWorker.offer(key);
                    }

                    synchronized (activeThreadsLock) {
                        activeThreads--;
                    }
                }
            });

            if (taskSize > sizeMin && activeThreads < sizeMax) {
                synchronized (activeThreadsLock) {
                    activeThreads++;
                }

                if (!freeIndexForNewWorker.isEmpty()) {
                    int id = freeIndexForNewWorker.poll();

                    ThreadWorker worker =new ThreadWorker("ThreadWorker " + id, id);
                    poolMap.put(id, worker);
                    worker.start();
                }
            }

            return taskSize < activeThreads && activeThreads > sizeMin;
        }
    }

    public int getActiveThreads () {
        return activeThreads;
    }

    private class ThreadWorker extends Thread {
        private int id;

        private ThreadWorker(String name, int id) {
            super(name);
            this.id = id;
        }

        @Override
        public void run() {
            System.out.println("ThreadWorker "+ id + " started");
            while (!Thread.currentThread().isInterrupted()) {

                synchronized (tasks) {
                    while (tasks.isEmpty()) {
                        try {
                            tasks.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }

                    if (!Thread.currentThread().isInterrupted()) {
                        FutureTask task = tasks.poll();
                        boolean needToInterrupt = poolResize();
                        if (task != null) {
                            task.run();
                            System.out.println("ThreadWorker "+ id + " ended");
                        }
                        if (needToInterrupt) {
                            System.out.println("ThreadWorker "+ id + " interrupted");
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }

        @Override
        public String toString() {
            return "ThreadWorker " + id;
        }
    }
}

