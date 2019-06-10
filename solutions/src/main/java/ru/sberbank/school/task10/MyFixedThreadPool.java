package ru.sberbank.school.task10;

import java.util.List;
import java.util.Queue;

/**
 * Created by Mart
 * 04.06.2019
 **/
public class MyFixedThreadPool extends MyScalableThreadPool {
    private List<Thread> pool;
    private Queue<Runnable> queue;

    public MyFixedThreadPool(int poolSize) {
        super(poolSize);
    }
}
