package com.kanzhun.manager.util;

import android.graphics.Bitmap;

import com.kanzhun.manager.ImageConfig;
import com.kanzhun.manager.bean.ImageInfo;
import com.kanzhun.manager.bean.ImageSizeInfo;
import com.kanzhun.manager.itfs.OnImageLoadCompleteListener;

/**
 * Created by zhouyou on 2016/6/24.
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

    public void setOnImageLoadCompleteListener(OnImageLoadCompleteListener listener) {
        this.listener = listener;
    }

    public TaskRunnable(ImageInfo imageInfo, ImageConfig imageConfig) {
        this.imageInfo = imageInfo;
        this.imageConfig = imageConfig;
    }

    @Override
    public void run() {
        // 获得图片需要显示的大小
        ImageSizeInfo imageSizeInfo = ImageUtils.getImageViewSize(imageInfo.imageView);
        //加载本地bitmap
        Bitmap b = ImageUtils.decodeSampledBitmapFromPath(imageInfo.path, imageSizeInfo.width, imageSizeInfo.height);
        //设置打包数据bitmap
        imageInfo.bitmap = b;
        //释放信号量
        imageConfig.getSemaphore().release();
        //回调主UI刷新
        if (listener != null) listener.onImageRefresh(imageInfo);
        //保存缓存
        if (b != null) {
            imageConfig.getmLruCache().add(imageInfo.path, b);
        }
    }
}