package com.kanzhun.manager.bean;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by zhouyou on 2016/6/24.
 */
public class ImageInfo {
    /**
     * bitmap对象
     */
    public Bitmap bitmap;
    /**
     * 加载图片控件
     */
    public ImageView imageView;
    /**
     * 加载图片的路径
     */
    public String path;
    /**
     * 是否有动画
     */
    public boolean hasAnimation;

    public ImageInfo(Bitmap bitmap, ImageView imageView, String path, boolean hasAnimation) {
        this.bitmap = bitmap;
        this.imageView = imageView;
        this.path = path;
        this.hasAnimation = hasAnimation;
    }
}
