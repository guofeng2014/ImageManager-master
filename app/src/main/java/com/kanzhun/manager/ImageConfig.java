package com.kanzhun.manager;

import android.graphics.Bitmap;

import com.kanzhun.manager.itfs.MemoryCache;
import com.kanzhun.manager.util.ImageLruCache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
     * 线程池
     */
    private ExecutorService threadPool;
    /**
     * 图片的压缩质量参数
     */
    private Bitmap.Config imageConfig = null;

    public ImageConfig(Builder builder) {
        this.defaultResourceId = builder.getDefaultResourceId();
        this.errorResourceId = builder.getErrorResourceId();
        this.threadCount = builder.getThreadCount() == 0 ? 3 : builder.getThreadCount();
        this.mLruCache = builder.getmLruCache() == null ? new ImageLruCache() : builder.getmLruCache();
        this.threadPool = Executors.newFixedThreadPool(getThreadCount());
        this.imageConfig = builder.getImageConfig() == null ? Bitmap.Config.ARGB_8888 : builder.getImageConfig();
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


    public Bitmap.Config getImageConfig() {
        return imageConfig;
    }

    public static class Builder {
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
         * 图片的压缩质量参数
         */
        private Bitmap.Config imageConfig;


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


        public Bitmap.Config getImageConfig() {
            return imageConfig;
        }

        public Builder setImageConfig(Bitmap.Config imageConfig) {
            this.imageConfig = imageConfig;
            return this;
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


        public ImageConfig build() {
            return new ImageConfig(this);
        }
    }
}
