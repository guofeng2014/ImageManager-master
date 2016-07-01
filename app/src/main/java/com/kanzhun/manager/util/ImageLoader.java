package com.kanzhun.manager.util;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import com.kanzhun.manager.ImageConfig;
import com.kanzhun.manager.bean.ImageInfo;
import com.kanzhun.manager.itfs.OnImageLoadCompleteListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouyou on 2016/6/24.
 */
public class ImageLoader implements OnImageLoadCompleteListener {
    /**
     * 用于存储ImageView和地址的对应关系
     */
    private final Map<ImageView, String> tagMap = new HashMap<>();
    /**
     * 当前实利
     */
    private static ImageLoader instance;
    /**
     * 配置信息
     */
    private ImageConfig config;


    public static ImageLoader get() {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoader();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化配置信息
     *
     * @param config
     */
    public void init(ImageConfig config) {
        this.config = config;
    }

    /**
     * 加载图片
     *
     * @param path      图片路径
     * @param imageView 图片控件
     */
    public void displayImage(String path, ImageView imageView) {
        if (config == null) throw new NullPointerException("请初始化ImageConfig");
        if (imageView == null) throw new NullPointerException("imageView不可为空");
        //加载默认图片
        imageView.setImageResource(config.getDefaultResourceId());
        //地址为空,加载默认图片
        if (TextUtils.isEmpty(path)) return;
        // 设置tag
        tagMap.put(imageView, path);
        //从缓存获取bitmap对象
        Bitmap b = config.getmLruCache().get(path);
        //打包数据
        ImageInfo imageInfo = new ImageInfo(b, imageView, path);
        //加载缓存bitmap
        if (b != null && !b.isRecycled()) {
            onImageRefresh(imageInfo);
            return;
        }
        //创建任务
        TaskRunnable imageRunnable = new TaskRunnable(imageInfo, config, tagMap, config.getImageConfig());
        imageRunnable.setOnImageLoadCompleteListener(this);
        //添加任务到线程池
        config.getThreadPool().execute(imageRunnable);
    }

    /**
     * 刷新主UI
     */
    private Handler mUIHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            ImageInfo imageInfo = (ImageInfo) msg.obj;
            Bitmap b = imageInfo.bitmap;
            ImageView iv = imageInfo.imageView;
            String tag = tagMap.get(iv);
            String path = imageInfo.path;
            if (TextUtils.equals(tag, path)) {
                //文件加载失败
                if (b == null) {
                    iv.setImageResource(config.getErrorResourceId());
                }
                //文件正常
                else {
                    iv.setImageBitmap(b);
                }
            }
            return true;
        }
    });

    /**
     * 发送ImageInfo数据到Handler，刷新UI统一入口
     *
     * @param imageInfo
     */
    @Override
    public void onImageRefresh(ImageInfo imageInfo) {
        Message message = Message.obtain();
        message.obj = imageInfo;
        mUIHandler.sendMessage(message);
    }

    /**
     * 释放内存
     */
    public void release() {
        tagMap.clear();
        config.getmLruCache().clear();
        System.gc();
    }

}
