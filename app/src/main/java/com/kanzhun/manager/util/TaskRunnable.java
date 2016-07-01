package com.kanzhun.manager.util;

import android.graphics.Bitmap;

import com.kanzhun.manager.ImageConfig;
import com.kanzhun.manager.bean.ImageInfo;
import com.kanzhun.manager.bean.ImageSizeInfo;
import com.kanzhun.manager.itfs.OnImageLoadCompleteListener;

/**
 * 作者：guofeng
 * ＊ 日期:16/7/1
 */
public class TaskRunnable implements Runnable {
    /**
     * 打包数据
     */
    private ImageInfo imageInfo;
    /**
     * 图片配置信息
     */
    private ImageConfig imageConfig;
    /**
     * 异步回调接口
     */
    private OnImageLoadCompleteListener listener;
    /**
     * 图片质量
     */
    private Bitmap.Config config;

    public void setOnImageLoadCompleteListener(OnImageLoadCompleteListener listener) {
        this.listener = listener;
    }

    public TaskRunnable(ImageInfo imageInfo, ImageConfig imageConfig, Bitmap.Config config) {
        this.imageInfo = imageInfo;
        this.imageConfig = imageConfig;
        this.config = config;
    }


    @Override
    public void run() {
        if (imageInfo == null) return;
        if (imageConfig == null) return;
        if (config == null) return;
        //判断图片是否可加载
        String tag = (String) imageInfo.imageView.getTag();
        String path = imageInfo.path;
        if (!tag.equals(path)) return;
        // 获得图片需要显示的大小
        ImageSizeInfo imageSizeInfo = ImageUtils.getImageViewSize(imageInfo.imageView);
        //加载本地bitmap
        Bitmap b = ImageUtils.decodeSampledBitmapFromPath(config, imageInfo.path, imageSizeInfo.width, imageSizeInfo.height);
        //设置打包数据bitmap
        imageInfo.bitmap = b;
        //回调主UI刷新
        if (listener != null) listener.onImageRefresh(imageInfo);
        //保存缓存
        if (b != null) {
            imageConfig.getmLruCache().add(imageInfo.path, b);
        }
    }
}
