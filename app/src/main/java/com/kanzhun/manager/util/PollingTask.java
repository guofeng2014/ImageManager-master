package com.kanzhun.manager.util;


import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;


/**
 * Created by zhouyou on 2016/6/23.
 * 创建后台轮询线程
 */
public class PollingTask extends Thread {
    /**
     * 任务队列
     */
    private BlockingDeque<TaskRunnable> parserQueue;
    /**
     * 队列模式
     */
    private Type queueType;
    /**
     * 线程池
     */
    private ExecutorService threadPool;
    /**
     * 信号量
     */
    private Semaphore semaphore;
    /**
     * 手机屏幕显示的grid格子的数量
     */
    private int gridCount;

    public PollingTask(BlockingDeque<TaskRunnable> parserQueue, Type queueType, ExecutorService threadPool, Semaphore semaphore, int gridCount) {
        this.parserQueue = parserQueue;
        this.queueType = queueType;
        this.threadPool = threadPool;
        this.semaphore = semaphore;
        this.gridCount = gridCount;
    }

    public void quit() {
        //终端线程
        interrupt();
        //清空队列
        parserQueue.clear();
    }

    @Override
    public void run() {
        while (true) {
            TaskRunnable r;
            try {
                //检查队列长度
                checkLimitQueue();
                if (queueType == Type.FIFO) {
                    r = parserQueue.takeFirst();
                } else {
                    r = parserQueue.takeLast();
                }
            } catch (InterruptedException e) {
                return;
            }
            if (r != null) {
                handleImageDownload(r);
            }
        }
    }

    /**
     * 保持消息队列长度在一屏幕范围
     */
    private void checkLimitQueue() {
        while (parserQueue.size() > gridCount) {
            parserQueue.removeFirst();
        }
    }


    private void handleImageDownload(TaskRunnable runnable) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadPool.execute(runnable);
    }
}
