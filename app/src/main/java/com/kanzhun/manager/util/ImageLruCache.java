package com.kanzhun.manager.util;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.kanzhun.manager.itfs.MemoryCache;

/**
 * Created by zhouyou on 2016/6/24.
 */
public class ImageLruCache implements MemoryCache {

    /**
     * 图片缓存的核心对象
     */
    private LruCache<String, Bitmap> mLruCache;

    public ImageLruCache() {
        // 获取应用的最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    @Override
    public void add(String path, Bitmap bitmap) {
        if (bitmap == null) return;
        if (get(path) == null) {
            mLruCache.put(path, bitmap);
        }
    }

    @Override
    public Bitmap get(String path) {
        return mLruCache.get(path);
    }

    @Override
    public void clear() {
        mLruCache.evictAll();
    }
}
