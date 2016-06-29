package com.kanzhun.manager.itfs;

import android.graphics.Bitmap;

/**
 * 作者：guofeng
 * 日期:16/6/27
 */
public interface MemoryCache {
    /**
     * 添加bitmap到缓存
     *
     * @param path
     * @param bitmap
     */
    void add(String path, Bitmap bitmap);
    /**
     * 获得缓存里面bitmap
     *
     * @param path
     * @return
     */
    Bitmap get(String path);
    /**
     * 清空缓存
     */
    void clear();
}
