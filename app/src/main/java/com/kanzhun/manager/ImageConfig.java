package com.kanzhun.manager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

import com.kanzhun.manager.itfs.MemoryCache;
import com.kanzhun.manager.util.ImageLruCache;
import com.kanzhun.manager.util.TaskRunnable;
import com.kanzhun.manager.util.Type;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

/**
 * 作者：guofeng
 * 日期:16/6/27
 * 图片配置信息
 */
public final class ImageConfig {

    /**
     * 图片加载默认显示的图片
     */
    private int defaultResourceId;
    /**
     * 图片加载失败显示的图片
     */
    private int errorResourceId;
    /**
     * 线程数量
     */
    private int threadCount;
    /**
     * 缓存的容器
     */
    private MemoryCache mLruCache;
    /**
     * 队列加载数据类型(FIFO),FILO)
     */
    private Type queueType;
    /**
     * 线程池
     */
    private ExecutorService threadPool;
    /**
     * 解析队列
     */
    private BlockingDeque<TaskRunnable> parserQueue = new LinkedBlockingDeque<>();
    /**
     * 信号量
     */
    private Semaphore semaphore;
    /**
     * 一屏幕显示的grid的数量
     */
    private int gridCount;
    /**
     * 图片的压缩质量参数
     */
    private Bitmap.Config imageConfig = null;

    public ImageConfig(Builder builder) {
        this.defaultResourceId = builder.getDefaultResourceId();
        this.errorResourceId = builder.getErrorResourceId();
        this.threadCount = builder.getThreadCount() == 0 ? 3 : builder.getThreadCount();
        this.mLruCache = builder.getmLruCache() == null ? new ImageLruCache() : builder.getmLruCache();
        this.queueType = builder.getQueueType();
        this.threadPool = Executors.newFixedThreadPool(getThreadCount());
        this.semaphore = new Semaphore(getThreadCount());
        this.gridCount = builder.getGridCount();
        this.imageConfig = builder.getImageConfig() == null ? Bitmap.Config.ARGB_8888 : builder.getImageConfig();
    }

    public Type getQueueType() {
        return queueType;
    }

    public int getDefaultResourceId() {
        return defaultResourceId;
    }

    public int getErrorResourceId() {
        return errorResourceId;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public MemoryCache getmLruCache() {
        return mLruCache;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public BlockingDeque<TaskRunnable> getParserQueue() {
        return parserQueue;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public int getGridCount() {
        return gridCount;
    }

    public Bitmap.Config getImageConfig() {
        return imageConfig;
    }

    public static class Builder {
        /**
         * 当前上下文
         */
        private Context context;
        /**
         * 图片加载默认显示的图片
         */
        private int defaultResourceId;
        /**
         * 图片加载失败显示的图片
         */
        private int errorResourceId;
        /**
         * 线程数量
         */
        private int threadCount;
        /**
         * 缓存的容器
         */
        private ImageLruCache mLruCache;
        /**
         * 队列加载数据类型(FIFO),LIFO)
         */
        private Type queueType;
        /**
         * 图片的压缩质量参数
         */
        private Bitmap.Config imageConfig;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setDefaultResourceId(int defaultResourceId) {
            this.defaultResourceId = defaultResourceId;
            return this;
        }

        public Builder setErrorResourceId(int errorResourceId) {
            this.errorResourceId = errorResourceId;
            return this;
        }

        public Builder setThreadCount(int threadCount) {
            this.threadCount = threadCount;
            return this;
        }

        public Builder setmLruCache(ImageLruCache mLruCache) {
            this.mLruCache = mLruCache;
            return this;
        }

        public Builder setQueueType(Type queueType) {
            this.queueType = queueType;
            return this;
        }

        public Bitmap.Config getImageConfig() {
            return imageConfig;
        }

        public Builder setImageConfig(Bitmap.Config imageConfig) {
            this.imageConfig = imageConfig;
            return this;
        }

        public Type getQueueType() {
            return queueType;
        }

        public int getDefaultResourceId() {
            return defaultResourceId;
        }

        public int getErrorResourceId() {
            return errorResourceId;
        }

        public int getThreadCount() {
            return threadCount;
        }

        public ImageLruCache getmLruCache() {
            return mLruCache;
        }


        public int getGridCount() {
            Resources res = context.getResources();
            DisplayMetrics metrics = res.getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            int gridSize = width / 3;
            int lines = height / gridSize;
            return lines * 3+6;
        }

        public ImageConfig build() {
            return new ImageConfig(this);
        }
    }
}
